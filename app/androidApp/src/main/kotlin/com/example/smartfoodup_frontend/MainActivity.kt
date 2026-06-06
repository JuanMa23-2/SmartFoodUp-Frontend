package com.example.smartfoodup_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Estado simple para controlar qué pantalla ver ("login", "register" o "home")
                var pantallaActual by remember { mutableStateOf("register") }

                when (pantallaActual) {
                    "register" -> RegisterScreen(
                        onNavigateToLogin = { pantallaActual = "login" }
                    )
                    "login" -> LoginScreen(
                        onNavigateToRegister = { pantallaActual = "register" },
                        onLoginSuccess = { pantallaActual = "home" }
                    )
                    "home" -> App()
                }
            }
        }
    }
}