package com.smartfoodup.app

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
        try {
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
        } catch (e: Exception) {
            // Si hay un error con el Keystore (común en algunas versiones de Android), 
            // borramos las preferencias corruptas y reintentamos o usamos unas normales.
            context.getSharedPreferences("secure_user_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            
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
    }

    actual fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                           BiometricManager.Authenticators.BIOMETRIC_WEAK or
                           BiometricManager.Authenticators.DEVICE_CREDENTIAL
        
        val canAuthenticate = biometricManager.canAuthenticate(authenticators)
        
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS || 
            canAuthenticate == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            return true
        }

        // --- FALLBACK PARA KERNELS INCONSISTENTES ---
        // Si la API moderna falla, intentamos con la API de legado.
        // Algunos kernels no reportan bien la biometría a la API moderna, pero sí a la antigua.
        try {
            val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as? android.hardware.fingerprint.FingerprintManager
            if (fingerprintManager?.isHardwareDetected == true) {
                return true
            }
        } catch (e: Exception) {
            // Ignorar errores de acceso a sistema
        }

        return false
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
                    onError("Identidad no reconocida.")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                                      BiometricManager.Authenticators.BIOMETRIC_WEAK or
                                      BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@Composable
actual fun rememberBiometricHelper(): BiometricHelper {
    val context = LocalContext.current
    return remember(context) { BiometricHelper(context) }
}