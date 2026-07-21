package com.example.smartfoodup_frontend

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Llamamos directamente a la función App compartida, 
            // que ya gestiona internamente el Login, Registro y Dashboard.
            App()
        }
    }
}