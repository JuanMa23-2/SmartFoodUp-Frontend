package com.smartfoodup.app

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class AndroidLocationHelper(private val context: Context) : LocationHelper {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(onSuccess: (LocationData) -> Unit, onError: (String) -> Unit) {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(LocationData(location.latitude, location.longitude))
                } else {
                    onError("No se pudo obtener la ubicación actual.")
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error desconocido al obtener ubicación.")
            }
    }
}

@Composable
actual fun rememberLocationHelper(): LocationHelper {
    val context = LocalContext.current
    return remember(context) { AndroidLocationHelper(context) }
}

@Composable
actual fun LocationPermissionRequester(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
