package com.smartfoodup.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

class IosLocationHelper : LocationHelper {
    override fun getCurrentLocation(onSuccess: (LocationData) -> Unit, onError: (String) -> Unit) {
        // En un caso real, usaríamos CoreLocation. Para este ejemplo, simulamos una ubicación.
        onSuccess(LocationData(19.4326, -99.1332)) // CDMX
    }
}

@Composable
actual fun rememberLocationHelper(): LocationHelper {
    return remember { IosLocationHelper() }
}

@Composable
actual fun LocationPermissionRequester(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    // En iOS la solicitud de permisos se maneja usualmente al momento de usar el servicio
    // o mediante Info.plist. Por simplicidad en este ejemplo, concedemos el permiso.
    LaunchedEffect(Unit) {
        onPermissionGranted()
    }
}
