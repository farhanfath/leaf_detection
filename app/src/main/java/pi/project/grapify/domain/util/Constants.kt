package pi.project.grapify.domain.util

import pi.project.grapify.data.model.DiseaseInfo

object Constants {
    const val MODEL_FILENAME = "grapeleafdisease_model.tflite"
    const val INPUT_SIZE = 224
    val CLASS_NAMES = listOf(
        "Grape Black Rot",
        "Grape Esca (Black Measles)",
        "Grape Leaf Blight (Isariopsis Leaf Spot)",
        "Grape Healthy",
        "Not Anggur"
    )
}