package pi.project.grapify.data.repository

import android.graphics.Bitmap
import android.util.Log
import pi.project.grapify.data.model.ClassPrediction
import pi.project.grapify.data.model.PredictionResult
import pi.project.grapify.domain.service.ImageProcessor
import pi.project.grapify.domain.service.ModelAnalyzer
import pi.project.grapify.domain.util.Constants
import javax.inject.Inject

class GrapeLeafDiseaseRepositoryImpl @Inject constructor(
    private val imageProcessor: ImageProcessor,
    private val modelAnalyzer: ModelAnalyzer
) : GrapeLeafDiseaseRepository {
    override suspend fun predictDisease(bitmap: Bitmap): Result<PredictionResult> {
        return try {
            // Resize gambar ke ukuran input model
            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                Constants.INPUT_SIZE,
                Constants.INPUT_SIZE,
                true
            )

            // Preprocessing gambar
            val inputBuffer = imageProcessor.preProcessImage(resizedBitmap)

            // Jalankan inferensi
            val rawOutputs = modelAnalyzer.runInference(inputBuffer)

            // Memproses hasil prediksi
            val predictions = rawOutputs.mapIndexed { index, confidence ->
                val className = if (index < Constants.CLASS_NAMES.size) {
                    Constants.CLASS_NAMES[index]
                } else {
                    "Unknown-$index"
                }
                ClassPrediction(className, confidence)
            }

            // Mendapatkan prediksi dengan kepercayaan tertinggi
            val maxPrediction = predictions.maxByOrNull { it.confidence }
                ?: ClassPrediction("Unknown", 0f)

            Result.success(
                PredictionResult(
                    predictedClass = maxPrediction.className,
                    allPredictions = predictions.sortedByDescending { it.confidence },
                    rawOutput = rawOutputs
                )
            )
        } catch (e: Exception) {
            Log.e("Repository", "Error during prediction: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun inspectModel() {
        modelAnalyzer.inspectModel()
    }
}