package com.example.smartfoodup

import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val exitoso: Boolean,
    val mensaje: String,
    val usuarioId: Int? = null
)