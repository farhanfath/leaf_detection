package pi.project.grapify.domain.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import pi.project.grapify.data.model.DiseaseInfo

fun getDiseaseInfo(diseaseName: String): DiseaseInfo? {
    return when (diseaseName) {
        "Grape Black Rot" -> DiseaseInfo(
            penyebab = "Penyakit ini disebabkan oleh jamur Phyllosticta ampelicida. Patogen tersebut biasanya menyerang saat musim dingin dan bertahan hidup pada pucuk yang terinfeksi atau buah kering yang terdapat pada tanaman anggur maupun di tanah. Spora jamur dilepaskan ketika terjadi hujan ringan dan kemudian tersebar melalui angin. Suhu optimal untuk pertumbuhan jamur ini adalah sekitar 25°C dengan kelembapan pada daun yang bertahan setidaknya selama enam jam.",
            gejala = "Gejala penyakit ini ditandai dengan munculnya bintik-bintik tidak beraturan yang dikelilingi oleh garis berwarna gelap merupakan salah satu gejala pada daun. Selain daun, gejala serupa juga dapat terlihat pada tunas, batang, dan tangkai daun. Jika infeksi terjadi pada tangkai daun, maka daun secara keseluruhan akan mengalami pengeringan.",
            pencegahan = "• Gunakan metode pemangkasan alternatif seperti pemangkasan tertunda atau pemangkasan ganda.\n• Hindari pemangkasan selama periode hujan deras ketika spora cenderung tersebar.\n• Pantau kebun anggur di musim semi, dan cari tunas yang mati atau tunas yang kerdil.\n• Pada akhir musim panas, potong bagian tanaman anggur yang membusuk.\n• Singkirkan sisa-sisa tanaman anggur yang terserang penyakit dan musnahkan.\n• Tunda pembuahan selama beberapa tahun hingga tanaman merambat memiliki pertumbuhan akar dan tunas yang seimbang."
        )
        "Grape Esca (Black Measles)" -> DiseaseInfo(
            penyebab = "Penyakit ini dapat muncul kapan saja selama musim tanam dan disebabkan oleh jamur Togninia minima, meskipun beberapa jenis jamur lain seperti Phaeomoniella chlamydospora juga dapat berperan. Infeksi umumnya terjadi pada tanaman anggur yang masih muda, namun gejalanya sering baru tampak di kebun anggur saat tanaman berusia antara 5 hingga 7 tahun.",
            gejala = "Gejala penyakit ini ditandai dengan munculnya belang di antara pembuluh daun, yang disertai perubahan warna serta pengeringan jaringan di sekitar pembuluh utama. Pada varietas anggur berbuah merah, perubahan warna umumnya menjadi merah tua, sedangkan pada varietas berbuah putih, warna berubah menjadi kuning. Daun yang terinfeksi dapat mengalami pengeringan total dan rontok sebelum waktunya.",
            pencegahan = "• Pilih varietas anggur yang lebih tahan penyakit jika tersedia.\n• Buang buah kering (mumi) dari pohon anggur.\n• Singkirkan dan musnahkan kayu serta sulur yang terinfeksi hama setelah panen.\n• Singkirkan daun-daun yang terkena serangan dari area kebun anggur.\n• Jaga kebersihan kebun anggur dengan membersihkan gulma.\n• Pastikan sirkulasi udara dan pencahayaan di kebun cukup.\n• Lakukan pemangkasan tanaman anggur setiap tahun sebelum fase vegetatif dimulai."
        )
        "Grape Leaf Blight (Isariopsis Leaf Spot)" -> DiseaseInfo(
            penyebab = "Penyakit ini disebabkan oleh jamur Peyronellaea glomerata, yang sebelumnya dikenal sebagai Phoma glomerata. Jamur ini tersebar luas dan mampu bertahan di tanah maupun pada berbagai bahan tanaman, baik yang masih hidup maupun sudah mati (seperti biji, buah-buahan, dan sayuran), biasanya tanpa menimbulkan gejala yang nyata.",
            gejala = "Gejala infeksi hawar daun phoma umumnya terlihat pada daun-daun yang lebih tua. Daun yang terinfeksi memperlihatkan lesi tidak teratur berbentuk sudut dengan warna kuning hingga cokelat yang menyebar di seluruh permukaan daun. Seiring perkembangan penyakit, lesi tersebut membesar membentuk bercak yang kemudian berubah menjadi area nekrotik dengan warna kusam, pusat berwarna abu-abu, dan tepi yang lebih gelap.",
            pencegahan = "• Memilih varietas tanaman yang tahan terhadap hawar daun.\n• Singkirkan daun yang terinfeksi dan gugur.\n• Menjaga kebun tetap bersih dari gulma dan sisa tanaman.\n• Menyediakan sirkulasi udara dan pencahayaan yang cukup antar tanaman.\n• Menghindari penyiraman dari atas yang membasahi daun.\n• Membersihkan alat pertanian dan tempat penyimpanan secara berkala.\n• Menggunakan fungisida sesuai kebutuhan dan anjuran.\n• Memantau tanaman secara berkala untuk deteksi dini infeksi."
        )
        "Grape Healthy" -> DiseaseInfo(
            penyebab = "Daun anggur sehat tidak terinfeksi oleh patogen penyebab penyakit.",
            gejala = "Daun anggur sehat memiliki warna hijau yang konsisten, struktur yang utuh tanpa bercak atau bintik-bintik abnormal. Daun terlihat segar dengan pembuluh yang tersusun baik dan tidak ada tanda-tanda pengeringan atau perubahan warna.",
            pencegahan = "• Melakukan perawatan kebun secara teratur.\n• Memastikan nutrisi tanah yang seimbang.\n• Melakukan pemangkasan secara teratur untuk sirkulasi udara yang baik.\n• Menjaga irigasi yang tepat dan menghindari kelebihan air.\n• Melakukan rotasi tanaman dan sanitasi kebun yang baik.\n• Pemantauan hama dan penyakit secara berkala.\n• Menggunakan varietas anggur yang tahan terhadap penyakit umum di daerah tersebut."
        )
        "Not Anggur" -> DiseaseInfo(
            penyebab = "Gambar yang diunggah bukan daun anggur atau tidak dapat diidentifikasi sebagai daun anggur.",
            gejala = "Tidak relevan karena bukan daun anggur.",
            pencegahan = "Silakan unggah gambar daun anggur untuk mendapatkan hasil deteksi yang akurat."
        )
        else -> null
    }
}

@Composable
fun getColorForDisease(diseaseName: String): Color {
    return when (diseaseName) {
        "Grape Black Rot" -> Color(0xFFE53935)           // Merah
        "Grape Esca (Black Measles)" -> Color(0xFFEF6C00) // Oranye
        "Grape Leaf Blight (Isariopsis Leaf Spot)" -> Color(0xFFFBC02D) // Kuning
        "Grape Healthy" -> Color(0xFF43A047)           // Hijau
        "Not Anggur" -> Color(0xFF757575)           // Abu-abu
        else -> MaterialTheme.colorScheme.primary
    }
}

fun getConfidenceColor(confidence: Float): Color {
    return when {
        confidence >= 0.8f -> Color(0xFF4CAF50) // Hijau
        confidence >= 0.6f -> Color(0xFF8BC34A) // Hijau Muda
        confidence >= 0.4f -> Color(0xFFFFC107) // Kuning
        confidence >= 0.2f -> Color(0xFFFF9800) // Oranye
        else -> Color(0xFFF44336) // Merah
    }
}