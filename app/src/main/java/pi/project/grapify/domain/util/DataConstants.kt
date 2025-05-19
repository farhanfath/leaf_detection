package pi.project.grapify.domain.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pi.project.grapify.R
import pi.project.grapify.data.model.DiseaseInfo

// untuk filtering result data
/**
 * TODO: disini untuk berisi data2 terkait hasil disesase dari model seperti penyebab, gejala, pencegahan, dsb
 */
fun getDiseaseInfo(diseaseName: String): DiseaseInfo? {
    return when (diseaseName) {
        "Grape Black Rot" -> DiseaseInfo(
            nama = "Grape Black Rot",
            penyebab = "Penyakit ini disebabkan oleh jamur Phyllosticta ampelicida. Patogen tersebut biasanya menyerang saat musim dingin dan bertahan hidup pada pucuk yang terinfeksi atau buah kering yang terdapat pada tanaman anggur maupun di tanah. Spora jamur dilepaskan ketika terjadi hujan ringan dan kemudian tersebar melalui angin. Suhu optimal untuk pertumbuhan jamur ini adalah sekitar 25°C dengan kelembapan pada daun yang bertahan setidaknya selama enam jam. Kondisi cuaca yang hangat dan lembap sangat mendukung perkembangan jamur ini, sehingga berdampak pada penurunan hasil produksi buah anggur.",
            gejala = "Gejala penyakit ini ditandai dengan munculnya bintik-bintik tidak beraturan yang dikelilingi oleh garis berwarna gelap pada daun. Selain daun, gejala serupa juga dapat terlihat pada tunas, batang, dan tangkai daun. Jika infeksi terjadi pada tangkai daun, maka daun secara keseluruhan akan mengalami pengeringan. Pada buah anggur, infeksi ditandai dengan perubahan warna menjadi abu-abu di tahap awal, yang kemudian berkembang menjadi bintik-bintik berwarna cokelat kemerahan atau ungu. Seiring waktu, buah akan mengalami perubahan bentuk hingga akhirnya menjadi layu.",
            pencegahan = """
            • Gunakan metode pemangkasan alternatif seperti pemangkasan tertunda atau pemangkasan ganda.
            • Hindari pemangkasan selama periode hujan deras ketika spora cenderung tersebar.
            • Pantau kebun anggur di musim semi, dan cari tunas yang mati atau tunas yang kerdil.
            • Pada akhir musim panas, potong bagian tanaman anggur yang membusuk.
            • Singkirkan sisa-sisa tanaman anggur yang terserang penyakit dan musnahkan.
            • Tunda pembuahan selama beberapa tahun hingga tanaman merambat memiliki pertumbuhan akar dan tunas yang seimbang.
        """.trimIndent(),
            imageSample = R.drawable.img_black_rot
        )

        "Grape Esca (Black Measles)" -> DiseaseInfo(
            nama = "Grape Esca (Black Measles)",
            penyebab = "Penyakit ini dapat muncul kapan saja selama musim tanam dan disebabkan oleh jamur Togninia minima, meskipun beberapa jenis jamur lain seperti Phaeomoniella chlamydospora juga dapat berperan. Infeksi umumnya terjadi pada tanaman anggur yang masih muda, namun gejalanya sering baru tampak di kebun anggur saat tanaman berusia antara 5 hingga 7 tahun. Jamur ini bertahan selama musim dingin dalam struktur yang tertanam di jaringan kayu. Pada musim semi, spora diproduksi dan dilepaskan, kemudian menginfeksi luka yang dihasilkan dari aktivitas pemangkasan.",
            gejala = "Gejala penyakit ini ditandai dengan munculnya belang di antara pembuluh daun, yang disertai perubahan warna serta pengeringan jaringan di sekitar pembuluh utama. Pada varietas anggur berbuah merah, perubahan warna umumnya menjadi merah tua, sedangkan pada varietas berbuah putih, warna berubah menjadi kuning. Daun yang terinfeksi dapat mengalami pengeringan total dan rontok sebelum waktunya. Sementara itu, pada buah akan tampak bercak-bercak kecil berbentuk bulat berwarna gelap, yang sering kali dikelilingi oleh cincin berwarna cokelat keunguan.",
            pencegahan = """
            • Pilih varietas anggur yang lebih tahan penyakit jika tersedia.
            • Buang buah kering (mumi) dari pohon anggur.
            • Singkirkan dan musnahkan kayu serta sulur yang terinfeksi hama setelah panen.
            • Singkirkan daun-daun yang terkena serangan dari area kebun anggur.
            • Jaga kebersihan kebun anggur dengan membersihkan gulma.
            • Pastikan sirkulasi udara dan pencahayaan di kebun cukup.
            • Lakukan pemangkasan tanaman anggur setiap tahun sebelum fase vegetatif dimulai.
        """.trimIndent(),
            imageSample = R.drawable.img_esca
        )

        "Grape Leaf Blight (Isariopsis Leaf Spot)" -> DiseaseInfo(
            nama = "Grape Leaf Blight (Isariopsis Leaf Spot)",
            penyebab = "Penyakit ini disebabkan oleh jamur Peyronellaea glomerata, yang sebelumnya dikenal sebagai Phoma glomerata, dan menjadi nama umum untuk penyakit ini. Jamur ini tersebar luas dan mampu bertahan di tanah maupun pada berbagai bahan tanaman, baik yang masih hidup maupun sudah mati (seperti biji, buah-buahan, dan sayuran), biasanya tanpa menimbulkan gejala yang nyata. Selain itu, jamur ini juga dapat ditemukan di lingkungan dalam ruangan, seperti pada permukaan kayu, semen, bahan yang dicat minyak, dan kertas. Umumnya, jamur ini bertindak sebagai patogen sekunder pada jaringan tanaman yang telah rusak. Namun, dalam kondisi lingkungan tertentu, seperti cuaca lembap dan suhu tinggi, jamur ini dapat memicu timbulnya penyakit. Pertumbuhan optimalnya terjadi pada suhu antara 26°C hingga sedikit di bawah 37°C.",
            gejala = "Gejala infeksi hawar daun phoma umumnya terlihat pada daun-daun yang lebih tua. Daun yang terinfeksi memperlihatkan lesi tidak teratur berbentuk sudut dengan warna kuning hingga cokelat yang menyebar di seluruh permukaan daun. Seiring perkembangan penyakit, lesi tersebut membesar membentuk bercak yang kemudian berubah menjadi area nekrotik dengan warna kusam, pusat berwarna abu-abu, dan tepi yang lebih gelap. Pada tahap lanjut, daun menjadi layu dan akhirnya rontok. Beberapa tanaman lain, seperti tanaman merambat termasuk anggur, diketahui juga dapat menjadi inang alternatif bagi jamur ini.",
            pencegahan = """
            • Memilih varietas tanaman yang tahan terhadap hawar daun.
            • Singkirkan daun yang terinfeksi dan gugur.
            • Menjaga kebun tetap bersih dari gulma dan sisa tanaman.
            • Menyediakan sirkulasi udara dan pencahayaan yang cukup antar tanaman.
            • Menghindari penyiraman dari atas yang membasahi daun.
            • Membersihkan alat pertanian dan tempat penyimpanan secara berkala.
            • Menggunakan fungisida sesuai kebutuhan dan anjuran.
            • Memantau tanaman secara berkala untuk deteksi dini infeksi.
        """.trimIndent(),
            imageSample = R.drawable.img_leaf_blight
        )

        "Grape Healthy" -> DiseaseInfo(
            nama = "Grape Healthy",
            penyebab = "Daun anggur sehat tidak terinfeksi oleh patogen penyebab penyakit.",
            gejala = "Kondisi daun anggur terlihat normal dan tidak menunjukan tanda-tanda penyakit.",
            pencegahan = """
            • Lanjutkan perawatan rutin seperti penyiraman, pemangkasan, dan pemupukan.
            • Tetap pantau kondisi secara berkala.
            • Memastikan nutrisi tanah yang seimbang.
            • Melakukan pemangkasan secara teratur untuk sirkulasi udara yang baik.
            • Menjaga irigasi yang tepat dan menghindari kelebihan air.
            • Melakukan rotasi tanaman dan sanitasi kebun yang baik.
            • Pemantauan hama dan penyakit secara berkala.
            • Menggunakan varietas anggur yang tahan terhadap penyakit umum di daerah tersebut.
        """.trimIndent(),
            imageSample = R.drawable.img_grape_healthy
        )
        "Not Anggur" -> DiseaseInfo(
            nama = "Not Anggur",
            penyebab = "Gambar yang diunggah bukan daun anggur atau tidak dapat diidentifikasi sebagai daun anggur.",
            gejala = "Tidak relevan karena bukan daun anggur.",
            pencegahan = """
            • Pastikan foto yang diunggah jelas, fokus, dan menunjukan daun secara utuh 
            • Coba ulangi dengan sudut pengambilan gambar yang berbeda 
            • Pastikan gambar berasal dari tanaman anggur
            """.trimIndent(),
            imageSample = R.drawable.img_not_anggur
        )
        else -> null
    }
}

/**
 * TODO: disini untuk handling perihal warna yang akan ditampilkan berdasarkan output nama yang didapatkan
 */
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

/**
 * TODO: disini untuk handling tentang presentase yang didapatkan oleh hasil dari model
 */
fun getConfidenceColor(confidence: Float): Color {
    return when {
        confidence >= 0.8f -> Color(0xFF4CAF50) // Hijau
        confidence >= 0.6f -> Color(0xFF8BC34A) // Hijau Muda
        confidence >= 0.4f -> Color(0xFFFFC107) // Kuning
        confidence >= 0.2f -> Color(0xFFFF9800) // Oranye
        else -> Color(0xFFF44336) // Merah
    }
}

// untuk list penyakit
fun getDiseaseGlossary() : List<DiseaseInfo> {
    return listOf(
        getDiseaseInfo("Grape Black Rot")!!,
        getDiseaseInfo("Grape Esca (Black Measles)")!!,
        getDiseaseInfo("Grape Leaf Blight (Isariopsis Leaf Spot)")!!,
        getDiseaseInfo("Grape Healthy")!!,
        getDiseaseInfo("Not Anggur")!!
    )
}