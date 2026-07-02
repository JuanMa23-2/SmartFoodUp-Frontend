package com.example.smartfoodup_frontend

import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String
)

@Serializable
data class AdminRegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val rol: String // El administrador puede elegir explícitamente el rol
)

@Serializable
data class AuthResponse(
    val exitoso: Boolean,
    val mensaje: String,
    val token: String? = null,
    val usuarioId: Int? = null,
    val nombre: String? = null,
    val email: String? = null,
    val rol: String? = null // Campo necesario para guardar si eres ADMIN o CLIENTE
)