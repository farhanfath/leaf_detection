package pi.project.grapify.data.model

import java.util.Locale

data class ClassPrediction(
    val className: String,
    val confidence: Float
) {
    val confidencePercentage: String
        get() = String.format(Locale("id", "ID"),"%.2f%%", confidence * 100)
}
