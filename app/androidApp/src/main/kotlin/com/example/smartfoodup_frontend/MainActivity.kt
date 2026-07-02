package com.example.smartfoodup_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Llamamos directamente a la función App compartida, 
            // que ya gestiona internamente el Login, Registro y Dashboard.
            App()
        }
    }
}