package com.example.smartfoodup

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SmartFoodApiService {

    // Cliente HTTP global configurado para entender JSON automáticamente
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                cleanNames = true
            })
        }
    }

    companion object {
        // BACKEND EN LA NUBE
        // NOTA: Reemplaza esto exactamente por tu dominio real de Railway (ej. "https://smartfoodup-production.up.railway.app")
        const val BASE_URL = "https://smartfoodup-production.up.railway.app"
    }

    /**
     * Envía la solicitud de registro al servidor local o en la nube
     */
    suspend fun registrarUsuario(request: RegistroRequest): AuthResponse {
        return try {
            val response = client.post("$BASE_URL/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            // Mapea automáticamente el JSON de respuesta a nuestro objeto AuthResponse
            response.body()
        } catch (e: Exception) {
            AuthResponse(false, "Fallo de conexión: ${e.message}")
        }
    }

    /**
     * Envía la solicitud de inicio de sesión al servidor
     */
    suspend fun iniciarSesion(request: RegistroRequest): AuthResponse {
        return try {
            val response = client.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            AuthResponse(false, "Fallo de conexión: ${e.message}")
        }
    }
}