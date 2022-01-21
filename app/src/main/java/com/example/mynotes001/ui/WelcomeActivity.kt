package com.example.mynotes001.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.mynotes001.R
import com.example.mynotes001.db.NoteDatabase
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val user = intent.getSerializableExtra("User") as String?
        btNotes.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("User", user)
            }
            )
        }
        btChange.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java).apply {
                putExtra("User", user)
            }
            )
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}