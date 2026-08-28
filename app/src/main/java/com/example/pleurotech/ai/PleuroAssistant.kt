package com.example.pleurotech.ai

import com.example.pleurotech.data.PleuroTechRepository
import com.example.pleurotech.data.ScanResult
import java.time.LocalTime

data class AssistantMessage(
    val sender: MessageSender,
    val text: String
)

enum class MessageSender {
    User,
    Assistant
}

data class AssistantSuggestion(
    val title: String,
    val prompt: String
)

enum class AssistantPriority {
    Stable,
    Watch,
    Critical
}

data class AiRecommendation(
    val title: String,
    val detail: String,
    val priority: AssistantPriority
)

data class AiBrief(
    val headline: String,
    val summary: String,
    val priority: AssistantPriority,
    val confidenceLabel: String,
    val recommendations: List<AiRecommendation>,
    val anomalySignals: List<String>
)

class PleuroAssistant {
    fun brief(repository: PleuroTechRepository): AiBrief {
        val insights = repository.insights(7)
        val latest = repository.latestScan
        val counts = repository.counts()
        val priority = when (insights.riskLevel) {
            "High" -> AssistantPriority.Critical
            "Watch" -> AssistantPriority.Watch
            else -> AssistantPriority.Stable
        }
        val latestSignal = latest?.let {
            "Latest bag ${it.bagNumber}: ${it.finalResult.displayName}, ${"%.0f".format(it.confidence * 100)}% model confidence"
        } ?: "No active scan yet"
        val confidenceSignal = when {
            insights.averageConfidence == 0f -> "Waiting for scan confidence"
            insights.averageConfidence < 0.82f -> "Confidence drift: review lighting and focus"
            else -> "Model confidence stable at ${"%.0f".format(insights.averageConfidence * 100)}%"
        }
        val rejectSignal = when {
            insights.rejectRate >= 18f -> "Reject rate above operating limit"
            insights.rejectRateChange >= 5f -> "Reject rate accelerating this week"
            insights.rejectRate >= 10f -> "Reject rate needs monitoring"
            else -> "Reject rate inside normal range"
        }

        return AiBrief(
            headline = when (priority) {
                AssistantPriority.Critical -> "Intervention recommended"
                AssistantPriority.Watch -> "Monitor next scans"
                AssistantPriority.Stable -> "Batch quality stable"
            },
            summary = "AI reviewed ${insights.scanCount} recent scans, ${counts.total} total records, and the current reject trend.",
            priority = priority,
            confidenceLabel = "${insights.qualityScore.toInt()} quality score",
            recommendations = recommendations(repository),
            anomalySignals = listOf(latestSignal, confidenceSignal, rejectSignal)
        )
    }

    fun welcome(repository: PleuroTechRepository): String {
        val brief = brief(repository)
        return buildString {
            append("${brief.headline}. ")
            append("${brief.confidenceLabel}. ")
            append(brief.recommendations.firstOrNull()?.detail ?: "Continue routine scanning.")
        }
    }

    fun answer(prompt: String, repository: PleuroTechRepository): String {
        val normalized = prompt.lowercase()
        val counts = repository.counts()
        val insights = repository.insights(7)
        val latest = repository.latestScan

        return when {
            normalized.contains("latest") || normalized.contains("last scan") -> {
                if (latest == null) {
                    "There are no scan records yet. Run a demo scan from the Scan tab to create the first result."
                } else {
                    "Latest scan: Bag ${latest.bagNumber} was classified as ${latest.finalResult.displayName} with ${"%.0f".format(latest.confidence * 100)}% confidence at ${latest.timeLabel}. QR access: ${latest.qrPayload}."
                }
            }
            normalized.contains("reject") || normalized.contains("risk") -> {
                val signals = brief(repository).anomalySignals.joinToString(separator = " ")
                "Reject risk is ${insights.riskLevel.lowercase()}. Current reject rate is ${"%.1f".format(insights.rejectRate)}%, and the recent trend is ${insights.trendDirection.lowercase()} by ${signedPercent(insights.rejectRateChange)}. $signals."
            }
            normalized.contains("improve") || normalized.contains("recommend") || normalized.contains("advice") -> {
                recommendations(repository).joinToString(separator = "\n") { "${it.title}: ${it.detail}" }
            }
            normalized.contains("action") || normalized.contains("next") -> {
                recommendations(repository).take(2).joinToString(separator = "\n") { "${it.title}: ${it.detail}" }
            }
            normalized.contains("summary") || normalized.contains("today") || normalized.contains("performance") -> {
                "Today has ${insights.today?.total ?: 0} scans. Across the selected pattern, the app recorded ${counts.classA} Class A, ${counts.classB} Class B, and ${counts.reject} rejects, with an average confidence of ${"%.0f".format(insights.averageConfidence * 100)}%."
            }
            normalized.contains("class a") || normalized.contains("premium") -> {
                "Premium rate is ${"%.1f".format(counts.premiumRate)}%. Best recent day is ${insights.bestDay?.label ?: "not available"} with ${insights.bestDay?.premiumPercent ?: 0}% Class A output."
            }
            else -> {
                "I can help summarize scan performance, explain the latest result, check reject risk, or suggest quality actions. Current status: ${insights.riskLevel}, quality score ${insights.qualityScore.toInt()}."
            }
        }
    }

