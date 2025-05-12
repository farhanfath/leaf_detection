package pi.project.grapify.presentation.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pi.project.grapify.data.model.DiseaseInfo
import pi.project.grapify.domain.util.getDiseaseInfo
import pi.project.grapify.presentation.components.ErrorMessage
import pi.project.grapify.presentation.components.IntroSection
import pi.project.grapify.presentation.components.LoadingIndicator
import pi.project.grapify.presentation.components.dialog.ImageSourceBottomSheet
import pi.project.grapify.presentation.components.dialog.InfoDialog
import pi.project.grapify.presentation.components.glossary.GrapeDiseasesGlossaryDrawer
import pi.project.grapify.presentation.components.PreviewCard
import pi.project.grapify.presentation.components.result.ResultCard
import pi.project.grapify.presentation.state.UiState
import pi.project.grapify.presentation.utils.rememberMediaHelper
import pi.project.grapify.presentation.viewmodel.GrapeLeafDiseaseViewModel

@Composable
fun DetectionScreen(
    viewModel: GrapeLeafDiseaseViewModel = hiltViewModel<GrapeLeafDiseaseViewModel>()
) {
    val context = LocalContext.current

    /**
     * untuk handle gambar terpilih dan yang akan di proses
     */
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    /**
     * helper untuk pengambilan gambar dari source
     */
    val mediaHelper = rememberMediaHelper(
        context = context,
        setImageBitmap = { selectedImage ->
            bitmap = selectedImage
        }
    )


    // dialog handler
    var showMoreInfo by remember { mutableStateOf(false) }
    var showImageSourceBottomSheet by remember { mutableStateOf(false) }

    /**
     * handle proses selama deteksi dan hasil deteksi
     */
    val uiState by viewModel.uiState.collectAsState()
    var showDiseaseDetailDialog by remember { mutableStateOf(false) }

    /**
     * untuk handle terkait glosarium
     */
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    var selectedDisease by remember { mutableStateOf<DiseaseInfo?>(null) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        ModalNavigationDrawer(
            modifier = Modifier.padding(innerPadding),
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(280.dp)
                ) {
                    /**
                     * menampilkan list glosarium penyakit pada daun anggur
                     */
                    GrapeDiseasesGlossaryDrawer()
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                /**
                 * kondisi awal saat belum ada gambar yang di pilih dan di proses
                 */
                if (bitmap == null && uiState is UiState.Idle) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        /**
                         * tampilan utama aplikasi
                         */
                        IntroSection(
                            onSelectImageClick = {
                                showImageSourceBottomSheet = true
                            },
                            onShowGlossaryClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            onShowInfoClick = {
                                showMoreInfo = true
                            }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        /**
                         * untuk menampilkan gambar yang sudah terpilih atau diambil dari kamera
                         */
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
                                        viewModel.resetState()
                                        showImageSourceBottomSheet = true
                                    },
                                    uiState = uiState
                                )
                            }
                        }

                        // Menampilkan state UI
                        when (val state = uiState) {
                            is UiState.Idle -> {}
                            is UiState.Loading -> {
                                LoadingIndicator()
                            }
                            is UiState.Error -> {
                                ErrorMessage(message = state.message)
                            }
                            is UiState.Success -> {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = expandVertically() + fadeIn()
                                ) {
                                    ResultCard(
                                        prediction = state.prediction,
                                        confidenceScores = state.confidenceScores,
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
                            mediaHelper.openGallery()
                            showImageSourceBottomSheet = false
                        },
                        onCameraClick = {
                            mediaHelper.openCamera()
                            showImageSourceBottomSheet = false
                        }
                    )
                }
            }
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