package com.smartfoodup.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimateTrackingScreen(onNavigateBack: () -> Unit) {
    val locationHelper = rememberLocationHelper()
    val climateService = remember { ClimateService() }
    val scope = rememberCoroutineScope()

    var weatherData by remember { mutableStateOf<WeatherData?>(null) }
    var errorMensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var permisoConcedido by remember { mutableStateOf(false) }

    val cargarDatosUbicacion = {
        cargando = true
        locationHelper.getCurrentLocation(
            onSuccess = { loc ->
                scope.launch {
                    weatherData = climateService.getWeatherData(loc.latitude, loc.longitude)
                    cargando = false
                }
            },
            onError = { err ->
                errorMensaje = err
                cargando = false
            }
        )
    }

    LocationPermissionRequester(
        onPermissionGranted = {
            permisoConcedido = true
            cargarDatosUbicacion()
        },
        onPermissionDenied = {
            errorMensaje = "Se requieren permisos de ubicación para esta funcionalidad."
            cargando = false
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inteligencia Ambiental", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMensaje.isNotEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMensaje, color = MaterialTheme.colorScheme.error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título Ubicación
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = weatherData?.city ?: "Tu Ubicación",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Card Clima Actual
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${weatherData?.temperature?.toInt()}°C",
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = weatherData?.description?.uppercase() ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ClimaDetalleItem(Icons.Default.WaterDrop, "${weatherData?.humidity}%", "Humedad")
                            ClimaDetalleItem(Icons.Default.Cloud, weatherData?.description ?: "", "Cielo")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Pronóstico Próximas Horas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Lista de Pronóstico
                weatherData?.forecast?.forEach { day ->
                    ForecastRow(day)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ForecastRow(day: ForecastDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(day.time, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Cloud, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(day.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Text("${day.temp.toInt()}°C", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ClimaDetalleItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f))
    }
}

@Composable
expect fun LocationPermissionRequester(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
)
