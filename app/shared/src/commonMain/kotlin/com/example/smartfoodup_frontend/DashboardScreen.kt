package com.smartfoodup.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import smartfoodup_frontend.app.shared.generated.resources.Res
import smartfoodup_frontend.app.shared.generated.resources.logo_smartfoodup
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    nombreUsuario: String,
    rolUsuario: String,
    onCerrarSesion: () -> Unit,
    onNavigateToAdminRegister: () -> Unit,
    onNavigateToAdminFood: () -> Unit,
    onNavigateToClimateTracking: () -> Unit
) {
    val colorPrimario = MaterialTheme.colorScheme.primary
    val colorFondo = MaterialTheme.colorScheme.surfaceVariant

    // Gestión de biometría
    val biometricHelper = rememberBiometricHelper()
    var huellaActiva by remember { mutableStateOf(biometricHelper.getSavedEmail() != null) }
    var facialActivo by remember { mutableStateOf(biometricHelper.getSavedEmail() != null) }
    
    var mostrarMenuAjustes by remember { mutableStateOf(false) }
    var tipoActivacionActual by remember { mutableStateOf("") } // "HUELLA" o "FACIAL"

    var mostrarDialogoContrasena by remember { mutableStateOf(false) }
    var inputCorreoConfirmacion by remember { mutableStateOf("") }
    var inputContraConfirmacion by remember { mutableStateOf("") }
    var errorDialogo by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.logo_smartfoodup),
                            contentDescription = "Logo SmartFoodUP",
                            modifier = Modifier.size(45.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "SmartFood UP",
                            fontWeight = FontWeight.Bold,
                            color = colorPrimario,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarMenuAjustes = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = colorPrimario
                        )
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "¡Hola de nuevo, $nombreUsuario!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                val colorEtiqueta = if (rolUsuario == "ADMIN") Color(0xFFD32F2F) else Color(0xFF388E3C)
                Box(
                    modifier = Modifier
                        .background(color = colorEtiqueta, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = rolUsuario,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Monitoreo general de frescura en tiempo real.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Sensores Activos", "5", colorPrimario, Modifier.weight(1f))
                TarjetaMetrica("Alertas Críticas", "0", MaterialTheme.colorScheme.error, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NUEVO: Botón de Inteligencia Ambiental
            Button(
                onClick = onNavigateToClimateTracking,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(Icons.Default.Cloud, null)
                Spacer(Modifier.width(8.dp))
                Text("Clima y Trazabilidad Regional", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Último Análisis de IA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Estado general óptimo. El refrigerador principal mantiene un índice de humedad estable.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MENÚ DE AJUSTES (AQUÍ ESTÁ LA BIOMETRÍA AHORA)
            if (mostrarMenuAjustes) {
                ModalBottomSheet(
                    onDismissRequest = { mostrarMenuAjustes = false },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "Configuración de Acceso",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorPrimario
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Activa los métodos seguros para entrar sin contraseña.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Opción 1: Huella Dactilar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Fingerprint, null, tint = colorPrimario)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Huella Dactilar", fontWeight = FontWeight.Bold)
                                Text("Usar el sensor de huellas", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = huellaActiva,
                                onCheckedChange = { activo ->
                                    if (activo) {
                                        tipoActivacionActual = "HUELLA"
                                        mostrarDialogoContrasena = true
                                    } else {
                                        biometricHelper.clearCredentials()
                                        huellaActiva = false
                                        facialActivo = false
                                    }
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                        // Opción 2: Reconocimiento Facial
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Face, null, tint = colorPrimario)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Reconocimiento Facial", fontWeight = FontWeight.Bold)
                                Text("Usar escaneo de rostro", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = facialActivo,
                                onCheckedChange = { activo ->
                                    if (activo) {
                                        tipoActivacionActual = "FACIAL"
                                        mostrarDialogoContrasena = true
                                    } else {
                                        biometricHelper.clearCredentials()
                                        huellaActiva = false
                                        facialActivo = false
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // SECCIÓN EXCLUSIVA PARA ADMINISTRADORES
            if (rolUsuario == "ADMIN") {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Panel de Control Administrativo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón 1: Registrar Personal
                    Button(
                        onClick = onNavigateToAdminRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Agregar usuario"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Registrar Nuevo Usuario",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Botón 2: Registrar Alimento (Catálogo)
                    Button(
                        onClick = onNavigateToAdminFood,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = "Agregar alimento"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Registrar Alimento al Catálogo",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DE CONFIGURACIÓN SEGURA PARA ENLAZAR LA BIOMETRÍA
    if (mostrarDialogoContrasena) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoContrasena = false
                errorDialogo = ""
            },
            title = { Text("Activar Acceso Biométrico", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Por favor, introduce tu correo y contraseña actuales. Al hacerlo, se guardarán encriptados bajo la protección del chip criptográfico de tu teléfono.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inputCorreoConfirmacion,
                        onValueChange = { inputCorreoConfirmacion = it },
                        label = { Text("Confirma Correo") },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        value = inputContraConfirmacion,
                        onValueChange = { inputContraConfirmacion = it },
                        label = { Text("Ingresa Contraseña") },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    if (errorDialogo.isNotEmpty()) {
                        Text(errorDialogo, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputCorreoConfirmacion.isBlank() || inputContraConfirmacion.isBlank()) {
                            errorDialogo = "Todos los campos son obligatorios."
                        } else {
                            val tituloPrompt = if (tipoActivacionActual == "HUELLA") "Configurar Huella" else "Configurar Rostro"
                            val subtituloPrompt = if (tipoActivacionActual == "HUELLA") "Escanea tu dedo para confirmar" else "Mira a la cámara para confirmar"

                            // Solicita confirmación biométrica antes de guardar las credenciales
                            biometricHelper.authenticate(
                                title = tituloPrompt,
                                subtitle = subtituloPrompt,
                                onSuccess = {
                                    biometricHelper.saveCredentials(inputCorreoConfirmacion, inputContraConfirmacion)
                                    if (tipoActivacionActual == "HUELLA") {
                                        huellaActiva = true
                                        facialActivo = false // Solo permitimos uno a la vez por simplicidad en el guardado
                                    } else {
                                        facialActivo = true
                                        huellaActiva = false
                                    }
                                    mostrarDialogoContrasena = false
                                    mostrarMenuAjustes = false
                                    inputCorreoConfirmacion = ""
                                    inputContraConfirmacion = ""
                                    errorDialogo = ""
                                },
                                onError = { error ->
                                    errorDialogo = error
                                }
                            )
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoContrasena = false
                    errorDialogo = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TarjetaMetrica(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
