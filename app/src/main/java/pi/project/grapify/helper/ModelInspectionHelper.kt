package pi.project.grapify.helper

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

/**
 * Helper class untuk inspeksi model TFLite
 * Dapat digunakan untuk debugging untuk memeriksa bentuk input dan output model
 */
object ModelInspectionHelper {

    /**
     * Memeriksa detail model TFLite dan mencetaknya ke log
     * @param context Android context
     * @param modelName Nama file model di assets folder
     */
    fun inspectModel(context: Context, modelName: String) {
        try {
            val modelFile = context.assets.openFd(modelName)
            val fileDescriptor = modelFile.fileDescriptor
            val interpreter = Interpreter(
                FileInputStream(fileDescriptor).channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    modelFile.startOffset,
                    modelFile.declaredLength
                )
            )

            // Log jumlah input dan output
            val inputCount = interpreter.inputTensorCount
            val outputCount = interpreter.outputTensorCount
            android.util.Log.d("Model Inspection", "Model has $inputCount input(s) and $outputCount output(s)")

            // Inspeksi setiap input tensor
            for (i in 0 until inputCount) {
                val inputTensor = interpreter.getInputTensor(i)
                val inputShape = inputTensor.shape()
                val inputDataType = inputTensor.dataType()

                android.util.Log.d("Model Inspection", "Input #$i shape: ${inputShape.contentToString()}, type: $inputDataType")
            }

            // Inspeksi setiap output tensor
            for (i in 0 until outputCount) {
                val outputTensor = interpreter.getOutputTensor(i)
                val outputShape = outputTensor.shape()
                val outputDataType = outputTensor.dataType()

                android.util.Log.d("Model Inspection", "Output #$i shape: ${outputShape.contentToString()}, type: $outputDataType")
            }

            // Melepaskan sumber daya interpreter
            interpreter.close()
            modelFile.close()

            android.util.Log.d("Model Inspection", "Model inspection completed")
        } catch (e: Exception) {
            android.util.Log.e("Model Inspection", "Error inspecting model: ${e.message}")
            android.util.Log.e("Model Inspection", "Stack trace: ${e.stackTraceToString()}")
        }
    }
}