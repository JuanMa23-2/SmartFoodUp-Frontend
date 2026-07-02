package com.example.smartfoodup_frontend

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*

@Composable
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf("login") }

        // Variables reactivas para almacenar los datos del usuario activo
        var usuarioLogueado by remember { mutableStateOf("") }
        var rolUsuarioLogueado by remember { mutableStateOf("CLIENTE") }

        when (pantallaActual) {
            "login" -> LoginScreen(
                onNavigateToRegister = { pantallaActual = "register" },
                onLoginSuccess = { nombreRecibido, rolRecibido ->
                    usuarioLogueado = nombreRecibido
                    rolUsuarioLogueado = rolRecibido
                    pantallaActual = "dashboard"
                }
            )
            "register" -> RegisterScreen(
                onNavigateToLogin = { pantallaActual = "login" },
                onRegisterSuccess = { nombreRecibido, rolRecibido ->
                    usuarioLogueado = nombreRecibido
                    rolUsuarioLogueado = rolRecibido
                    pantallaActual = "dashboard"
                }
            )
            "dashboard" -> DashboardScreen(
                nombreUsuario = usuarioLogueado,
                rolUsuario = rolUsuarioLogueado,
                onCerrarSesion = {
                    usuarioLogueado = ""
                    rolUsuarioLogueado = "CLIENTE"
                    pantallaActual = "login"
                },
                onNavigateToAdminRegister = {
                    pantallaActual = "admin_register"
                }
            )
            "admin_register" -> AdminRegisterScreen(
                onNavigateBack = {
                    pantallaActual = "dashboard"
                }
            )
        }
    }
}