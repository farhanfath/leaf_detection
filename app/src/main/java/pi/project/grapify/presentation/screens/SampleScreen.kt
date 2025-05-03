package pi.project.grapify.presentation.screens

// Jetpack Compose
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

// CameraX
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

// Permissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

// Android
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.accompanist.permissions.isGranted
import pi.project.grapify.presentation.state.UiState
import pi.project.grapify.presentation.viewmodel.GrapeLeafDiseaseViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SampleScreen(
    viewModel: GrapeLeafDiseaseViewModel = hiltViewModel<GrapeLeafDiseaseViewModel>()
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var imageSource by remember { mutableStateOf<ImageSource>(ImageSource.None) }

    val uiState by viewModel.uiState.collectAsState()
    val showRawOutput by viewModel.showRawOutput.collectAsState()

    // Permission untuk kamera
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // Launcher untuk memilih gambar dari galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            try {
                val source = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.createSource(context.contentResolver, it)
                } else {
                    TODO("VERSION.SDK_INT < P")
                }
                bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
                imageSource = ImageSource.Gallery
            } catch (e: Exception) {
                Log.e("ImagePicker", "Error loading image", e)
                // Handle error
            }
        }
    }

    // Fungsi untuk mengambil gambar dari kamera dan menyimpannya sebagai bitmap
    val captureImage: (Bitmap) -> Unit = { capturedBitmap ->
        bitmap = capturedBitmap
        showCamera = false
        imageSource = ImageSource.Camera
    }

    // Tampilan utama
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (showCamera) {
            CameraView(
                onImageCaptured = captureImage,
                onError = { Log.e("Camera", "Error", it) },
                onClose = { showCamera = false }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Deteksi Penyakit Daun Anggur",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { galleryLauncher.launch("image/*") }) {
                        Text("Pilih dari Galeri")
                    }

                    Button(
                        onClick = {
                            if (cameraPermissionState.status.isGranted) {
                                showCamera = true
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                    ) {
                        Text("Ambil Foto")
                    }
                }

                // Menampilkan gambar preview
                bitmap?.let { bm ->
                    Image(
                        bitmap = bm.asImageBitmap(),
                        contentDescription = "Preview Gambar",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Tombol untuk memulai analisis
                    Button(
                        onClick = { viewModel.detectDisease(bm) },
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("Analisis Gambar")
                    }
                }

                // Menampilkan state UI
                when (val state = uiState) {
                    is UiState.Idle -> {
                        // Tampilan awal, tidak ada yang perlu ditampilkan
                    }
                    is UiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is UiState.Error -> {
                        ErrorMessage(message = state.message)
                    }
                    is UiState.Success -> {
                        bitmap?.let { bm ->
                            ResultCard(
                                bitmap = bm,
                                prediction = state.prediction,
                                confidenceScores = state.confidenceScores,
                                rawOutput = state.rawOutput,
                                showRawOutput = showRawOutput,
                                onToggleRawOutput = { viewModel.toggleRawOutput() }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Class untuk melacak sumber gambar
sealed class ImageSource {
    object None : ImageSource()
    object Gallery : ImageSource()
    object Camera : ImageSource()
}

// Komponen CameraView untuk menampilkan dan mengambil gambar dari kamera
@Composable
fun CameraView(
    onImageCaptured: (Bitmap) -> Unit,
    onError: (Exception) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    val executor = remember { ContextCompat.getMainExecutor(context) }

    DisposableEffect(lifecycleOwner) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            cameraProvider = future.get()

            val preview = Preview.Builder().build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                onError(e)
            }
        }, executor)

        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                preview?.setSurfaceProvider(previewView.surfaceProvider)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

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

        IconButton(
            onClick = {
                val imageCapture = imageCapture ?: return@IconButton

                val outputOptions = ImageCapture.OutputFileOptions.Builder(
                    ByteArrayOutputStream()
                ).build()

                imageCapture.takePicture(
                    outputOptions,
                    executor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture
                        .OutputFileResults) {
                            val savedUri = outputFileResults.savedUri

                            // Sebagai alternatif, ambil dari imageProxy jika savedUri null
                            try {
                                val imageProxy = imageCapture.takePicture(executor)
                                val buffer = imageProxy.planes[0].buffer
                                val bytes = ByteArray(buffer.capacity())
                                buffer.get(bytes)

                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                if (bitmap != null) {
                                    onImageCaptured(bitmap)
                                }

                                imageProxy.close()
                            } catch (e: Exception) {
                                onError(e)
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            onError(exception)
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
                .background(Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Take Photo",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ResultCard(
    bitmap: Bitmap,
    prediction: String,
    confidenceScores: List<Pair<String, Float>>,
    rawOutput: FloatArray?,
    showRawOutput: Boolean,
    onToggleRawOutput: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hasil Deteksi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Hasil prediksi utama
            Text(
                text = "Terdeteksi: $prediction",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confidence scores
            Text(
                text = "Confidence Scores:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            confidenceScores.forEach { (label, score) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = String.format("%.2f%%", score * 100),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = score,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            // Toggle untuk raw output
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Tampilkan Raw Output")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = showRawOutput,
                    onCheckedChange = { onToggleRawOutput() }
                )
            }

            // Raw output jika diaktifkan
            if (showRawOutput && rawOutput != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Raw Output:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rawOutput.joinToString(", ") { "%.4f".format(it) },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                )
            }
        }
    }
}