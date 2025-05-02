package pi.project.grapify.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pi.project.grapify.presentation.screen.GrapeLeafDiseaseDetectionScreen
import pi.project.grapify.presentation.ui.theme.GrapifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrapifyTheme {
                GrapeLeafDiseaseDetectionScreen()
            }
        }
    }
}