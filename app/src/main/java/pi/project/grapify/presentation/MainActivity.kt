package pi.project.grapify.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pi.project.grapify.presentation.screens.GrapeLeafDiseaseDetectionScreen
import pi.project.grapify.presentation.theme.GrapifyTheme

@AndroidEntryPoint
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