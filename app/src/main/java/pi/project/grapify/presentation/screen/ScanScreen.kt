package pi.project.grapify.presentation.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import pi.project.grapify.helper.ModelInspectionHelper
import pi.project.grapify.ml.GrapeleafdiseaseModel
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.max

// Daftar kelas penyakit daun anggur
val CLASS_NAMES = listOf(
    "Grape Black Rot",
    "Grape Esca (Black Measles)",
    "Grape Leaf Blight (Isariopsis Leaf Spot)",
    "Grape Healthy",
    "Not Anggur"
)

fun preProcessImage(bitmap: Bitmap): ByteBuffer {
    // Alokasi ByteBuffer untuk menyimpan nilai piksel gambar
    val inputSize = 224
    val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
    byteBuffer.order(ByteOrder.nativeOrder())

    // Mendapatkan nilai piksel dari bitmap dan menormalisasi
    val intValues = IntArray(inputSize * inputSize)
    bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    // Log informasi preprocessing
    android.util.Log.d("Image Preprocessing", "Preprocessing image of size ${bitmap.width}x${bitmap.height} to ${inputSize}x${inputSize}")

    for (i in 0 until inputSize) {
        for (j in 0 until inputSize) {
            val pixelValue = intValues[i * inputSize + j]

            // Ekstrak dan normalisasi nilai RGB (normalisasi dari 0-255 ke 0-1)
            byteBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixelValue and 0xFF) / 255.0f)
        }
    }

    // Reset buffer position untuk pembacaan
    byteBuffer.rewind()

    // Log bahwa preprocessing selesai
    android.util.Log.d("Image Preprocessing", "Image preprocessing completed")

    return byteBuffer
}

fun runInference(context: Context, bitmap: Bitmap): FloatArray {
    try {
        // Preprocess gambar
        val byteBuffer = preProcessImage(bitmap)

        // Menggunakan TensorFlow Lite Interpreter untuk model yang dihasilkan dari ML Model Binding
        val model = GrapeleafdiseaseModel.newInstance(context)

        // Menyiapkan input
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Menjalankan inferensi
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Mengambil hasil mentah sebagai FloatArray
        val rawOutputs = outputFeature0.floatArray

        // Log hasil mentah secara detail
        logDetailedRawOutput(rawOutputs)

        // Melepaskan sumber daya model
        model.close()

        return rawOutputs
    } catch (e: Exception) {
        // Log detail error
        android.util.Log.e("TFLite Error", "Error saat menjalankan model: ${e.message}")
        android.util.Log.e("TFLite Error", "Stack trace: ${e.stackTraceToString()}")

        return FloatArray(CLASS_NAMES.size) { 0f }
    }
}

// Fungsi untuk logging hasil mentah dari model secara lebih detail
fun logDetailedRawOutput(results: FloatArray) {
    val tag = "ModelRawOutput"

    // Menampilkan raw output dalam berbagai format

    // 1. Format output mentah sebagai array
    android.util.Log.d(tag, "Raw output as array: ${results.contentToString()}")

    // 2. Format output mentah sebagai pasangan indeks-nilai
    val sb = StringBuilder("Raw output by index:\n")
    for (i in results.indices) {
        sb.append("Index $i: ${results[i]}\n")
    }
    android.util.Log.d(tag, sb.toString())

    // 3. Jika kita memiliki label, hubungkan dengan output
    if (results.size == CLASS_NAMES.size) {
        val labeledSb = StringBuilder("Raw output with labels:\n")
        labeledSb.append("{\n")
        for (i in results.indices) {
            labeledSb.append("  \"${CLASS_NAMES[i]}\": ${results[i]}")
            if (i < results.size - 1) labeledSb.append(",")
            labeledSb.append("\n")
        }
        labeledSb.append("}")
        android.util.Log.d(tag, labeledSb.toString())
    }

    // 4. Jika model menggunakan softmax, tampilkan juga dalam persentase
    val softmaxSb = StringBuilder("Output as probabilities (%):\n")
    for (i in results.indices) {
        val percent = results[i] * 100
        softmaxSb.append("Index $i")
        if (i < CLASS_NAMES.size) {
            softmaxSb.append(" (${CLASS_NAMES[i]})")
        }
        softmaxSb.append(": ${String.format("%.2f%%", percent)}\n")
    }
    android.util.Log.d(tag, softmaxSb.toString())

    // 5. Mencari nilai maksimum dan indeksnya
    val maxIndex = results.indices.maxByOrNull { results[it] } ?: 0
    android.util.Log.d(tag, "Max value: ${results[maxIndex]} at index $maxIndex")
    if (maxIndex < CLASS_NAMES.size) {
        android.util.Log.d(tag, "Corresponding label: ${CLASS_NAMES[maxIndex]}")
    }
}

