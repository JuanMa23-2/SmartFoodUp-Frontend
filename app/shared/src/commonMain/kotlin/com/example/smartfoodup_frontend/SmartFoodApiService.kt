package com.example.smartfoodup_frontend

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.*
import kotlinx.serialization.Serializable

// Modelos de datos compartidos de autenticación
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
    val nombre: String? = null,
    val rol: String? = null
)

val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
        })
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}

const val BASE_URL = "https://smartfoodup-production.up.railway.app"

class SmartFoodApiService {

    suspend fun registrarUsuario(request: RegistroRequest): AuthResponse {
        return try {
            val response = client.post("$BASE_URL/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            AuthResponse(exitoso = false, mensaje = "Fallo de conexión: ${e.message}")
        }
    }

    suspend fun iniciarSesion(request: RegistroRequest): AuthResponse {
        return try {
            val response = client.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            AuthResponse(exitoso = false, mensaje = "Fallo de conexión: ${e.message}")
        }
    }

    // Registro exclusivo ejecutado por Administradores
    suspend fun adminRegistrarUsuario(request: AdminRegistroRequest): AuthResponse {
        return try {
            val response = client.post("$BASE_URL/auth/admin-register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            AuthResponse(exitoso = false, mensaje = "Fallo de conexión: ${e.message}")
        }
    }
}