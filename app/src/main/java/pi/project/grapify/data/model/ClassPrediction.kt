package pi.project.grapify.data.model

/**
 * TODO: untuk manage data hasil prediksi contoh (grape black rot, 99.0%)
 */
data class ClassPrediction(
    val className: String,
    val confidence: Float
)