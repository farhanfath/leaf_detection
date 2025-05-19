package pi.project.grapify.presentation.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * TODO: komponen yang menampilkan 2 tombol kamera dan gallery
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSourceBottomSheet(
  onDismissRequest: () -> Unit,
  onGalleryClick: () -> Unit,
  onCameraClick: () -> Unit
) {
  val bottomSheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = bottomSheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 4.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Pilih Sumber Gambar",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Unggah gambar daun anggur untuk mendeteksi penyakit",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(32.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        ImageSourceOption(
          modifier = Modifier.weight(1f),
          icon = Icons.Rounded.PhotoLibrary,
          title = "Galeri",
          description = "Pilih dari galeri",
          onClick = onGalleryClick,
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        ImageSourceOption(
          modifier = Modifier.weight(1f),
          icon = Icons.Rounded.CameraAlt,
          title = "Kamera",
          description = "Ambil foto baru",
          onClick = onCameraClick,
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun ImageSourceOption(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  title: String,
  description: String,
  onClick: () -> Unit,
  containerColor: Color,
  contentColor: Color
) {
  Card(
    modifier = modifier
      .clickable(onClick = onClick)
      .size(120.dp),
    colors = CardDefaults.cardColors(
      containerColor = containerColor
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = contentColor,
        modifier = Modifier.size(24.dp)
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = contentColor
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = contentColor.copy(alpha = 0.8f),
        textAlign = TextAlign.Center
      )
    }
  }
}