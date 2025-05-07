package pi.project.grapify.presentation.components.result

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pi.project.grapify.data.model.ClassPrediction
import pi.project.grapify.domain.util.getColorForDisease
import pi.project.grapify.domain.util.getDiseaseInfo

@Composable
fun ResultCard(
  bitmap: Bitmap,
  prediction: String,
  confidenceScores: List<ClassPrediction>,
  rawOutput: String,
  showRawOutput: Boolean,
  onToggleRawOutput: () -> Unit
) {
  var expandedDisease by remember { mutableStateOf(false) }
  val diseaseInfo = getDiseaseInfo(prediction)

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
      // Header dengan warna yang sesuai dengan hasil
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            color = getColorForDisease(prediction),
            shape = RoundedCornerShape(8.dp)
          )
          .padding(16.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Hasil Deteksi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Spacer(modifier = Modifier.height(8.dp))

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

      // Informasi tentang penyakit
      if (diseaseInfo != null) {
        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
          visible = true,
          enter = expandVertically() + fadeIn()
        ) {
          DiseaseInfoCard(
            disease = prediction,
            diseaseInfo = diseaseInfo,
            expanded = expandedDisease,
            onExpandChange = { expandedDisease = !expandedDisease }
          )
        }
      }

      // Toggle untuk raw output
      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
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
      AnimatedVisibility(visible = showRawOutput && rawOutput.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ) {
          Text(
            text = "Raw Output:",
            style = MaterialTheme.typography.titleSmall
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = rawOutput,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
              .fillMaxWidth()
              .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
              )
              .padding(12.dp)
          )
        }
      }

      // Action buttons
      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        OutlinedButton(
          onClick = { /* Share result logic */ },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share"
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Bagikan")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
          onClick = { /* Save result logic */ },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Save,
            contentDescription = "Save"
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Simpan")
        }
      }
    }
  }
}