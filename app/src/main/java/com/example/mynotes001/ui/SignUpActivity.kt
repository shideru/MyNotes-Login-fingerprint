package com.example.mynotes001.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mynotes001.R
import com.example.mynotes001.db.NoteDatabase
import com.example.mynotes001.db.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.math.BigInteger
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class SignUpActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_sign_up)
        btRegister.setOnClickListener {
            val name = nameinput.text.toString().trim()
            val password1 = passwordinput1.text.toString().trim()
            val password2 = passwordinput2.text.toString().trim()
            if (name.isEmpty()) {
                nameinput.error = "name required"
                nameinput.requestFocus()
                return@setOnClickListener
            }
            if (NoteDatabase(this).getUserDao().isUser(name) != null) {
            nameinput.error = "name is already taken"
            nameinput.requestFocus()
            return@setOnClickListener
            }

            if (password1.isEmpty()) {
                passwordinput1.error = "password required"
                passwordinput1.requestFocus()
                return@setOnClickListener
            }
            if (password1 != password2) {
                passwordinput2.error = "passwords do not mach"
                passwordinput2.requestFocus()
                return@setOnClickListener
            }
            val hash = generateStorngPasswordHash(password1).toString()
            val newuser = User(name, hash)
            NoteDatabase(this).getUserDao().addUser(newuser)
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();

        }
        btCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

}