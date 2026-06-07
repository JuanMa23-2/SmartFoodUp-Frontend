package com.example.smartfoodup_frontend

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf("login") }
        //  Variable reactiva para almacenar el nombre del usuario activo
        var usuarioLogueado by remember { mutableStateOf("") }

        when (pantallaActual) {
            "login" -> LoginScreen(
                onNavigateToRegister = { pantallaActual = "register" },
                onLoginSuccess = { nombreRecibido ->
                    usuarioLogueado = nombreRecibido // Guarda el nombre
                    pantallaActual = "dashboard"
                }
            )
            "register" -> RegisterScreen(
                onNavigateToLogin = { pantallaActual = "login" },
                onRegisterSuccess = { nombreRecibido ->
                    usuarioLogueado = nombreRecibido // Va directo al dashboard con su nombre
                    pantallaActual = "dashboard"
                }
            )
            "dashboard" -> DashboardScreen(
                nombreUsuario = usuarioLogueado, // Pasa el nombre real de la base de datos
                onCerrarSesion = {
                    usuarioLogueado = ""
                    pantallaActual = "login"
                }
            )
        }
    }
}