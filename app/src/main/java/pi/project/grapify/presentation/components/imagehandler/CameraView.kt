package pi.project.grapify.presentation.components.imagehandler

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

@Composable
fun CameraView(
  onImageCaptured: (Bitmap) -> Unit,
  onError: (Exception) -> Unit,
  onClose: () -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val previewView = remember { PreviewView(context) }
  val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

  var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

  AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

  LaunchedEffect(true) {
    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build().also {
      it.surfaceProvider = previewView.surfaceProvider
    }

    imageCapture = ImageCapture.Builder().build()

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
      lifecycleOwner,
      cameraSelector,
      preview,
      imageCapture
    )
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
  ) {
    IconButton(
      onClick = onClose,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(16.dp)
        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
    ) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Close Camera",
        tint = Color.White
      )
    }

    FloatingActionButton(
      onClick = {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
          File.createTempFile("temp", ".jpg", context.cacheDir)
        ).build()

        imageCapture?.takePicture(
          ContextCompat.getMainExecutor(context),
          object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
              val bitmap = imageProxy.toBitmap()
              onImageCaptured(bitmap)
              imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
              Log.e("CameraX", "Capture failed: ${exception.message}")
              onError(exception)
            }
          }
        )
      },
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(16.dp)
    ) {
      Icon(Icons.Default.Camera, contentDescription = "Capture")
    }
  }
}