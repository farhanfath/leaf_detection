package pi.project.grapify.data.repository

import android.graphics.Bitmap
import pi.project.grapify.data.model.PredictionResult

interface GrapeLeafDiseaseRepository {
    suspend fun predictDisease(bitmap: Bitmap): Result<PredictionResult>
    fun inspectModel()
}