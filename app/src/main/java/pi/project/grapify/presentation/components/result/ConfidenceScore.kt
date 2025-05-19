package pi.project.grapify.presentation.components.result

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pi.project.grapify.domain.util.getConfidenceColor
import java.util.Locale

/**
 * TODO: komponen yang menampilkan indicator dari hasil confidence skor dalam bentur garis
 */
@Preview(showBackground = true)
@Composable
fun ConfidenceScoreItem(
  label: String = "",
  score: Float = 1f
) {
  val animatedProgress = remember { Animatable(initialValue = 0f) }

  LaunchedEffect(key1 = score) {
    animatedProgress.animateTo(
      targetValue = score,
      animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f)
      )

      Text(
        text = String.format(Locale("id", "ID"),"%.2f%%", score * 100),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(12.dp)
        .background(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(6.dp)
        )
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(animatedProgress.value)
          .height(12.dp)
          .background(
            color = getConfidenceColor(score),
            shape = RoundedCornerShape(6.dp)
          )
      )
    }
  }
}