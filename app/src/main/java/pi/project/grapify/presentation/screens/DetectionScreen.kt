package pi.project.grapify.presentation.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import pi.project.grapify.presentation.components.EmptySection
import pi.project.grapify.presentation.components.ErrorMessage
import pi.project.grapify.presentation.components.LoadingIndicator
import pi.project.grapify.presentation.components.dialog.ImageSourceBottomSheet
import pi.project.grapify.presentation.components.dialog.InfoDialog
import pi.project.grapify.presentation.components.imagehandler.CameraView
import pi.project.grapify.presentation.components.imagehandler.PreviewCard
import pi.project.grapify.presentation.components.result.ResultCard
import pi.project.grapify.presentation.state.ImageSource
import pi.project.grapify.presentation.state.UiState
import pi.project.grapify.presentation.viewmodel.GrapeLeafDiseaseViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetectionScreen(
    viewModel: GrapeLeafDiseaseViewModel = hiltViewModel<GrapeLeafDiseaseViewModel>()
) {
    val context = LocalContext.current

    // image handler
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var imageSource by remember { mutableStateOf<ImageSource>(ImageSource.None) }

    // dialog handler
    var showMoreInfo by remember { mutableStateOf(false) }
    var showImageSourceBottomSheet by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val showRawOutput by viewModel.showRawOutput.collectAsState()

    // Permission untuk kamera
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    // gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            try {
                bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                imageSource = ImageSource.Gallery
            } catch (e: Exception) {
                Log.e("ImagePicker", "Error loading image", e)
            }
        }
    }

    // Fungsi untuk mengambil gambar dari kamera dan menyimpannya sebagai bitmap
    val captureImage: (Bitmap) -> Unit = { capturedBitmap ->
        bitmap = capturedBitmap
        showCamera = false
        imageSource = ImageSource.Camera
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
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
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Menampilkan gambar preview dalam bentuk card
                    bitmap?.let { bm ->
                        AnimatedVisibility(
                            visible = true,
                            enter = expandVertically() + fadeIn()
                        ) {
                            PreviewCard(
                                bitmap = bm,
                                onAnalyzeClick = { viewModel.detectDisease(bm) }
                            )
                        }
                    }

                    // Menampilkan state UI
                    when (val state = uiState) {
                        is UiState.Idle -> {
                            if (bitmap == null) {
                                EmptySection(
                                    onSelectImageClick = {
                                        showImageSourceBottomSheet = true
                                    }
                                )
                            }
                        }
                        is UiState.Loading -> {
                            LoadingIndicator()
                        }
                        is UiState.Error -> {
                            ErrorMessage(message = state.message)
                        }
                        is UiState.Success -> {
                            bitmap?.let { bm ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = expandVertically() + fadeIn()
                                ) {
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

            // Info Dialog
            if (showMoreInfo) {
                InfoDialog(onDismiss = { showMoreInfo = false })
            }

            // choose image source dialog
            if (showImageSourceBottomSheet) {
                ImageSourceBottomSheet(
                    onDismissRequest = {
                        showImageSourceBottomSheet = false
                    },
                    onGalleryClick = {
                        galleryLauncher.launch("image/*")
                        showImageSourceBottomSheet = false
                    },
                    onCameraClick = {
                        if (cameraPermissionState.status.isGranted) {
                            showCamera = true
                            showImageSourceBottomSheet = false
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                )
            }
        }
    }
}