package com.smartfoodup.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLocationHelper(): LocationHelper

interface LocationHelper {
    fun getCurrentLocation(onSuccess: (LocationData) -> Unit, onError: (String) -> Unit)
}
