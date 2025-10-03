package student.projects.biometricssologin

import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import student.projects.biometricssologin.PromptInfo

private val Unit.PromptInfo: Any
private val Unit.PromptInfo: Any

class LoginActivity : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val ssoLoginButton: Button = findViewById(R.id.btn_sso_login)
        val biometricLoginButton: Button = findViewById(R.id.btn_biometric_login)
        biometricStatusTextView = findViewById(R.id.tv_biometric_status)

        // 1. Initialize Biometrics
        setupBiometricAuthentication()

        // 2. Set up Button Click Handlers

        // Placeholder for initiating SSO flow
        ssoLoginButton.setOnClickListener {
            // In a real app, this would start the web-based SSO flow (e.g., using AppAuth)
            Toast.makeText(this, "Starting SSO Login...", Toast.LENGTH_SHORT).show()
            // After successful SSO, you would typically save tokens and navigate to the main screen.
            // You might also 'enable' biometrics for the first time here.
        }

        // Placeholder for initiating Biometric flow
        biometricLoginButton.setOnClickListener {
            // Only attempt biometric login if it's available and set up
            if (isBiometricReady()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Biometric login is not available or set up.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- BIOMETRIC IMPLEMENTATION ---

    private fun setupBiometricAuthentication() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricStatusTextView.text = "Biometric Status: Ready"
                biometricStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                biometricStatusTextView.text = "Biometric Status: No Hardware"
                biometricStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                biometricStatusTextView.text = "Biometric Status: Unavailable"
                biometricStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                biometricStatusTextView.text = "Biometric Status: Not Enrolled (Go to settings)"
                biometricStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
            }
            else -> {
                biometricStatusTextView.text = "Biometric Status: Unknown Error"
                biometricStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
        }

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Authentication Succeeded! Logging in...", Toast.LENGTH_LONG).show()
                    // In a real app, successful authentication would decrypt a stored session token
                    // and navigate to the main screen.
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Quick Login")
            .setSubtitle("Authenticate to access your app.")
            .setNegativeButtonText("Use SSO Login") // Fallback to SSO button
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private fun isBiometricReady(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }
}