package com.example.pleurotech.data

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import com.example.pleurotech.data.auth.SupabaseAuthManager
import com.example.pleurotech.data.local.PleuroTechDbHelper
import com.example.pleurotech.data.sync.SupabaseSyncManager
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class PleuroTechRepository(
    val dbHelper: PleuroTechDbHelper? = null,
    val syncManager: SupabaseSyncManager? = null,
    val authManager: SupabaseAuthManager? = null,
    initialScans: List<ScanRecord>,
    initialBatches: List<MushroomBatch>
) {
    private var nextScanId = (initialScans.maxOfOrNull { it.id } ?: 0) + 1

    val batches = mutableStateListOf<MushroomBatch>().apply {
        addAll(initialBatches)
    }

    var activeBatchId by mutableStateOf(initialBatches.firstOrNull()?.id ?: "BATCH-001")

    val activeBatch: MushroomBatch
        get() = batches.firstOrNull { it.id == activeBatchId && !it.isArchived }
            ?: batches.firstOrNull { !it.isArchived }
            ?: batches.firstOrNull()
            ?: defaultBatch()

    fun setActiveBatch(batchId: String) {
        if (batches.any { it.id == batchId }) {
            activeBatchId = batchId
        }
    }

    fun archiveBatch(batchId: String, archive: Boolean) {
        val idx = batches.indexOfFirst { it.id == batchId }
        if (idx >= 0) {
            val updated = batches[idx].copy(isArchived = archive)
            batches[idx] = updated
            dbHelper?.archiveBatch(batchId, archive)
            if (activeBatchId == batchId && archive) {
                activeBatchId = batches.firstOrNull { !it.isArchived }?.id ?: batches.firstOrNull()?.id ?: "BATCH-001"
            }
        }
    }

    fun createBatch(
        name: String,
        targetBagCount: Int = 120,
        rackCount: Int = 4,
        shelvesPerRack: Int = 5
    ): MushroomBatch {
        val nextNum = batches.size + 1
        val batchId = "BATCH-${nextNum.toString().padStart(3, '0')}"
        val newBatch = MushroomBatch(
            id = batchId,
            name = name.ifBlank { "Oyster Batch ${nextNum.toString().padStart(3, '0')}" },
            startedDate = LocalDate.now(),
            targetBagCount = targetBagCount.coerceAtLeast(1),
            rackCount = rackCount.coerceIn(1, 26),
            shelvesPerRack = shelvesPerRack.coerceIn(1, 10),
            isArchived = false
        )
        batches.add(newBatch)
        activeBatchId = newBatch.id
        dbHelper?.insertOrUpdateBatch(newBatch)
        return newBatch
    }

    val scans = mutableStateListOf<ScanRecord>().apply {
        addAll(initialScans.sortedByDescending { it.id })
    }

    val latestScan: ScanRecord?
        get() = scans.maxByOrNull { it.id }

    fun counts(): ScanCounts {
        return ScanCounts(
            classA = scans.count { it.finalResult == ScanResult.ClassA },
            classB = scans.count { it.finalResult == ScanResult.ClassB },
            reject = scans.count { it.finalResult == ScanResult.Reject }
        )
    }

    fun recentScans(limit: Int = 10): List<ScanRecord> = scans.take(limit)

    fun history(page: Int, perPage: Int): List<ScanRecord> {
        val start = ((page - 1).coerceAtLeast(0)) * perPage
        return scans.drop(start).take(perPage)
    }

    fun trend(days: Int = 7): List<DailyTrend> {
        val today = LocalDateTime.now().toLocalDate()
        return (days - 1 downTo 0).map { offset ->
            val day = today.minusDays(offset.toLong())
            val dayRows = scans.filter { it.timestamp.toLocalDate() == day }
            DailyTrend(
                date = day,
                label = day.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
                classA = dayRows.count { it.finalResult == ScanResult.ClassA },
                classB = dayRows.count { it.finalResult == ScanResult.ClassB },
                reject = dayRows.count { it.finalResult == ScanResult.Reject }
            )
        }
    }

    fun insights(days: Int = 7): TrendInsights {
        val trend = trend(days)
        val includedDates = trend.map { it.date }.toSet()
        val windowRows = scans.filter { it.timestamp.toLocalDate() in includedDates }
        val counts = ScanCounts(
            classA = windowRows.count { it.finalResult == ScanResult.ClassA },
            classB = windowRows.count { it.finalResult == ScanResult.ClassB },
            reject = windowRows.count { it.finalResult == ScanResult.Reject }
        )
        val averageConfidence = if (windowRows.isEmpty()) {
            0f
        } else {
            windowRows.sumOf { it.confidence.toDouble() }.toFloat() / windowRows.size
        }
        val firstHalf = trend.take(days / 2)
        val secondHalf = trend.drop(days / 2)
        val firstRejectRate = rejectRateFor(firstHalf)
        val secondRejectRate = rejectRateFor(secondHalf)
        val qualityScore = if (windowRows.isEmpty()) {
            0f
        } else {
            (counts.premiumRate * 0.7f + (100f - counts.rejectRate) * 0.3f).coerceIn(0f, 100f)
        }

        return TrendInsights(
            scanCount = windowRows.size,
            averageConfidence = averageConfidence,
            qualityScore = qualityScore,
            rejectRate = counts.rejectRate,
            rejectRateChange = secondRejectRate - firstRejectRate,
            bestDay = trend.filter { it.total > 0 }.maxByOrNull { it.premiumPercent - it.rejectPercent },
            busiestDay = trend.maxByOrNull { it.total },
            today = trend.lastOrNull()
        )
    }

    fun harvestForecast(days: Int = 7): HarvestForecast {
        val trend = trend(days).filter { it.total > 0 }
        val insights = insights(days)
        val counts = ScanCounts(
            classA = trend.sumOf { it.classA },
            classB = trend.sumOf { it.classB },
            reject = trend.sumOf { it.reject }
        )
        val averageDailyScans = if (trend.isEmpty()) 0f else trend.sumOf { it.total } / trend.size.toFloat()
        val momentum = trend.takeLast(3).let { recentDays ->
            if (recentDays.isEmpty()) 0f else recentDays.sumOf { it.total } / recentDays.size.toFloat() - averageDailyScans
        }
        val projectedTotal = (averageDailyScans + momentum * 0.35f).coerceAtLeast(0f).toInt()
        val projectedClassA = (projectedTotal * counts.premiumRate / 100f).toInt()
        val projectedReject = (projectedTotal * counts.rejectRate / 100f).toInt()
        val projectedClassB = (projectedTotal - projectedClassA - projectedReject).coerceAtLeast(0)
        val confidence = when {
            trend.size >= 6 && insights.averageConfidence >= 0.88f -> 0.92f
            trend.size >= 4 && insights.averageConfidence >= 0.82f -> 0.84f
            trend.isNotEmpty() -> 0.72f
            else -> 0.0f
        }
        val status = when {
            projectedTotal == 0 -> "Needs scan data"
            counts.rejectRate >= 18f || insights.rejectRateChange >= 5f -> "High reject pressure"
            counts.premiumRate >= 70f && insights.trendDirection != "Increasing" -> "Strong premium outlook"
            counts.rejectRate >= 10f -> "Watch tomorrow's batch"
            else -> "Balanced output expected"
        }

        // Agronomic yield calculations for Oyster Mushrooms (Pleurotus ostreatus):
        // Standard substrate bag (1.0 - 1.2 kg wet substrate) yields per flush:
        // Class A (prime clusters): ~0.200 kg (200g)
        // Class B (market/processing grade): ~0.140 kg (140g)
        val classAKg = projectedClassA * 0.200f
        val classBKg = projectedClassB * 0.140f
        val totalKg = classAKg + classBKg

        // Market benchmark pricing in Philippines (₱):
        // Class A fresh table mushrooms: ₱250 / kg
        // Class B processing / wet market grade: ₱160 / kg
        val revClassA = classAKg * 250f
        val revClassB = classBKg * 160f
        val totalRevenue = revClassA + revClassB

        // Loss prevention: uncollected yield from diseased/rejected bags
        val potentialLoss = projectedReject * 0.180f * 220f

        // Estimated Biological Efficiency (B.E.) per flush (benchmark 15-25%):
        val avgBags = if (projectedTotal > 0) projectedTotal else 1
        val bePercent = if (projectedTotal > 0) ((totalKg / (avgBags * 1.0f)) * 100f).coerceIn(0f, 35f) else 0f

        // Optimal harvest window timing:
        val harvestWindow = if (insights.rejectRateChange >= 5f) {
            "Priority Dawn (5:30 AM – 7:30 AM)"
        } else {
            "Early Morning (6:00 AM – 9:00 AM)"
        }

        // Flush cycle status:
        val flushStage = when {
            momentum > 4f -> "1st Flush Peak (High Cluster Density)"
            momentum < -4f -> "Inter-Flush Resting (Preparing Flush 2)"
            counts.premiumRate >= 75f -> "Prime Fruiting (Optimal Cap Thickness)"
            projectedTotal == 0 -> "Pre-Harvest Incubation"
            else -> "Active Flush Fruiting"
        }

        // Multi-day harvest planning horizon (Tomorrow, Day +2, Day +3):
        val day1 = DailyYieldProjection(
            dayLabel = "Tomorrow",
            expectedBags = projectedTotal,
            classA = projectedClassA,
            classB = projectedClassB,
            estimatedKg = totalKg,
            estimatedRevenuePhp = totalRevenue
        )
        val day2Bags = (projectedTotal * 0.85f).toInt()
        val day2ClassA = (day2Bags * (counts.premiumRate / 100f)).toInt()
        val day2ClassB = (day2Bags - day2ClassA).coerceAtLeast(0)
        val day2Kg = day2ClassA * 0.200f + day2ClassB * 0.140f
        val day2 = DailyYieldProjection(
            dayLabel = "+2 Days",
            expectedBags = day2Bags,
            classA = day2ClassA,
            classB = day2ClassB,
            estimatedKg = day2Kg,
            estimatedRevenuePhp = day2ClassA * 250f + day2ClassB * 160f
        )
        val day3Bags = (projectedTotal * 0.65f).toInt()
        val day3ClassA = (day3Bags * (counts.premiumRate / 100f)).toInt()
        val day3ClassB = (day3Bags - day3ClassA).coerceAtLeast(0)
        val day3Kg = day3ClassA * 0.200f + day3ClassB * 0.140f
        val day3 = DailyYieldProjection(
            dayLabel = "+3 Days",
            expectedBags = day3Bags,
            classA = day3ClassA,
            classB = day3ClassB,
            estimatedKg = day3Kg,
            estimatedRevenuePhp = day3ClassA * 250f + day3ClassB * 160f
        )

        // Practical farmer agronomic tips:
        val actionTips = mutableListOf<HarvestActionTip>()
        actionTips.add(
            HarvestActionTip(
                category = "Harvest Technique",
                title = "Base Twist-and-Pull",
                description = "Gently rock the oyster cluster and twist from the bag neck. Never cut stems with dull knives that leave necrotic stumps inviting green mold.",
                iconType = "harvest"
            )
        )
        actionTips.add(
            HarvestActionTip(
                category = "Moisture Control",
                title = "Cease Misting 4 Hours Prior",
                description = "Turn off overhead foggers/sprayers 4-6 hours before picking. Saturated gills trap water and accelerate bacterial browning during transit.",
                iconType = "water"
            )
        )
        actionTips.add(
            HarvestActionTip(
                category = "Cold-Chain Preservation",
                title = "Chill to 4°C–7°C within 1 Hour",
                description = "Pack into perforated crates or brown kraft paper. Rapid pre-cooling extends oyster mushroom shelf life from 2 days to over 7 days.",
                iconType = "cold"
            )
        )
        if (projectedReject > 0) {
            actionTips.add(
                HarvestActionTip(
                    category = "Biosecurity & Loss Prevention",
                    title = "Isolate $projectedReject Contaminated Bags",
                    description = "Remove infected bags into sealed sacks immediately to prevent Trichoderma and Cobweb mold spores from aerosolizing to neighboring racks.",
                    iconType = "warning"
                )
            )
        }

        return HarvestForecast(
            expectedScansTomorrow = projectedTotal,
            expectedClassA = projectedClassA,
            expectedClassB = projectedClassB,
            expectedReject = projectedReject,
            projectedPremiumRate = counts.premiumRate,
            projectedRejectRate = counts.rejectRate,
            confidence = confidence,
            status = status,
            estimatedWeightKg = totalKg,
            classAWeightKg = classAKg,
            classBWeightKg = classBKg,
            projectedRevenuePhp = totalRevenue,
            potentialLossPhp = potentialLoss,
            biologicalEfficiencyPercent = bePercent,
            optimalHarvestWindow = harvestWindow,
            flushCycleStage = flushStage,
            climateYieldFactor = 1.0f,
            climateYieldNote = "Maintained within 80-88% optimal fruiting humidity",
            projections = listOf(day1, day2, day3),
            actionTips = actionTips
        )
    }

    fun preHarvestReflection(days: Int = 7): PreHarvestReflection {
        val insights = insights(days)
        val forecast = harvestForecast(days)
        val readinessScore = if (insights.scanCount == 0) {
            0f
        } else {
            (
                insights.qualityScore * 0.55f +
                    forecast.confidence * 100f * 0.25f +
                    (100f - forecast.projectedRejectRate).coerceIn(0f, 100f) * 0.20f
            ).coerceIn(0f, 100f)
        }
        val label = when {
            insights.scanCount == 0 -> "Awaiting Scans"
            readinessScore >= 85f -> "Ready to harvest"
            readinessScore >= 70f -> "Proceed with checks"
            readinessScore >= 55f -> "Delay and inspect"
            else -> "Hold batch"
        }
        val observation = when {
            insights.scanCount == 0 -> "No scans recorded yet. Start by scanning bags to generate AI harvest readiness insights."
            insights.rejectRateChange >= 5f -> "Reject pressure is rising across the latest scan window."
            insights.averageConfidence < 0.82f -> "Model confidence is below the preferred inspection band."
            forecast.projectedPremiumRate >= 70f -> "Recent grading pattern favors premium output."
            else -> "The batch is mixed and should be reviewed before final harvest decisions."
        }
        val action = when (label) {
            "Ready to harvest" -> "Prepare crates and continue spot checks during collection."
            "Proceed with checks" -> "Harvest selectively and rescan lower-confidence bags first."
            "Delay and inspect" -> "Inspect humidity, cap maturity, and any rejected shelf zones before harvest."
            "Awaiting Scans" -> "Perform initial bag scans to begin automated batch monitoring."
            else -> "Pause harvest and isolate suspect bags until reject pressure improves."
        }
        val drivers = listOf(
            "${insights.scanCount} scans in the review window",
            "${"%.1f".format(forecast.projectedPremiumRate)}% projected Class A",
            "${"%.1f".format(forecast.projectedRejectRate)}% projected rejects",
            "${"%.0f".format(forecast.confidence * 100)}% forecast confidence"
        )

        return PreHarvestReflection(
            readinessScore = readinessScore,
            readinessLabel = label,
            observation = observation,
            action = action,
            drivers = drivers
        )
    }

    private fun rejectRateFor(days: List<DailyTrend>): Float {
        val total = days.sumOf { it.total }
        if (total == 0) return 0f
        return days.sumOf { it.reject } * 100f / total
    }

    fun addScan(
        bagNumber: Int,
        result: ScanResult,
        confidence: Float,
        timestamp: LocalDateTime = LocalDateTime.now(),
        batchId: String = activeBatch.id,
        shelfCode: String = shelfCodeForBag(bagNumber),
        customExplanation: String? = null,
        photoPath: String? = null,
        mushroomCount: Int = 1,
        classBreakdown: String = ""
    ) {
        val id = nextScanId++
        val record = ScanRecord(
            id = id,
            bagNumber = bagNumber,
            result = result,
            confidence = confidence.coerceIn(0f, 1f),
            timestamp = timestamp,
            batchId = batchId,
            shelfCode = shelfCode,
            qrPayload = bagQrPayload(batchId, bagNumber),
            aiExplanation = customExplanation ?: explanationFor(result, confidence, shelfCode),
            photoPath = photoPath,
            mushroomCount = mushroomCount,
            classBreakdown = classBreakdown
        )
        scans.add(0, record)
        dbHelper?.insertScan(record, PleuroTechDbHelper.SYNC_PENDING)
    }

    fun scansForShelf(shelfCode: String): List<ScanRecord> {
        return scans.filter { it.shelfCode.equals(shelfCode, ignoreCase = true) }
    }

    fun latestScanForBag(bagNumber: Int): ScanRecord? {
        return scans.firstOrNull { it.bagNumber == bagNumber }
    }


    fun clear(context: Context? = null) {
        scans.clear()
        nextScanId = 1
        dbHelper?.clearAll()
        batches.clear()
        val freshBatch = defaultBatch()
        batches.add(freshBatch)
        dbHelper?.insertOrUpdateBatch(freshBatch)
        if (context != null) {
            try {
                val dir = java.io.File(context.filesDir, "scans")
                if (dir.exists()) dir.deleteRecursively()
            } catch (_: Exception) {}
        }
    }

    fun verifyScan(scanId: Int, result: ScanResult) {
        val index = scans.indexOfFirst { it.id == scanId }
        if (index >= 0) {
            scans[index] = scans[index].copy(
                verified = true,
                verifiedResult = result
            )
            dbHelper?.updateScanResult(scanId, result)
        }
    }

    fun addRackToActiveBatch(): MushroomBatch {
        val current = activeBatch
        val newRackCount = (current.rackCount + 1).coerceAtMost(26)
        val updated = current.copy(
            rackCount = newRackCount,
            targetBagCount = current.targetBagCount + (current.shelvesPerRack * 5)
        )
        val idx = batches.indexOfFirst { it.id == current.id }
        if (idx >= 0) {
            batches[idx] = updated
        } else {
            batches.add(updated)
        }
        dbHelper?.insertOrUpdateBatch(updated)
        return updated
    }

    fun removeLastRackFromActiveBatch(): MushroomBatch? {
        val current = activeBatch
        if (current.rackCount <= 1) return null
        val newRackCount = current.rackCount - 1
        val updated = current.copy(
            rackCount = newRackCount,
            targetBagCount = (current.targetBagCount - (current.shelvesPerRack * 5)).coerceAtLeast(1)
        )
        val idx = batches.indexOfFirst { it.id == current.id }
        if (idx >= 0) {
            batches[idx] = updated
        } else {
            batches.add(updated)
        }
        dbHelper?.insertOrUpdateBatch(updated)
        return updated
    }

    fun refreshFromDb() {
        dbHelper?.let { db ->
            val fresh = db.getAllScans()
            scans.clear()
            scans.addAll(fresh)
            nextScanId = (fresh.maxOfOrNull { it.id } ?: 0) + 1
        }
    }

    fun shelfMap(): List<ShelfSummary> {
        val batch = activeBatch
        return (1..batch.rackCount).flatMap { rack ->
            (1..batch.shelvesPerRack).map { shelf ->
                val code = "${('A'.code + rack - 1).toChar()}$shelf"
                val rows = scans.filter { it.shelfCode == code && it.batchId == batch.id }
                ShelfSummary(
                    shelfCode = code,
                    total = rows.size,
                    classA = rows.count { it.finalResult == ScanResult.ClassA },
                    classB = rows.count { it.finalResult == ScanResult.ClassB },
                    reject = rows.count { it.finalResult == ScanResult.Reject }
                )
            }
        }
    }

    fun contaminationAlerts(): List<ContaminationAlert> {
        val shelfAlerts = shelfMap()
            .filter { it.total >= 3 && it.rejectRate >= 20f }
            .map {
                ContaminationAlert(
                    title = "Possible contamination cluster",
                    detail = "${it.shelfCode} has ${it.reject} rejects from ${it.total} scans. Isolate and inspect this shelf before harvest.",
                    shelfCode = it.shelfCode,
                    severity = if (it.rejectRate >= 30f) "High" else "Watch"
                )
            }
        val latest = latestScan
        val latestAlert = if (latest?.finalResult == ScanResult.Reject) {
            listOf(
                ContaminationAlert(
                    title = "Latest bag needs isolation",
                    detail = "Bag ${latest.bagNumber} on shelf ${latest.shelfCode} was rejected by the latest scan.",
                    shelfCode = latest.shelfCode,
                    severity = "High"
                )
            )
        } else {
            emptyList()
        }
        return (latestAlert + shelfAlerts).distinctBy { it.title + it.shelfCode }.take(4)
    }

    fun bagLabels(startBag: Int = 1, limit: Int = activeBatch.targetBagCount): List<BagLabel> {
        val batch = activeBatch
        val start = startBag.coerceIn(1, batch.targetBagCount.coerceAtLeast(1))
        val maxLimit = (batch.targetBagCount.coerceAtLeast(1) - start + 1).coerceAtLeast(1)
        val actualLimit = limit.coerceIn(1, maxLimit)
        val end = start + actualLimit - 1
        return (start..end).map { bagNumber ->
            BagLabel(
                batchId = batch.id,
                bagNumber = bagNumber,
                shelfCode = shelfCodeForBag(bagNumber),
                qrPayload = bagQrPayload(batch.id, bagNumber, isConfirm = false),
                confirmQrPayload = bagQrPayload(batch.id, bagNumber, isConfirm = true)
            )
        }
    }

    fun report(): ScanReport {
        val counts = counts()
        val reflection = preHarvestReflection()
        val forecast = harvestForecast()
        val rows = listOf(
            "Active batch: ${activeBatch.name} (${activeBatch.id})",
            "Scans: ${counts.total}",
            "Class A: ${counts.classA} | Class B: ${counts.classB} | Reject: ${counts.reject}",
            "Tomorrow forecast: ${forecast.expectedScansTomorrow} scans, ${forecast.expectedClassA} Class A, ${forecast.expectedReject} rejects",
            "Pre-harvest readiness: ${reflection.readinessLabel} (${reflection.readinessScore.toInt()})",
            "Verified records: ${scans.count { it.verified }}"
        )
        return ScanReport(
            title = "Oyster Mushroom Quality Report",
            summary = reflection.action,
            rows = rows
        )
    }

    fun exportCsv(): String {
        val rows = scans.sortedByDescending { it.id }.map {
            "${it.id},${it.batchId},${it.shelfCode},${it.bagNumber},${it.result.apiName},${it.finalResult.apiName},${it.verified},${"%.2f".format(it.confidence)},${it.qrPayload},${it.timestamp}"
        }
        return listOf("id,batch_id,shelf_code,bag_num,ai_result,final_result,verified,confidence,qr_payload,timestamp").plus(rows).joinToString("\n")
    }

    fun shareCsv(context: Context) {
        try {
            val csvData = exportCsv()
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, "pleurotech_harvest_scans_${System.currentTimeMillis()}.csv")
            file.writeText(csvData)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "PleuroTech Oyster Mushroom Scans Export")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Scans CSV").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bagQrPayload(batchId: String, bagNumber: Int, isConfirm: Boolean = false): String {
        val typeSuffix = if (isConfirm) "/confirm" else "/id"
        return "pleurotech://batch/$batchId/bag/${bagNumber.toString().padStart(3, '0')}$typeSuffix"
    }

    fun shelfCodeForBag(bagNumber: Int): String {
        val totalShelves = (activeBatch.rackCount * activeBatch.shelvesPerRack).coerceAtLeast(1)
        val bagsPerShelf = (activeBatch.targetBagCount / totalShelves).coerceAtLeast(1)
        val bagsPerRack = (activeBatch.shelvesPerRack * bagsPerShelf).coerceAtLeast(1)
        val rackIndex = ((bagNumber - 1) / bagsPerRack).coerceIn(0, activeBatch.rackCount - 1)
        val shelfIndex = ((((bagNumber - 1) % bagsPerRack) / bagsPerShelf) % activeBatch.shelvesPerRack) + 1
        val rackChar = ('A'.code + rackIndex).toChar()
        return "$rackChar$shelfIndex"
    }

    private fun explanationFor(result: ScanResult, confidence: Float, shelfCode: String): String {
        val confidenceText = "${"%.0f".format(confidence * 100)}% confidence"
        return when (result) {
            ScanResult.ClassA -> "AI found uniform oyster mushroom growth, clean cap structure, and low defect risk on shelf $shelfCode with $confidenceText."
            ScanResult.ClassB -> "AI detected acceptable growth with minor quality variation. Review cap size and bag surface before premium sorting."
            ScanResult.Reject -> "AI detected visual risk patterns that may indicate disease, contamination, or poor fruiting quality. Isolate this bag for manual inspection."
        }
    }

    companion object {
        fun create(context: Context): PleuroTechRepository {
            val db = PleuroTechDbHelper(context)
            val sync = SupabaseSyncManager(context, db)
            val auth = SupabaseAuthManager(db)

            // Ensure a clean default batch exists if table is empty
            if (db.getAllBatches().isEmpty()) {
                val defaultB = defaultBatch()
                db.insertOrUpdateBatch(defaultB)
            }

            val batches = db.getAllBatches().ifEmpty { listOf(defaultBatch()) }
            val scans = db.getAllScans()

            return PleuroTechRepository(
                dbHelper = db,
                syncManager = sync,
                authManager = auth,
                initialScans = scans,
                initialBatches = batches
            )
        }

        fun empty(): PleuroTechRepository = PleuroTechRepository(
            dbHelper = null,
            syncManager = null,
            initialScans = emptyList(),
            initialBatches = listOf(defaultBatch())
        )

        fun isOnboardingCompleted(context: Context): Boolean {
            val prefs = context.getSharedPreferences("pleurotech_prefs", Context.MODE_PRIVATE)
            return prefs.getBoolean("has_completed_onboarding", false)
        }

        fun setOnboardingCompleted(context: Context, completed: Boolean) {
            val prefs = context.getSharedPreferences("pleurotech_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("has_completed_onboarding", completed).apply()
        }

        private fun defaultBatch(): MushroomBatch = MushroomBatch(
            id = "BATCH-001",
            name = "Oyster Batch 001",
            startedDate = LocalDate.now(),
            targetBagCount = 120,
            rackCount = 4,
            shelvesPerRack = 5
        )
    }
}