// Menambahkan UI untuk menampilkan output mentah
@Composable
fun GrapeLeafDiseaseDetectionScreen() {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var predictionResult by remember { mutableStateOf("") }
    var confidenceScores by remember { mutableStateOf<List<Pair<String, Float>>>(emptyList()) }
    var rawOutputText by remember { mutableStateOf("") } // Untuk menampilkan output mentah
    var showRawOutput by remember { mutableStateOf(false) } // Toggle untuk menampilkan output mentah

    // Inspect model at startup
    LaunchedEffect(Unit) {
        ModelInspectionHelper.inspectModel(context, "grapeleafdisease_model.tflite")
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            }

            bitmap?.let { bm ->
                val resizedBitmap = Bitmap.createScaledBitmap(bm, 224, 224, true)
                val result = runInference(context, resizedBitmap)

                // Menyimpan output mentah untuk ditampilkan di UI
                rawOutputText = buildString {
                    append("Output Mentah Model:\n")
                    append("[\n")
                    result.forEachIndexed { index, value ->
                        append("  $index: $value")
                        if (index < result.size - 1) append(",")
                        append("\n")
                    }
                    append("]\n\n")

                    append("Output dengan Label:\n")
                    result.forEachIndexed { index, value ->
                        if (index < CLASS_NAMES.size) {
                            append("  ${CLASS_NAMES[index]}: $value (${String.format("%.2f%%", value * 100)})\n")
                        } else {
                            append("  Unknown-$index: $value (${String.format("%.2f%%", value * 100)})\n")
                        }
                    }
                }

                // Mengambil indeks tertinggi sebagai prediksi
                val maxIndex = result.indices.maxByOrNull { result[it] } ?: 0
                predictionResult = if (maxIndex < CLASS_NAMES.size) CLASS_NAMES[maxIndex] else "Unknown-$maxIndex"

                // Menyiapkan daftar semua kelas dengan skor keyakinan untuk ditampilkan
                confidenceScores = result.mapIndexed { index, value ->
                    val label = if (index < CLASS_NAMES.size) CLASS_NAMES[index] else "Unknown-$index"
                    Pair(label, value)
                }.sortedByDescending { it.second }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Deteksi Penyakit Daun Anggur",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Pilih Gambar")
            }

            bitmap?.let { bm ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = bm.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(bottom = 8.dp)
                        )

                        if (predictionResult.isNotEmpty()) {
                            Text(
                                text = "Hasil Deteksi: $predictionResult",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Skor Keyakinan:",
                                style = MaterialTheme.typography.titleMedium
                            )

                            confidenceScores.forEach { (className, score) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = className)
                                    Text(
                                        text = String.format("%.2f%%", score * 100),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Tombol untuk menampilkan/menyembunyikan output mentah
                            OutlinedButton(
                                onClick = { showRawOutput = !showRawOutput },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(if (showRawOutput) "Sembunyikan Output Mentah" else "Tampilkan Output Mentah")
                            }

                            if (showRawOutput && rawOutputText.isNotEmpty()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = rawOutputText,
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


