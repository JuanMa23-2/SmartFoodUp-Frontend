package com.smartfoodup.app

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

@Serializable
data class AlimentoRequest(
    val nombre: String,
    val categoria: String,
    val cantidad: Int,
    val imagenBytesBase64: String? = null // Permite enviar la foto serializada como texto de forma multiplataforma
)

@Serializable
data class AlimentoResponse(
    val exitoso: Boolean,
    val mensaje: String
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

    suspend fun iniciarSesion(request: LoginRequest): AuthResponse {
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

    //Registrar Alimento en el Catálogo desde la Capa Compartida
    suspend fun registrarAlimento(request: AlimentoRequest): AlimentoResponse {
        return try {
            val response = client.post("$BASE_URL/api/alimentos") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body()
        } catch (e: Exception) {
            AlimentoResponse(exitoso = false, mensaje = "Error de red: ${e.message}")
        }
    }
}