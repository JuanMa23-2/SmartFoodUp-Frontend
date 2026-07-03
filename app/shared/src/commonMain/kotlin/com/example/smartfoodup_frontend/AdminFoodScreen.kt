package com.example.smartfoodup_frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFoodScreen(onNavigateBack: () -> Unit) {
    var nombreAlimento by remember { mutableStateOf("") }
    var categoriaAlimento by remember { mutableStateOf("") }
    var cantidadAlimento by remember { mutableStateOf("") }

    var imagenSeleccionada by remember { mutableStateOf<ImagenSeleccionada?>(null) }
    var mensajeEstado by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val apiService = remember { SmartFoodApiService() }

    // Configuracion de los disparadores basados en el enumerador de origen
    val lanzarGaleria = registrarImagePicker(OrigenImagen.GALERIA) { imagen ->
        imagenSeleccionada = imagen
    }

    val lanzarCamara = registrarImagePicker(OrigenImagen.CAMARA) { imagen ->
        imagenSeleccionada = imagen
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alta de Alimentos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !cargando) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Fastfood,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Catálogo de Inventario",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Agregue nuevos elementos al dataset de control de frescura.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombreAlimento,
                onValueChange = { nombreAlimento = it },
                label = { Text("Nombre del Alimento") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = categoriaAlimento,
                onValueChange = { categoriaAlimento = it },
                label = { Text("Categoría (Ej. Verduras, Carnes)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cantidadAlimento,
                onValueChange = { cantidadAlimento = it },
                label = { Text("Cantidad Inicial de Lote") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Origen de la Imagen del Alimento",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // Fila de distribucion para los proveedores de contenido y captura nativa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Caja interactiva para Galeria
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .clickable(enabled = !cargando) { lanzarGaleria() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Galería", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Caja interactiva para Camara en vivo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .clickable(enabled = !cargando) { lanzarCamara() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tomar Foto", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Indicador visual descriptivo del archivo cargado en memoria
            if (imagenSeleccionada != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Archivo cargado: ${imagenSeleccionada?.nombre}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            if (mensajeEstado.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensajeEstado,
                    color = if (mensajeEstado.contains("exitosamente")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val cantidadNum = cantidadAlimento.toIntOrNull()
                    if (nombreAlimento.isBlank() || categoriaAlimento.isBlank() || cantidadNum == null) {
                        mensajeEstado = "Por favor, complete todos los campos de forma correcta."
                    } else {
                        mensajeEstado = ""
                        cargando = true

                        scope.launch {
                            val request = AlimentoRequest(
                                nombre = nombreAlimento,
                                categoria = categoriaAlimento,
                                cantidad = cantidadNum,
                                imagenBytesBase64 = imagenSeleccionada?.bytesBase64
                            )
                            val respuesta = apiService.registrarAlimento(request)
                            cargando = false
                            mensajeEstado = respuesta.mensaje

                            if (respuesta.exitoso) {
                                nombreAlimento = ""
                                categoriaAlimento = ""
                                cantidadAlimento = ""
                                imagenSeleccionada = null
                            }
                        }
                    }
                },
                enabled = !cargando,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrar Alimento", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}