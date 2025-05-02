package pi.project.grapify.presentation.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pi.project.grapify.presentation.components.ErrorMessage
import pi.project.grapify.presentation.components.ResultCard
import pi.project.grapify.presentation.state.UiState
import pi.project.grapify.presentation.viewmodel.GrapeLeafDiseaseViewModel

@Composable
fun GrapeLeafDiseaseDetectionScreen(
    viewModel: GrapeLeafDiseaseViewModel = hiltViewModel<GrapeLeafDiseaseViewModel>()
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val showRawOutput by viewModel.showRawOutput.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            }

            bitmap?.let { bm ->
                viewModel.detectDisease(bm)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
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
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Pilih Gambar")
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