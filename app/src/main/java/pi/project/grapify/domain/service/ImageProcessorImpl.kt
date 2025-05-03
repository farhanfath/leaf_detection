package pi.project.grapify.domain.service

import android.graphics.Bitmap
import android.util.Log
import pi.project.grapify.domain.util.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class ImageProcessorImpl @Inject constructor(): ImageProcessor {
    override fun preProcessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = Constants.INPUT_SIZE
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        Log.d("Image Preprocessing", "Preprocessing image of size ${bitmap.width}x${bitmap.height} to ${inputSize}x${inputSize}")

        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]

                // Ekstrak dan normalisasi nilai RGB (normalisasi dari 0-255 ke 0-1)
                byteBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((pixelValue and 0xFF) / 255.0f)
            }
        }

        byteBuffer.rewind()
        Log.d("Image Preprocessing", "Image preprocessing completed")
        return byteBuffer
    }
}