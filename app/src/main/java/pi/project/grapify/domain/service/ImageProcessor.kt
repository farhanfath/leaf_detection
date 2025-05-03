package pi.project.grapify.domain.service

import android.graphics.Bitmap
import java.nio.ByteBuffer

interface ImageProcessor {
    fun preProcessImage(bitmap: Bitmap): ByteBuffer
}