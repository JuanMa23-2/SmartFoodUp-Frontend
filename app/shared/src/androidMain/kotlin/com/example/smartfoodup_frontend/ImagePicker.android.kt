package com.example.smartfoodup_frontend

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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

    // Launcher 1: Diseñado para abrir el Content Provider de la Galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uriValida ->
            val infoImagen = obtenerDatosDeUri(contexto, uriValida)
            if (infoImagen != null) onImageSelected(infoImagen)
        }
    }

    // Launcher 2: Diseñado para capturar la foto directamente desde la Cámara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { bitmapValido ->
            val infoImagen = procesarBitmapCamara(bitmapValido)
            if (infoImagen != null) onImageSelected(infoImagen)
        }
    }

    // Retorna la acción de ejecución dependiendo de la selección del administrador
    return remember(origen) {
        {
            if (origen == OrigenImagen.GALERIA) {
                galeriaLauncher.launch("image/*")
            } else {
                camaraLauncher.launch()
            }
        }
    }
}

private fun obtenerDatosDeUri(contexto: Context, uri: Uri): ImagenSeleccionada? {
    val contentResolver = contexto.contentResolver
    var nombreArchivo = "imagen_galeria.jpg"

    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            nombreArchivo = cursor.getString(nameIndex)
        }
    }

    return try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val bytes = readAllBytes(inputStream)
            val stringBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            ImagenSeleccionada(nombre = nombreArchivo, bytesBase64 = stringBase64)
        } else null
    } catch (e: Exception) {
        null
    }
}

// Procesa el mapa de bits devuelto por la cámara y lo serializa a Base64 en memoria
private fun procesarBitmapCamara(bitmap: Bitmap): ImagenSeleccionada? {
    return try {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val bytes = stream.toByteArray()
        val stringBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        ImagenSeleccionada(nombre = "captura_camara.jpg", bytesBase64 = stringBase64)
    } catch (e: Exception) {
        null
    }
}

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