package pi.project.grapify.presentation.utils

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File

class MediaHelper(
    val openGallery: () -> Unit,
    val openCamera: () -> Unit
)

/**
 * TODO: sebuah helper unuk menghandle izin kamera dan juga gallery
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberMediaHelper(
    context: Context,
    setImageBitmap: (Bitmap) -> Unit
): MediaHelper {
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var shouldLaunchCamera by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("MediaHelper", "Failed to decode gallery image", e)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uri ->
                try {
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                            decoder.isMutableRequired = true
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }
                    setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.e("MediaHelper", "Failed to decode camera image", e)
                }
            }
        }
    }

    fun launchCamera() {
        val photoFile = File.createTempFile(
            "camera_photo_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        tempImageUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted && shouldLaunchCamera) {
            launchCamera()
            shouldLaunchCamera = false
        } else if (!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale && shouldLaunchCamera) {
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_LONG).show()
            shouldLaunchCamera = false
        }
    }

    return remember {
        MediaHelper(
            openGallery = {
                galleryLauncher.launch("image/*")
            },
            openCamera = {
                if (cameraPermissionState.status.isGranted) {
                    launchCamera()
                } else {
                    shouldLaunchCamera = true
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        )
    }
}
