package com.smartfoodup.app

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

@Serializable
data class OpenWeatherResponse(
    val name: String,
    val main: MainWeather,
    val weather: List<WeatherDescription>
)

@Serializable
data class MainWeather(
    val temp: Double,
    val humidity: Int
)

@Serializable
data class WeatherDescription(
    val description: String
)

@Serializable
data class ForecastResponse(
    val list: List<ForecastItem>
)

@Serializable
data class ForecastItem(
    val dt: Long,
    val main: MainWeather,
    val weather: List<WeatherDescription>,
    val dt_txt: String
)

class ClimateService {
    private val apiKey = "88fd8635aa24da15f1b4bfe361f93e37"

    suspend fun getWeatherData(lat: Double, lon: Double): WeatherData {
        return try {
            val current: OpenWeatherResponse = client.get("https://api.openweathermap.org/data/2.5/weather") {
                parameter("lat", lat)
                parameter("lon", lon)
                parameter("appid", apiKey)
                parameter("units", "metric")
                parameter("lang", "es")
            }.body()

            val forecastRaw: ForecastResponse = client.get("https://api.openweathermap.org/data/2.5/forecast") {
                parameter("lat", lat)
                parameter("lon", lon)
                parameter("appid", apiKey)
                parameter("units", "metric")
                parameter("lang", "es")
            }.body()

            val forecastDays = forecastRaw.list.take(8).map { item ->
                ForecastDay(
                    time = item.dt_txt.substringAfter(" ").substringBeforeLast(":"),
                    temp = item.main.temp,
                    humidity = item.main.humidity,
                    description = item.weather.firstOrNull()?.description ?: ""
                )
            }

            WeatherData(
                city = current.name,
                temperature = current.main.temp,
                humidity = current.main.humidity,
                description = current.weather.firstOrNull()?.description ?: "N/A",
                forecast = forecastDays
            )
        } catch (e: Exception) {
            WeatherData("Ubicación", 0.0, 0, "Error de conexión", emptyList())
        }
    }
}
