package com.smartfoodup.app

import androidx.compose.runtime.Composable

data class ImagenSeleccionada(
    val nombre: String,
    val bytesBase64: String
)

// Añadimos la enumeración para identificar la procedencia de la captura
enum class OrigenImagen {
    GALERIA,
    CAMARA
}

@Composable
expect fun registrarImagePicker(
    origen: OrigenImagen,
    onImageSelected: (ImagenSeleccionada) -> Unit
): () -> Unit