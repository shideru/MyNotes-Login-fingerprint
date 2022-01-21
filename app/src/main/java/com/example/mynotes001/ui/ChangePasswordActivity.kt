package com.example.mynotes001.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.mynotes001.R
import com.example.mynotes001.db.NoteDatabase
import com.example.mynotes001.db.User
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.passwordinput1
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.math.BigInteger
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class ChangePasswordActivity  : AppCompatActivity() {
    @Throws(
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class
    )
    private fun generateStorngPasswordHash(password: String): String? {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt: ByteArray = getSalt()
        val spec =
            PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        return iterations.toString() + ":" + toHex(salt) + ":" + toHex(hash)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getSalt(): ByteArray {
        val sr: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(16)
        sr.nextBytes(salt)
        return salt
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun toHex(array: ByteArray): String? {
        val bi = BigInteger(1, array)
        val hex: String = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0) {
            String.format("%0" + paddingLength + "d", 0) + hex
        } else {
            hex
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val user = intent.getSerializableExtra("User") as String
    btChange.setOnClickListener {
        val password1 = passwordinput0.text.toString().trim()
        val password2 = passwordinput1.text.toString().trim()

        if (password1.isEmpty()) {
            passwordinput0.error = "passwords can't be empty"
            passwordinput0.requestFocus()
            return@setOnClickListener
        }
        if (password1 != password2) {
        passwordinput0.error = "passwords do not mach"
        passwordinput0.requestFocus()
        return@setOnClickListener
    }

        val hash = generateStorngPasswordHash(password1).toString()
        NoteDatabase(this).getUserDao().passwordUpdate(user,hash)
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
    }
    }
   
}