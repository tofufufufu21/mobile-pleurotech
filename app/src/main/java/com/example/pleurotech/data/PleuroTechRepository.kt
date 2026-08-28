package com.example.pleurotech.data

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class PleuroTechRepository private constructor(
    initialScans: List<ScanRecord>,
    initialBatches: List<MushroomBatch>
) {
    private var nextScanId = (initialScans.maxOfOrNull { it.id } ?: 0) + 1

    val batches = mutableStateListOf<MushroomBatch>().apply {
        addAll(initialBatches)
    }

    val activeBatch: MushroomBatch
        get() = batches.firstOrNull() ?: defaultBatch()

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
        val qualityScore = (
            counts.premiumRate * 0.7f +
                (100f - counts.rejectRate) * 0.3f
            ).coerceIn(0f, 100f)

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
            else -> 0.35f
        }
        val status = when {
            projectedTotal == 0 -> "Needs scan data"
            counts.rejectRate >= 18f || insights.rejectRateChange >= 5f -> "High reject pressure"
            counts.premiumRate >= 70f && insights.trendDirection != "Increasing" -> "Strong premium outlook"
            counts.rejectRate >= 10f -> "Watch tomorrow's batch"
            else -> "Balanced output expected"
        }

        return HarvestForecast(
            expectedScansTomorrow = projectedTotal,
            expectedClassA = projectedClassA,
            expectedClassB = projectedClassB,
            expectedReject = projectedReject,
            projectedPremiumRate = counts.premiumRate,
            projectedRejectRate = counts.rejectRate,
            confidence = confidence,
            status = status
        )
    }

    fun preHarvestReflection(days: Int = 7): PreHarvestReflection {
        val insights = insights(days)
        val forecast = harvestForecast(days)
        val readinessScore = (
            insights.qualityScore * 0.55f +
                forecast.confidence * 100f * 0.25f +
                (100f - forecast.projectedRejectRate).coerceIn(0f, 100f) * 0.20f
            ).coerceIn(0f, 100f)
        val label = when {
            readinessScore >= 85f -> "Ready to harvest"
            readinessScore >= 70f -> "Proceed with checks"
            readinessScore >= 55f -> "Delay and inspect"
            else -> "Hold batch"
        }
        val observation = when {
            insights.scanCount == 0 -> "No recent scans are available, so readiness is based on missing evidence."
            insights.rejectRateChange >= 5f -> "Reject pressure is rising across the latest scan window."
            insights.averageConfidence < 0.82f -> "Model confidence is below the preferred inspection band."
            forecast.projectedPremiumRate >= 70f -> "Recent grading pattern favors premium output."
            else -> "The batch is mixed and should be reviewed before final harvest decisions."
        }
        val action = when (label) {
            "Ready to harvest" -> "Prepare crates and continue spot checks during collection."
            "Proceed with checks" -> "Harvest selectively and rescan lower-confidence bags first."
            "Delay and inspect" -> "Inspect humidity, cap maturity, and any rejected shelf zones before harvest."
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
        shelfCode: String = shelfCodeForBag(bagNumber)
    ) {
        val id = nextScanId++
        scans.add(
            0,
            ScanRecord(
                id = id,
                bagNumber = bagNumber,
                result = result,
                confidence = confidence.coerceIn(0f, 1f),
                timestamp = timestamp,
                batchId = batchId,
                shelfCode = shelfCode,
                qrPayload = bagQrPayload(batchId, bagNumber),
                aiExplanation = explanationFor(result, confidence, shelfCode)
            )
        )
    }

    fun addMockScan() {
        val weighted = listOf(
            ScanResult.ClassA,
            ScanResult.ClassA,
            ScanResult.ClassA,
            ScanResult.ClassB,
            ScanResult.ClassB,
            ScanResult.Reject
        )
        addScan(
            bagNumber = Random.nextInt(1, 21),
            result = weighted.random(),
            confidence = Random.nextDouble(0.75, 0.99).toFloat()
        )
    }

    fun clear() {
        scans.clear()
    }

    fun verifyScan(scanId: Int, result: ScanResult) {
        val index = scans.indexOfFirst { it.id == scanId }
        if (index >= 0) {
            scans[index] = scans[index].copy(
                verified = true,
                verifiedResult = result
            )
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

    fun bagLabels(limit: Int = activeBatch.targetBagCount): List<BagLabel> {
        val batch = activeBatch
        return (1..batch.targetBagCount.coerceAtLeast(1)).take(limit).map { bagNumber ->
            BagLabel(
                batchId = batch.id,
                bagNumber = bagNumber,
                shelfCode = shelfCodeForBag(bagNumber),
                qrPayload = bagQrPayload(batch.id, bagNumber)
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

    private fun bagQrPayload(batchId: String, bagNumber: Int): String {
        return "pleurotech://batch/$batchId/bag/${bagNumber.toString().padStart(3, '0')}"
    }

    private fun shelfCodeForBag(bagNumber: Int): String {
        val rack = ((bagNumber - 1).floorDiv(5)).coerceIn(0, activeBatch.rackCount - 1)
        val shelf = ((bagNumber - 1) % activeBatch.shelvesPerRack) + 1
        return "${('A'.code + rack).toChar()}$shelf"
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
        fun empty(): PleuroTechRepository = PleuroTechRepository(emptyList(), listOf(defaultBatch()))

        fun seeded(): PleuroTechRepository {
            val now = LocalDateTime.now()
            var scanId = 1
            val results = listOf(
                ScanResult.ClassA,
                ScanResult.ClassA,
                ScanResult.ClassA,
                ScanResult.ClassB,
                ScanResult.ClassB,
                ScanResult.Reject
            )
            val scans = mutableListOf<ScanRecord>()

            for (dayOffset in 13 downTo 0) {
                val day = now.minusDays(dayOffset.toLong())
                repeat(Random.nextInt(20, 36)) {
                    val result = results.random()
                    scans += ScanRecord(
                        id = scanId++,
                        bagNumber = Random.nextInt(1, 21),
                        result = result,
                        confidence = Random.nextDouble(0.75, 0.99).toFloat(),
                        timestamp = day.withHour(Random.nextInt(6, 19)).withMinute(Random.nextInt(0, 60)).withSecond(0),
                        batchId = defaultBatch().id,
                        shelfCode = "A1",
                        qrPayload = "pleurotech://batch/${defaultBatch().id}/bag/${(scanId - 1).toString().padStart(3, '0')}"
                    )
                }
            }

            val enrichedScans = scans.map {
                it.copy(
                    shelfCode = shelfForSeed(it.bagNumber),
                    qrPayload = "pleurotech://batch/${it.batchId}/bag/${it.bagNumber.toString().padStart(3, '0')}",
                    aiExplanation = when (it.result) {
                        ScanResult.ClassA -> "AI found uniform oyster mushroom growth, clean cap structure, and low defect risk on shelf ${shelfForSeed(it.bagNumber)} with ${"%.0f".format(it.confidence * 100)}% confidence."
                        ScanResult.ClassB -> "AI detected acceptable oyster mushroom growth with minor quality variation. Review cap size before premium sorting."
                        ScanResult.Reject -> "AI detected visual risk patterns that may indicate contamination or poor fruiting quality. Isolate this bag for manual inspection."
                    }
                )
            }

            return PleuroTechRepository(enrichedScans, listOf(defaultBatch()))
        }

        private fun defaultBatch(): MushroomBatch = MushroomBatch(
            id = "BATCH-001",
            name = "Oyster Batch 001",
            startedDate = LocalDate.now().minusDays(12),
            targetBagCount = 120,
            rackCount = 4,
            shelvesPerRack = 5
        )

        private fun shelfForSeed(bagNumber: Int): String {
            val rack = ((bagNumber - 1).floorDiv(5)).coerceIn(0, 3)
            val shelf = ((bagNumber - 1) % 5) + 1
            return "${('A'.code + rack).toChar()}$shelf"
        }
    }
}
