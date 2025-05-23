package com.example.lexia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration)

        auth = FirebaseAuth.getInstance()

        val usernameField = findViewById<EditText>(R.id.username)
        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val confirmPasswordField = findViewById<EditText>(R.id.confirmPassword)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginPrompt = findViewById<TextView>(R.id.loginPrompt)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        registerButton.setOnClickListener {
            val username = usernameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorMessage.text = "Please fill in all fields."
                errorMessage.visibility = TextView.VISIBLE
            } else if (password != confirmPassword) {
                errorMessage.text = "Passwords don't match."
                errorMessage.visibility = TextView.VISIBLE
            } else {
                registerUser(username, email, password)
            }
        }

        loginPrompt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val database = FirebaseDatabase.getInstance().getReference("users")

                    val userMap = HashMap<String, String>()
                    userMap["username"] = username
                    userMap["email"] = email

                    if (uid != null) {
                        database.child(uid).setValue(userMap).addOnCompleteListener {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to save user: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errorMessage = findViewById<TextView>(R.id.errorMessage)
                    errorMessage.text = "Registration failed: ${task.exception?.message}"
                    errorMessage.visibility = TextView.VISIBLE
                }
            }
    }
}
