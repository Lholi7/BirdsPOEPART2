package com.example.birdspoepart2

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var metricRadioButton: RadioButton
    private lateinit var imperialRadioButton: RadioButton
    private lateinit var maxDistanceEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var notificationTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)

        // Initialize UI elements
        metricRadioButton = findViewById(R.id.metricRadioButton)
        imperialRadioButton = findViewById(R.id.imperialRadioButton)
        maxDistanceEditText = findViewById(R.id.maxDistanceEditText)
        saveButton = findViewById(R.id.saveButton)
        notificationTextView = findViewById(R.id.notificationTextView)

        // Load user settings
        sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE)
        loadUserSettings()

        // Save button click listener
        saveButton.setOnClickListener {
            saveUserSettings()
        }
    }

    private fun loadUserSettings() {
        val isMetric = sharedPreferences.getBoolean("isMetric", true)
        val maxDistance = sharedPreferences.getString("maxDistance", "10")

        metricRadioButton.isChecked = isMetric
        imperialRadioButton.isChecked = !isMetric
        maxDistanceEditText.setText(maxDistance)
    }

    private fun saveUserSettings() {
        val isMetric = metricRadioButton.isChecked
        val maxDistance = maxDistanceEditText.text.toString()

        // Save to shared preferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isMetric", isMetric)
        editor.putString("maxDistance", maxDistance)
        editor.apply()

        // Save to Firebase
        val userSettings = HashMap<String, Any>()
        userSettings["isMetric"] = isMetric
        userSettings["maxDistance"] = maxDistance

        databaseReference.child("Settings").updateChildren(userSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showNotification("Settings saved successfully")
                } else {
                    showNotification("Failed to save settings")
                }
            }
    }

    private fun showNotification(message: String) {
        notificationTextView.text = message
    }
}

