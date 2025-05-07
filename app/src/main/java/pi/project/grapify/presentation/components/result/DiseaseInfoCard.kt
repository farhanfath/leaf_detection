package pi.project.grapify.presentation.components.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pi.project.grapify.data.model.DiseaseInfo

@Composable
fun DiseaseInfoCard(
  disease: String,
  diseaseInfo: DiseaseInfo,
  expanded: Boolean,
  onExpandChange: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Informasi Penyakit",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onExpandChange) {
          Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (expanded) "Sembunyikan" else "Tampilkan"
          )
        }
      }

      // Informasi singkat selalu ditampilkan
      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.BugReport,
          contentDescription = "Penyebab",
          tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
          text = "Penyebab: ${diseaseInfo.penyebab.take(50)}${if (diseaseInfo.penyebab.length > 50) "..." else ""}",
          style = MaterialTheme.typography.bodyMedium
        )
      }

      // Informasi lengkap saat expanded
      AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column {
          Spacer(modifier = Modifier.height(16.dp))

          DiseaseInfoSection(
            title = "Penyebab Lengkap",
            content = diseaseInfo.penyebab,
            icon = Icons.Default.Coronavirus
          )

          Divider(modifier = Modifier.padding(vertical = 12.dp))

          DiseaseInfoSection(
            title = "Gejala",
            content = diseaseInfo.gejala,
            icon = Icons.Default.HealthAndSafety
          )

          Divider(modifier = Modifier.padding(vertical = 12.dp))

          DiseaseInfoSection(
            title = "Tindakan Pencegahan",
            content = diseaseInfo.pencegahan,
            icon = Icons.Default.Shield
          )
        }
      }
    }
  }
}
