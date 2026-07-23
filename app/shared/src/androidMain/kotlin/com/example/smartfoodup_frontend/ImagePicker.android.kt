package com.smartfoodup.app

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import android.util.Base64

@Composable
actual fun registrarImagePicker(
    origen: OrigenImagen,
    onImageSelected: (ImagenSeleccionada) -> Unit
): () -> Unit {
    val contexto = LocalContext.current

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uriValida ->
            val infoImagen = obtenerDatosDeUri(contexto, uriValida)
            if (infoImagen != null) {
                onImageSelected(infoImagen)
            }
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { bmp ->
            val stream = ByteArrayOutputStream()
            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, stream)
            val bytes = stream.toByteArray()
            val stringBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            onImageSelected(
                ImagenSeleccionada(
                    nombre = "captura_camara_${System.currentTimeMillis()}.jpg",
                    bytesBase64 = stringBase64
                )
            )
        }
    }

    return remember {
        {
            if (origen == OrigenImagen.GALERIA) {
                galeriaLauncher.launch("image/*")
            } else {
                camaraLauncher.launch(null)
            }
        }
    }
}

// Función auxiliar encargada de interactuar con el ContentResolver mediante la URI provista
private fun obtenerDatosDeUri(contexto: Context, uri: Uri): ImagenSeleccionada? {
    val contentResolver = contexto.contentResolver
    var nombreArchivo = "imagen_alimento.jpg"

    // Consulta los metadatos del archivo a través del ContentResolver
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            nombreArchivo = cursor.getString(nameIndex)
        }
    }

    return try {
        // Abre el flujo de entrada del recurso mediante el ContentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val bytes = readAllBytes(inputStream)
            // Convierte el arreglo de bytes a una cadena Base64 compatible con Ktor en commonMain
            val stringBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            ImagenSeleccionada(nombre = nombreArchivo, bytesBase64 = stringBase64)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

// Lee los fragmentos de datos del archivo del sistema
private fun readAllBytes(inputStream: InputStream): ByteArray {
    val byteBuffer = ByteArrayOutputStream()
    val bufferSize = 1024
    val buffer = ByteArray(bufferSize)
    var len: Int
    while (inputStream.read(buffer).also { len = it } != -1) {
        byteBuffer.write(buffer, 0, len)
    }
    return byteBuffer.toByteArray()
}