package com.example.smartfoodup_frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import smartfoodup_frontend.app.shared.generated.resources.Res
import smartfoodup_frontend.app.shared.generated.resources.logo_smartfoodup
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(nombreUsuario: String, onCerrarSesion: () -> Unit) { // 👈 Recibe el nombre dinámico
    val colorPrimario = MaterialTheme.colorScheme.primary
    val colorFondo = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SmartFood UP",
                        fontWeight = FontWeight.Bold,
                        color = colorPrimario,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    Icon(
                        painter = painterResource(Res.drawable.logo_smartfoodup),
                        contentDescription = "Logo Pequeño",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp),
                        tint = Color.Unspecified
                    )

                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bienvenida Dinámica
            Text(
                text = "¡Hola de nuevo, $nombreUsuario!", // 👈 Nombre inyectado directamente aquí
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
            )
            Text(
                text = "Monitoreo general de frescura en tiempo real.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica("Sensores Activos", "5", colorPrimario, Modifier.weight(1f))
                TarjetaMetrica("Alertas Críticas", "0", MaterialTheme.colorScheme.error, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Último Análisis de IA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Estado general óptimo. El refrigerador principal mantiene un índice de humedad estable.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaMetrica(titulo: String, valor: String, colorIcono: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(colorIcono, shape = RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = valor, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = titulo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}