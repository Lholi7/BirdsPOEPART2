package com.example.birdspoepart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginRegister : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)


        auth = FirebaseAuth.getInstance()
        var emailEditText = findViewById<EditText>(R.id.emailEditText)
        var passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            loginUser(email, password)
        }

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            signUpUser(email, password)
        }

    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    Toast.makeText(this,"loginUser:success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,SettingsActivity::class.java)
                    startActivity(intent)
                    // Perform further actions (e.g., navigate to another activity)
                } else {
                    // Login failed
                    Toast.makeText(this,"loginUser:failure", Toast.LENGTH_SHORT).show()
                    // Show error message to the user
                }
            }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Signup successful
                    Toast.makeText(this,"User Registered", Toast.LENGTH_SHORT).show()
                    // Perform further actions (e.g., navigate to another activity)
                } else {
                    // Signup failed
                    Toast.makeText(this,"User NOT Registered", Toast.LENGTH_SHORT).show()
                    // Show error message to the user
                }
            }

    }
}