package com.example.pleurotech.ai

import com.example.pleurotech.data.ScanResult

data class DetectionResult(
    val result: ScanResult,
    val confidence: Float,
    val label: String = result.apiName
)

interface YoloV8Classifier {
    suspend fun classifyFrame(frameBytes: ByteArray): DetectionResult
}

class TrainingPlaceholderYoloV8Classifier : YoloV8Classifier {
    override suspend fun classifyFrame(frameBytes: ByteArray): DetectionResult {
        return DetectionResult(
            result = ScanResult.ClassA,
            confidence = 0f,
            label = "YOLOv8 model placeholder"
        )
    }
}
