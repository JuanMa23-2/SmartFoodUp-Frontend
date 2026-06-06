package com.example.smartfoodup_frontend

/**
 * Archivo de conexión global para SmartFoodUp.
 * Centraliza la URL pública de Railway para que todas las pantallas de la app
 * apunten al mismo servidor en la nube.
 */
object SmartFoodApiService {

    const val BASE_URL = "https://smartfoodup-production.up.railway.app"

    //caminos a los endpoints futuros:
    const val ROUTE_REGISTRAR = "$BASE_URL/registrar"
    const val ROUTE_MEDICIONES = "$BASE_URL/mediciones"
    const val ROUTE_IA_ANALISIS = "$BASE_URL/analisis-ia"
}