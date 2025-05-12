package pi.project.grapify.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pi.project.grapify.presentation.state.UiState

@Preview
@Composable
fun PreviewCard(
  bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
  onAnalyzeClick: () -> Unit = {},
  onRetryClick: () -> Unit = {},
  uiState: UiState = UiState.Idle
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Title with icon
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Image,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Preview Gambar",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Preview Gambar",
        modifier = Modifier
          .size(250.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.height(16.dp))

      when(uiState) {
        is UiState.Idle -> {
          GradientButton(
            onClick = onAnalyzeClick,
            text = "Analisis Gambar",
            icon = Icons.Default.Search
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Ketuk untuk menganalisis gambar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(4.dp)
          )
        }
        is UiState.Success -> {
          GradientButton(
            onClick = onRetryClick,
            text = "Pilih Gambar Lainnya",
            icon = Icons.Default.Restore,
            gradientColors = listOf(
              MaterialTheme.colorScheme.tertiary,
              MaterialTheme.colorScheme.secondary
            )
          )
        }
        is UiState.Loading -> {}
        is UiState.Error -> {}
      }
    }
  }
}

@Composable
fun GradientButton(
  onClick: () -> Unit,
  text: String,
  icon: ImageVector,
  gradientColors: List<Color> = listOf(
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.secondary
  )
) {
  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth(0.9f)
      .height(56.dp),
    shape = RoundedCornerShape(28.dp),
    contentPadding = PaddingValues(0.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(colors = gradientColors)
        ),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = text,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}