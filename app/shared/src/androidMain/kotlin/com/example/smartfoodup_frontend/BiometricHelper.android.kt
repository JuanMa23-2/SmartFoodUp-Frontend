package com.example.smartfoodup_frontend

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class BiometricHelper(private val context: Context) {

    private val sharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "secure_user_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    actual fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                           BiometricManager.Authenticators.BIOMETRIC_WEAK
        
        val canAuthenticate = biometricManager.canAuthenticate(authenticators)
        
        // Retornamos true si el hardware está presente, incluso si el usuario no ha registrado su huella aún.
        // Esto permite que el componente sea visible en el Dashboard para que el usuario intente activarlo.
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS || 
               canAuthenticate == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
    }

    actual fun saveCredentials(email: String, contrasena: String) {
        sharedPreferences.edit().apply {
            putString("saved_email", email)
            putString("saved_pass", contrasena)
            apply()
        }
    }

    actual fun getSavedEmail(): String? = sharedPreferences.getString("saved_email", null)
    actual fun getSavedPassword(): String? = sharedPreferences.getString("saved_pass", null)

    actual fun clearCredentials() {
        sharedPreferences.edit().apply {
            remove("saved_email")
            remove("saved_pass")
            apply()
        }
    }

    actual fun authenticate(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val activity = context as? FragmentActivity ?: run {
            onError("La pantalla de Login no está montada sobre una FragmentActivity nativa.")
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Huella no reconocida.")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Cancelar")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                                      BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@Composable
actual fun rememberBiometricHelper(): BiometricHelper {
    val context = LocalContext.current
    return remember(context) { BiometricHelper(context) }
}