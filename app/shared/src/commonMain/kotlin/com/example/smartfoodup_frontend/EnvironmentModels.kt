package com.smartfoodup.app

import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val city: String,
    val temperature: Double,
    val humidity: Int,
    val description: String,
    val forecast: List<ForecastDay> = emptyList()
)

@Serializable
data class ForecastDay(
    val time: String,
    val temp: Double,
    val humidity: Int,
    val description: String
)

@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double
)
