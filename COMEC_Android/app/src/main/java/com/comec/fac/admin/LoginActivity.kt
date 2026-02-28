package com.comec.fac.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.comec.fac.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvError = findViewById<TextView>(R.id.tv_error)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()

            // Connexion simple admin local
            if (username == "admin" && password == "comec2025") {
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            } else {
                // Essayer Firebase Auth
                auth.signInWithEmailAndPassword(username, password)
                    .addOnSuccessListener {
                        startActivity(Intent(this, AdminActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        tvError.text = getString(R.string.error_login)
                    }
            }
        }
    }
}
