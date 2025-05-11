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
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BlurCircular
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import pi.project.grapify.data.model.ClassPrediction
import pi.project.grapify.data.model.DiseaseInfo
import pi.project.grapify.domain.util.getDiseaseGlossary
import pi.project.grapify.domain.util.getDiseaseInfo
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

    // glossary handler
    var showGlossary by remember { mutableStateOf(false) }
    var selectedDisease by remember { mutableStateOf<DiseaseInfo?>(null) }
    var showDiseaseDetailDialog by remember { mutableStateOf(false) }
    // Daftar semua penyakit untuk glosarium
    val diseases = remember { getDiseaseGlossary() }

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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showGlossary = !showGlossary },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (showGlossary) Icons.Default.Close else Icons.AutoMirrored.Default.MenuBook,
                    contentDescription = if (showGlossary) "Tutup Glosarium" else "Buka Glosarium",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
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
                    // Glossary Section - Akan muncul ketika tombol FAB ditekan
                    AnimatedVisibility(
                        visible = showGlossary,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        DiseaseGlossarySection(
                            diseases = diseases,
                            onDiseaseClick = { disease ->
                                selectedDisease = disease
                                showDiseaseDetailDialog = true
                            }
                        )
                    }

                    // Menampilkan gambar preview dalam bentuk card
                    bitmap?.let { bm ->
                        AnimatedVisibility(
                            visible = true,
                            enter = expandVertically() + fadeIn()
                        ) {
                            PreviewCard(
                                bitmap = bm,
                                onAnalyzeClick = {
                                    viewModel.detectDisease(bm)
                                },
                                onRetryClick = {
                                    bitmap = null
                                    selectedImageUri = null
                                    imageSource = ImageSource.None
                                    viewModel.resetState()
                                    showImageSourceBottomSheet = true
                                },
                                uiState = uiState
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
                                        onToggleRawOutput = { viewModel.toggleRawOutput() },
                                        onShowInfoClick = { diseaseName ->
                                            val diseaseInfo = getDiseaseInfo(diseaseName)
                                            if (diseaseInfo != null) {
                                                selectedDisease = diseaseInfo
                                                showDiseaseDetailDialog = true
                                            }
                                        }
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

            // Disease Detail Dialog
            if (showDiseaseDetailDialog && selectedDisease != null) {
                DiseaseDetailDialog(
                    diseaseInfo = selectedDisease!!,
                    onDismiss = {
                        showDiseaseDetailDialog = false
                        selectedDisease = null
                    }
                )
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

@Composable
fun DiseaseGlossarySection(
    diseases: List<DiseaseInfo>,
    onDiseaseClick: (DiseaseInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Glosarium Penyakit Daun Anggur",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(diseases) { disease ->
                DiseaseCard(disease = disease, onClick = { onDiseaseClick(disease) })
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun DiseaseCard(
    disease: DiseaseInfo,
    onClick: () -> Unit
) {
    val bgColor = when(disease.nama) {
        "Grape Black Rot" -> Color(0xFFFFCDD2)
        "Grape Esca (Black Measles)" -> Color(0xFFE1BEE7)
        "Grape Leaf Blight (Isariopsis Leaf Spot)" -> Color(0xFFC8E6C9)
        "Grape Healthy" -> Color(0xFFBBDEFB)
        else -> MaterialTheme.colorScheme.surface
    }

    val iconVector = when(disease.nama) {
        "Grape Black Rot" -> Icons.Filled.BugReport
        "Grape Esca (Black Measles)" -> Icons.Filled.Coronavirus
        "Grape Leaf Blight (Isariopsis Leaf Spot)" -> Icons.Filled.BlurCircular
        "Grape Healthy" -> Icons.Filled.Check
        else -> Icons.AutoMirrored.Filled.Help
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = bgColor.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = disease.nama,
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = disease.nama,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Tap untuk detail",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun DiseaseDetailDialog(
    diseaseInfo: DiseaseInfo,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = diseaseInfo.nama,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup"
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                SectionTitle(title = "Penyebab")
                Text(
                    text = diseaseInfo.penyebab,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SectionTitle(title = "Gejala")
                Text(
                    text = diseaseInfo.gejala,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SectionTitle(title = "Pencegahan")
                Text(
                    text = diseaseInfo.pencegahan,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}