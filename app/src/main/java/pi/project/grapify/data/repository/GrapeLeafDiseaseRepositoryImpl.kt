package pi.project.grapify.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import pi.project.grapify.data.model.ClassPrediction
import pi.project.grapify.data.model.PredictionResult
import pi.project.grapify.domain.service.ImageProcessor
import pi.project.grapify.domain.service.ModelAnalyzer
import pi.project.grapify.domain.util.Constants
import javax.inject.Inject

class GrapeLeafDiseaseRepositoryImpl @Inject constructor(
    private val context: Context,
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

            // Log hasil mentah untuk debugging
            logDetailedRawOutput(rawOutputs)

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

    private fun logDetailedRawOutput(results: FloatArray) {
        val tag = "ModelRawOutput"

        // 1. Format output mentah sebagai array
        Log.d(tag, "Raw output as array: ${results.contentToString()}")

        // 2. Format output mentah sebagai pasangan indeks-nilai
        val sb = StringBuilder("Raw output by index:\n")
        for (i in results.indices) {
            sb.append("Index $i: ${results[i]}\n")
        }
        Log.d(tag, sb.toString())

        // 3. Jika kita memiliki label, hubungkan dengan output
        if (results.size == Constants.CLASS_NAMES.size) {
            val labeledSb = StringBuilder("Raw output with labels:\n")
            labeledSb.append("{\n")
            for (i in results.indices) {
                labeledSb.append("  \"${Constants.CLASS_NAMES[i]}\": ${results[i]}")
                if (i < results.size - 1) labeledSb.append(",")
                labeledSb.append("\n")
            }
            labeledSb.append("}")
            Log.d(tag, labeledSb.toString())
        }

        // 4. Jika model menggunakan softmax, tampilkan juga dalam persentase
        val softmaxSb = StringBuilder("Output as probabilities (%):\n")
        for (i in results.indices) {
            val percent = results[i] * 100
            softmaxSb.append("Index $i")
            if (i < Constants.CLASS_NAMES.size) {
                softmaxSb.append(" (${Constants.CLASS_NAMES[i]})")
            }
            softmaxSb.append(": ${String.format("%.2f%%", percent)}\n")
        }
        Log.d(tag, softmaxSb.toString())

        // 5. Mencari nilai maksimum dan indeksnya
        val maxIndex = results.indices.maxByOrNull { results[it] } ?: 0
        Log.d(tag, "Max value: ${results[maxIndex]} at index $maxIndex")
        if (maxIndex < Constants.CLASS_NAMES.size) {
            Log.d(tag, "Corresponding label: ${Constants.CLASS_NAMES[maxIndex]}")
        }
    }
}