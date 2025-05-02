package pi.project.grapify.domain.service

import java.nio.ByteBuffer

interface ModelAnalyzer {
    fun runInference(inputBuffer: ByteBuffer): FloatArray
    fun inspectModel()
}