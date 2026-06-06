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

// Cliente HTTP global configurado para Ktor
val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // Ignora campos nuevos que mande el backend y no use el front
            prettyPrint = true
            isLenient = true
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