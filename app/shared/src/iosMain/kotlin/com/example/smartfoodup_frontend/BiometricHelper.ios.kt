package com.smartfoodup.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.LocalAuthentication.*
import platform.Foundation.NSUserDefaults
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlinx.cinterop.ExperimentalForeignApi

actual class BiometricHelper {

    private val userDefaults = NSUserDefaults.standardUserDefaults

    @OptIn(ExperimentalForeignApi::class)
    actual fun isBiometricSupported(): Boolean {
        val context = LAContext()
        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
    }

    actual fun saveCredentials(email: String, contrasena: String) {
        userDefaults.setObject(email, "saved_email")
        userDefaults.setObject(contrasena, "saved_pass")
        userDefaults.synchronize()
    }

    actual fun getSavedEmail(): String? = userDefaults.stringForKey("saved_email")
    actual fun getSavedPassword(): String? = userDefaults.stringForKey("saved_pass")

    actual fun clearCredentials() {
        userDefaults.removeObjectForKey("saved_email")
        userDefaults.removeObjectForKey("saved_pass")
        userDefaults.synchronize()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun authenticate(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val context = LAContext()
        val reason = "$title: $subtitle"

        context.evaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            reason
        ) { success, error ->
            dispatch_async(dispatch_get_main_queue()) {
                if (success) {
                    onSuccess()
                } else {
                    onError(error?.localizedDescription ?: "Error de autenticación")
                }
            }
        }
    }
}

@Composable
actual fun rememberBiometricHelper(): BiometricHelper {
    return remember { BiometricHelper() }
}
