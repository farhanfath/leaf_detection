package pi.project.grapify.data.model

/**
 * TODO: data untuk handling perihal hasil dari deteksi dan juga untuk glossary
 */
data class DiseaseInfo(
    val nama: String,
    val penyebab: String,
    val gejala: String,
    val pencegahan: String,
    val imageSample: Int
)