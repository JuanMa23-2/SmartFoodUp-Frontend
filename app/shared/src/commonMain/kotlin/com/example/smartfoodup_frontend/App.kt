package com.smartfoodup.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

@Composable
fun App() {
    SmartFoodUpTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                },
                onNavigateToAdminFood = {
                    pantallaActual = "admin_food"
                },
                onNavigateToClimateTracking = {
                    pantallaActual = "climate_tracking"
                }
            )
            "admin_register" -> AdminRegisterScreen(
                onNavigateBack = {
                    pantallaActual = "dashboard"
                }
            )
            "admin_food" -> AdminFoodScreen(
                onNavigateBack = {
                    pantallaActual = "dashboard"
                }
            )
            "climate_tracking" -> ClimateTrackingScreen(
                onNavigateBack = {
                    pantallaActual = "dashboard"
                }
            )
        }
    }
}
}
