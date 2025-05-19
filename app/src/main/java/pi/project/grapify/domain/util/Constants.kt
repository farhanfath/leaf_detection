package pi.project.grapify.domain.util

/**
 * TODO: disini untuk menyatukan nama2 yang sekiranya digunakan dengan nama yang sama sehingga tidak perlu inisiasi lebih dari 1x
 */
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