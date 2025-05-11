package pi.project.grapify.presentation.components.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pi.project.grapify.data.model.ClassPrediction
import pi.project.grapify.domain.util.getColorForDisease

@Composable
fun ResultCard(
  prediction: String,
  confidenceScores: List<ClassPrediction>,
  onShowInfoClick: (String) -> Unit = {}
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            color = getColorForDisease(prediction),
            shape = RoundedCornerShape(8.dp)
          )
          .padding(16.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Hasil Deteksi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
          )

          Text(
            text = prediction,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Confidence scores dengan visualisasi yang lebih baik
      Text(
        text = "Confidence Scores:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Confidence scores dalam bentuk horizontal bar chart
      confidenceScores.forEach { classPrediction ->
        ConfidenceScoreItem(
          label = classPrediction.className,
          score = classPrediction.confidence
        )
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp),
        color = MaterialTheme.colorScheme.onBackground
      )

      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
          onShowInfoClick(prediction)
        }
      ) {
        Row(
          modifier = Modifier.padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info penyakit",
            tint = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Cek Penjelasan Hasil Terkait"
          )
        }
      }
    }
  }
}