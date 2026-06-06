package com.example.smartfoodup_frontend

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usamos un TextView nativo clásico de Android.
        // Cero dependencias de Compose, por lo que no dará ningún error en rojo.
        val vistaTexto = TextView(this).apply {
            text = "SmartFoodUp: Probando conexión en segundo plano...\nRevisa el Logcat abajo."
            textSize = 20f
            setPadding(50, 50, 50, 50)
        }
        setContentView(vistaTexto)

        // Ejecutamos la prueba de red en segundo plano hacia Railway
        lifecycleScope.launch {
            val resultado = realizarPruebaDeConexion()
            // Imprime la etiqueta gigante para que la busquemos en el Logcat
            println("📡 [PRUEBA_RED_SMARTFOODUP] -> $resultado")

            // Opcional: Actualiza la pantalla del emulador con el resultado real
            vistaTexto.text = "Resultado del Servidor:\n\n$resultado"
        }
    }

    private suspend fun realizarPruebaDeConexion(): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(SmartFoodApiService.BASE_URL)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "GET"
            conexion.connectTimeout = 5000
            conexion.readTimeout = 5000

            val codigoRespuesta = conexion.responseCode
            if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
                val lector = BufferedReader(InputStreamReader(conexion.inputStream))
                val respuestaCompleta = StringBuilder()
                var linea: String?

                while (lector.readLine().also { linea = it } != null) {
                    respuestaCompleta.append(linea)
                }
                lector.close()
                conexion.disconnect()

                "Conexión Exitosa -> $respuestaCompleta"
            } else {
                "Error del Servidor: Código $codigoRespuesta"
            }
        } catch (e: Exception) {
            "Error de Conexión al Backend: ${e.localizedMessage}"
        }
    }
}