package pi.project.grapify.presentation.components.imagehandler

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.asImageBitmap
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
      Text(
        text = "Preview Gambar",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium
      )

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

      Button(
        onClick = onAnalyzeClick,
        modifier = Modifier
          .fillMaxWidth(0.8f)
          .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Analyze"
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Analisis Gambar",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (uiState is UiState.Success) {
        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = onRetryClick,
          modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
          shape = RoundedCornerShape(25.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Restore,
              contentDescription = "retry"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Pilih Gambar Lainnya",
              style = MaterialTheme.typography.bodyLarge,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}