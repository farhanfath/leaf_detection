package pi.project.grapify.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pi.project.grapify.data.repository.GrapeLeafDiseaseRepository
import pi.project.grapify.domain.util.Constants
import pi.project.grapify.presentation.state.UiState
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GrapeLeafDiseaseViewModel @Inject constructor(
    private val repository: GrapeLeafDiseaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _showRawOutput = MutableStateFlow(false)
    val showRawOutput: StateFlow<Boolean> = _showRawOutput

    init {
        // Inspect model on initialization
        repository.inspectModel()
    }

    fun detectDisease(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.predictDisease(bitmap).fold(
                onSuccess = { result ->
                    _uiState.value = UiState.Success(
                        prediction = result.predictedClass,
                        confidenceScores = result.allPredictions,
                        rawOutput = formatRawOutput(result.rawOutput)
                    )
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error occurred")
                }
            )
        }
    }

    fun toggleRawOutput() {
        _showRawOutput.value = !_showRawOutput.value
    }

    private fun formatRawOutput(rawOutput: FloatArray): String {
        return buildString {
            append("Output Mentah Model:\n")
            append("[\n")
            rawOutput.forEachIndexed { index, value ->
                append("  $index: $value")
                if (index < rawOutput.size - 1) append(",")
                append("\n")
            }
            append("]\n\n")

            append("Output dengan Label:\n")
            rawOutput.forEachIndexed { index, value ->
                if (index < Constants.CLASS_NAMES.size) {
                    append("  ${Constants.CLASS_NAMES[index]}: $value (${String.format(Locale("id", "ID"),"%.2f%%", value * 100)})\n")
                } else {
                    append("  Unknown-$index: $value (${String.format(Locale("id", "ID"),"%.2f%%", value * 100)})\n")
                }
            }
        }
    }
}