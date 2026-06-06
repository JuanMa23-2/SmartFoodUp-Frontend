package com.example.smartfoodup_frontend

import kotlinx.serialization.Serializable // 👈 1. IMPORTANTE: Agrega esta importación

@Serializable // 👈 2. IMPORTANTE: Agrega esta anotación aquí arriba
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String
)

@Serializable // 👈 3. También agrégala aquí para cuando manejes la respuesta del servidor
data class AuthResponse(
    val exitoso: Boolean,
    val mensaje: String,
    val token: String? = null
)