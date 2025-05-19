package pi.project.grapify.presentation.state

import pi.project.grapify.data.model.ClassPrediction

// State untuk UI
/**
 * TODO: untuk menggabungkan hasil yang didapatkan selama proses deteksi menggunakan ini seperti :
 * apabila sedang loading / error / success
 */
sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class Success(
        val prediction: String,
        val confidenceScores: List<ClassPrediction>,
        val rawOutput: String
    ) : UiState()
    data class Error(val message: String) : UiState()
}