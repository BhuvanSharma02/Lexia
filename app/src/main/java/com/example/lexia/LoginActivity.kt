package com.example.lexia

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if the user is already signed in
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in, redirect to HomeActivity
                startActivity(Intent(this, Home::class.java))
                finish()  // Close the LoginActivity
            }
        }

        val gifImageView = findViewById<ImageView>(R.id.gifImageView)
        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)
        val registerPrompt = findViewById<TextView>(R.id.registerPrompt)

        Glide.with(this)
            .asGif()
            .load(R.drawable.hello)
            .into(gifImageView)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                errorMessage.text = "Please fill in all fields."
                errorMessage.visibility = TextView.VISIBLE
            }
        }

        registerPrompt.setOnClickListener {
            // Navigate to Registration Activity
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, redirect to HomeActivity
                    startActivity(Intent(this, Home::class.java))
                    finish()
                } else {
                    // Login failed
                    val errorMessage = findViewById<TextView>(R.id.errorMessage)
                    errorMessage.text = "Login failed: ${task.exception?.message}"
                    errorMessage.visibility = TextView.VISIBLE
                }
            }
    }
}