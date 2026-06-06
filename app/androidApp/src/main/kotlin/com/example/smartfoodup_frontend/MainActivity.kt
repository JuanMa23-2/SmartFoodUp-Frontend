package com.example.smartfoodup_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Renderizamos el contenedor de Jetpack Compose en la Actividad Principal
        setContent {
            MaterialTheme {
                // 🔌 Mandamos a llamar tu nueva pantalla de Registro
                RegisterScreen(
                    onNavigateToLogin = {
                        // Aquí se controlará el salto al Login más adelante
                        println("El usuario presionó el enlace para ir al Login")
                    }
                )
            }
        }
    }
}