    fun suggestions(): List<AssistantSuggestion> = listOf(
        AssistantSuggestion("Summarize", "Summarize today performance"),
        AssistantSuggestion("Risk", "Check reject risk"),
        AssistantSuggestion("Latest", "Explain latest scan"),
        AssistantSuggestion("Actions", "What should I do next?"),
        AssistantSuggestion("Advice", "Recommend actions")
    )

    fun recommendations(repository: PleuroTechRepository): List<AiRecommendation> {
        val insights = repository.insights(7)
        val latest = repository.latestScan
        val basePriority = when (insights.riskLevel) {
            "High" -> AssistantPriority.Critical
            "Watch" -> AssistantPriority.Watch
            else -> AssistantPriority.Stable
        }
        val first = when (latest?.finalResult) {
            ScanResult.Reject -> AiRecommendation(
                title = "Isolate latest reject",
                detail = "Move bag ${latest.bagNumber} out of the accepted batch and rescan adjacent bags from the same shelf.",
                priority = AssistantPriority.Critical
            )
            ScanResult.ClassB -> AiRecommendation(
                title = "Review borderline quality",
                detail = "Keep bag ${latest.bagNumber} available for manual check and compare color, cap shape, and substrate exposure.",
                priority = AssistantPriority.Watch
            )
            ScanResult.ClassA -> AiRecommendation(
                title = "Preserve capture setup",
                detail = "Latest scan is premium. Keep the same distance and light angle for the next batch pass.",
                priority = AssistantPriority.Stable
            )
            null -> AiRecommendation(
                title = "Start assisted grading",
                detail = "Run the first scan so the assistant can create live recommendations from the batch pattern.",
                priority = AssistantPriority.Watch
            )
        }
        val second = when {
            insights.rejectRateChange >= 5f -> AiRecommendation(
                title = "Investigate trend spike",
                detail = "Reject rate is rising by ${signedPercent(insights.rejectRateChange)}. Inspect humidity, harvest timing, and camera focus before accepting more bags.",
                priority = AssistantPriority.Critical
            )
            insights.rejectRate >= 10f -> AiRecommendation(
                title = "Tighten sampling",
                detail = "Scan two extra bags per shelf until reject rate returns below 10%.",
                priority = AssistantPriority.Watch
            )
            insights.averageConfidence in 0.01f..0.82f -> AiRecommendation(
                title = "Improve model confidence",
                detail = "Retake uncertain images with the bag centered and glare reduced.",
                priority = AssistantPriority.Watch
            )
            else -> AiRecommendation(
                title = "Continue routine pass",
                detail = "No anomaly is detected. Keep scanning in the same order so trend data remains comparable.",
                priority = basePriority
            )
        }
        val third = AiRecommendation(
            title = "Log batch decision",
            detail = "Export scan history after this run so the quality record matches today's inspection.",
            priority = AssistantPriority.Stable
        )
        return listOf(first, second, third)
    }

    private fun buildAdvice(repository: PleuroTechRepository): String {
        val insights = repository.insights(7)
        val latest = repository.latestScan
        val time = LocalTime.now()
        val timingNote = if (time.hour >= 17) {
            "Review end-of-day records before closing the batch."
        } else {
            "Keep scanning consistent angles so confidence stays comparable."
        }
        val latestNote = when (latest?.finalResult) {
            ScanResult.Reject -> "The latest bag was rejected, so separate it and rescan nearby bags from the same batch."
            ScanResult.ClassB -> "The latest bag is acceptable but not premium; compare it against recent Class A examples."
            ScanResult.ClassA -> "Latest scan is premium, so keep the same capture distance and lighting."
            null -> "Start with a demo scan so the assistant has data to review."
        }
        return "${recommendationForRisk(insights.riskLevel)} $latestNote $timingNote"
    }

    private fun recommendationForRisk(level: String): String = when (level) {
        "High" -> "Prioritize reject review and inspect the batch before accepting more bags."
        "Watch" -> "Rejects are worth monitoring; compare the next scans closely against the recent pattern."
        else -> "The batch looks stable; continue routine scanning and watch for sudden reject spikes."
    }

    private fun signedPercent(value: Float): String {
        val sign = if (value > 0f) "+" else ""
        return "$sign${"%.1f".format(value)}%"
    }
}
