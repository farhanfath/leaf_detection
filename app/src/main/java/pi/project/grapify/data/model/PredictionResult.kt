package pi.project.grapify.data.model

data class PredictionResult(
    val predictedClass: String,
    val allPredictions: List<ClassPrediction>,
    val rawOutput: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PredictionResult

        if (predictedClass != other.predictedClass) return false
        if (allPredictions != other.allPredictions) return false
        if (!rawOutput.contentEquals(other.rawOutput)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = predictedClass.hashCode()
        result = 31 * result + allPredictions.hashCode()
        result = 31 * result + rawOutput.contentHashCode()
        return result
    }

}
