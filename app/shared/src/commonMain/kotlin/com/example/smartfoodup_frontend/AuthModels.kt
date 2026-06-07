package com.example.smartfoodup_frontend

import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String
)

@Serializable
data class AuthResponse(
    val exitoso: Boolean,
    val mensaje: String,
    val nombre: String? = null // Mapeado perfectamente con el backend
)