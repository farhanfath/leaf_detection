package pi.project.grapify.domain.service

import android.content.Context
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import pi.project.grapify.domain.util.Constants
import pi.project.grapify.ml.GrapeleafdiseaseModel
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class TFLiteModelAnalyzer @Inject constructor(
    private val context: Context
) : ModelAnalyzer {
    override fun runInference(inputBuffer: ByteBuffer): FloatArray {
        try {
            // Menggunakan TensorFlow Lite model binding
            val model = GrapeleafdiseaseModel.newInstance(context)

            // Menyiapkan input
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, Constants.INPUT_SIZE, Constants.INPUT_SIZE, 3),
                DataType.FLOAT32
            )
            inputFeature0.loadBuffer(inputBuffer)

            // Menjalankan inferensi
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Mengambil hasil mentah sebagai FloatArray
            val rawOutputs = outputFeature0.floatArray

            // Melepaskan sumber daya model
            model.close()

            return rawOutputs
        } catch (e: Exception) {
            Log.e("TFLite Error", "Error saat menjalankan model: ${e.message}", e)
            return FloatArray(Constants.CLASS_NAMES.size) { 0f }
        }
    }

    override fun inspectModel() {
        try {
            val modelFile = context.assets.openFd(Constants.MODEL_FILENAME)
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
            Log.d("Model Inspection", "Model has $inputCount input(s) and $outputCount output(s)")

            // Inspeksi setiap input tensor
            for (i in 0 until inputCount) {
                val inputTensor = interpreter.getInputTensor(i)
                val inputShape = inputTensor.shape()
                val inputDataType = inputTensor.dataType()

                Log.d("Model Inspection", "Input #$i shape: ${inputShape.contentToString()}, type: $inputDataType")
            }

            // Inspeksi setiap output tensor
            for (i in 0 until outputCount) {
                val outputTensor = interpreter.getOutputTensor(i)
                val outputShape = outputTensor.shape()
                val outputDataType = outputTensor.dataType()

                Log.d("Model Inspection", "Output #$i shape: ${outputShape.contentToString()}, type: $outputDataType")
            }

            // Melepaskan sumber daya interpreter
            interpreter.close()
            modelFile.close()

            Log.d("Model Inspection", "Model inspection completed")
        } catch (e: Exception) {
            Log.e("Model Inspection", "Error inspecting model: ${e.message}", e)
        }
    }
}