package com.smartfoodup.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

import smartfoodup_frontend.app.shared.generated.resources.Res
import smartfoodup_frontend.app.shared.generated.resources.logo_smartfoodup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRegisterScreen(onNavigateBack: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estados para el manejo del rol e interfaz del DropdownMenu
    var rolSeleccionado by remember { mutableStateOf("CLIENTE") }
    var menuExpandido by remember { mutableStateOf(false) }
    val opcionesRoles = listOf("CLIENTE", "ADMIN")

    var mensajeEstado by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val apiService = remember { SmartFoodApiService() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Personal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !cargando) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al Dashboard"
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

            Image(
                painter = painterResource(Res.drawable.logo_smartfoodup),
                contentDescription = "Logo SmartFoodUp",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Alta de Cuentas",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Herramienta administrativa para la creación de usuarios con asignación de privilegios.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector desplegable nativo de Jetpack Compose para asignar el Rol de la Base de Datos
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { if (!cargando) menuExpandido = !menuExpandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = rolSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol Asignado") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    opcionesRoles.forEach { opción ->
                        DropdownMenuItem(
                            text = { Text(opción) },
                            onClick = {
                                rolSeleccionado = opción
                                menuExpandido = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
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
                    if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                        mensajeEstado = "Por favor, llene todos los campos."
                    } else {
                        mensajeEstado = ""
                        cargando = true

                        scope.launch {
                            try {
                                val requestData = AdminRegistroRequest(
                                    nombre = nombre,
                                    email = email,
                                    contrasena = password,
                                    rol = rolSeleccionado
                                )
                                val resultado = apiService.adminRegistrarUsuario(requestData)

                                cargando = false
                                mensajeEstado = resultado.mensaje

                                if (resultado.exitoso) {
                                    nombre = ""
                                    email = ""
                                    password = ""
                                    rolSeleccionado = "CLIENTE"
                                }
                            } catch (e: Exception) {
                                cargando = false
                                mensajeEstado = "Error de red local: ${e.message}"
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
                    Text("Registrar Usuario", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}