package com.example.mynotes001.ui

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mynotes001.R
import com.example.mynotes001.db.NoteDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.nameinput
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.experimental.xor


class LoginActivity : AppCompatActivity() {
    private var cancellationSignal: CancellationSignal?= null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication error: $errString")
                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Authentication successful")
                    startActivity(Intent(this@LoginActivity, WelcomeActivity::class.java))
                }
            }
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun validatePassword(
        originalPassword: String,
        storedPassword: String
    ): Boolean {
        val parts = storedPassword.split(":").toTypedArray()
        val iterations = parts[0].toInt()
        val salt: ByteArray = fromHex(parts[1])
        val hash: ByteArray = fromHex(parts[2])
        val spec =
            PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.size * 8)
        val skf: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val testHash: ByteArray = skf.generateSecret(spec).getEncoded()
        var diff = hash.size xor testHash.size
        var i = 0
        while (i < hash.size && i < testHash.size) {
            diff = (hash[i] xor testHash[i]).toInt()
            i++
        }
        return diff == 0
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun fromHex(hex: String): ByteArray {
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            bytes[i] = hex.substring(2 * i, 2 * i + 2).toInt(16).toByte()
        }
        return bytes
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btSignIn.setOnClickListener {
            val name = nameinput.text.toString().trim()
            val password = passwordinput.text.toString().trim()
            val hash = NoteDatabase(this).getUserDao().password(name).toString()

            if (name.isEmpty()) {
                nameinput.error = "name required"
                nameinput.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordinput1.error = "password required"
                passwordinput1.requestFocus()
                startActivity(Intent(this, SignUpActivity::class.java))
                return@setOnClickListener
            }
            if (validatePassword(password,hash)) {
                Toast.makeText(this, "login successful", Toast.LENGTH_SHORT).show();
                startActivity(Intent(this, WelcomeActivity::class.java).apply {
                    putExtra("User", name)
                }
                )
            }
            else
            {
                passwordinput.error = "Wrong password"
                passwordinput.requestFocus()
                return@setOnClickListener
            }
        }
        btSignUp.setOnClickListener {
            if(NoteDatabase(this).getUserDao().getAll() == null)
            {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
            else
            {
                Toast.makeText(this, "Sorry we do not support multiple users!", Toast.LENGTH_SHORT).show();
            }
        }

        checkBiometricSupport()
        btSignInFingerprint.setOnClickListener {
            val biometricPrompt = BiometricPrompt.Builder( this)
                    .setTitle("Authentication")
                    .setSubtitle("We need you to scan your fingerprint.")
                    .setDescription("This application requires authentication with a fingerprint scan, it will tak only a second.")
                    .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener{ dialog, which ->
                        notifyUser("Authentication cancelled")
                    }).build()
            biometricPrompt.authenticate(getCancellationSignal(),mainExecutor, authenticationCallback)

        }
    }
    override fun onBackPressed() {
        finishAffinity()
        System.exit(0)
    }
    private fun getCancellationSignal():CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication has been cancelled")
        }
        return cancellationSignal as CancellationSignal
    }
    private fun checkBiometricSupport() : Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure){
            notifyUser("Fingerprint authentication has not been enabled on this device")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED){
            notifyUser("Permission not enabled")
            return false
        }
        return if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true


    }

    private fun notifyUser(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}