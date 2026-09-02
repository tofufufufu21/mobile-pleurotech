package com.example.pleurotech.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class ScanResult(
    val apiName: String,
    val displayName: String,
    val shortName: String,
    val description: String,
    val color: Color,
    val softColor: Color
) {
    ClassA(
        apiName = "Class_A",
        displayName = "Class A",
        shortName = "A",
        description = "premium",
        color = Color(0xFF4ADE80),
        softColor = Color(0x1F4ADE80)
    ),
    ClassB(
        apiName = "Class_B",
        displayName = "Class B",
        shortName = "B",
        description = "acceptable",
        color = Color(0xFFFBBF24),
        softColor = Color(0x1FFBBF24)
    ),
    Reject(
        apiName = "Reject",
        displayName = "Reject",
        shortName = "R",
        description = "remove now",
        color = Color(0xFFF87171),
        softColor = Color(0x1FF87171)
    )
}

data class ScanRecord(
    val id: Int,
    val bagNumber: Int,
    val result: ScanResult,
    val confidence: Float,
    val timestamp: LocalDateTime,
    val batchId: String = "BATCH-001",
    val shelfCode: String = "A1",
    val qrPayload: String = "pleurotech://scan/0",
    val aiExplanation: String = "",
    val verified: Boolean = false,
    val verifiedResult: ScanResult? = null,
    val photoPath: String? = null,
    val mushroomCount: Int = 1,
    val classBreakdown: String = ""
) {
    val timeLabel: String = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    val dateLabel: String = timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val finalResult: ScanResult = verifiedResult ?: result
}

data class ScanCounts(
    val classA: Int = 0,
    val classB: Int = 0,
    val reject: Int = 0
) {
    val total: Int = classA + classB + reject
    val premiumRate: Float = if (total == 0) 0f else classA * 100f / total
    val rejectRate: Float = if (total == 0) 0f else reject * 100f / total

    fun countFor(result: ScanResult): Int = when (result) {
        ScanResult.ClassA -> classA
        ScanResult.ClassB -> classB
        ScanResult.Reject -> reject
    }
}

data class DailyTrend(
    val date: LocalDate,
    val label: String,
    val classA: Int,
    val classB: Int,
    val reject: Int
) {
    val total: Int = classA + classB + reject
    val rejectPercent: Int = if (total == 0) 0 else ((reject * 100f) / total).toInt()
    val premiumPercent: Int = if (total == 0) 0 else ((classA * 100f) / total).toInt()
}

data class TrendInsights(
    val scanCount: Int,
    val averageConfidence: Float,
    val qualityScore: Float,
    val rejectRate: Float,
    val rejectRateChange: Float,
    val bestDay: DailyTrend?,
    val busiestDay: DailyTrend?,
    val today: DailyTrend?
) {
    val riskLevel: String = when {
        rejectRate >= 18f || rejectRateChange >= 5f -> "High"
        rejectRate >= 10f || rejectRateChange >= 2f -> "Watch"
        else -> "Stable"
    }

    val trendDirection: String = when {
        rejectRateChange > 1f -> "Increasing"
        rejectRateChange < -1f -> "Improving"
        else -> "Steady"
    }
}

data class HarvestForecast(
    val expectedScansTomorrow: Int,
    val expectedClassA: Int,
    val expectedClassB: Int,
    val expectedReject: Int,
    val projectedPremiumRate: Float,
    val projectedRejectRate: Float,
    val confidence: Float,
    val status: String
)

data class PreHarvestReflection(
    val readinessScore: Float,
    val readinessLabel: String,
    val observation: String,
    val action: String,
    val drivers: List<String>
)

data class MushroomBatch(
    val id: String,
    val name: String,
    val startedDate: LocalDate,
    val targetBagCount: Int,
    val rackCount: Int,
    val shelvesPerRack: Int
)

data class BagLabel(
    val batchId: String,
    val bagNumber: Int,
    val shelfCode: String,
    val qrPayload: String,
    val confirmQrPayload: String = ""
)

data class ShelfSummary(
    val shelfCode: String,
    val total: Int,
    val classA: Int,
    val classB: Int,
    val reject: Int
) {
    val rejectRate: Float = if (total == 0) 0f else reject * 100f / total
    val status: String = when {
        rejectRate >= 25f -> "Isolate"
        rejectRate >= 12f -> "Watch"
        total == 0 -> "Unscanned"
        else -> "Stable"
    }
}

data class ContaminationAlert(
    val title: String,
    val detail: String,
    val shelfCode: String?,
    val severity: String
)

data class ScanReport(
    val title: String,
    val summary: String,
    val rows: List<String>
)
