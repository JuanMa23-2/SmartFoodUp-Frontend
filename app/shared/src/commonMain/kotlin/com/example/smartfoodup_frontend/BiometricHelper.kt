package com.example.smartfoodup_frontend

import androidx.compose.runtime.Composable

expect class BiometricHelper {
    fun isBiometricSupported(): Boolean
    fun saveCredentials(email: String, contrasena: String)
    fun getSavedEmail(): String?
    fun getSavedPassword(): String?
    fun clearCredentials()
    fun authenticate(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}

@Composable
expect fun rememberBiometricHelper(): BiometricHelper
