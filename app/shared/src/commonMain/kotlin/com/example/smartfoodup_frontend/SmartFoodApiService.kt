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
import kotlinx.serialization.Serializable

//Modelos de datos para el flujo de autenticación
@Serializable
data class RegistroRequest(val nombre: String, val email: String, val contrasena: String)

@Serializable
data class AuthResponse(val exitoso: Boolean, val mensaje: String)

//Cliente HTTP global configurado para Ktor 3.5.0
val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
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
}