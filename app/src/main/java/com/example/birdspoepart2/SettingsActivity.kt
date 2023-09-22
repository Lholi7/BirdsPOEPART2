package com.example.birdspoepart2

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.HashMap

class SettingsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var metricRadioButton: RadioButton
    private lateinit var imperialRadioButton: RadioButton
    private lateinit var maxDistanceEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var notificationTextView: TextView
    private lateinit var mapViewContainer: FragmentContainerView
    private lateinit var googleMap: GoogleMap
    private val locationPermissionCode = 1
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
        mapViewContainer = findViewById(R.id.mapViewContainer)

        // Load user settings
        sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE)
        loadUserSettings()

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapViewContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Check for location permission again (just in case)
        if (isLocationPermissionGranted()) {
            // Enable the My Location layer on the map
            googleMap.isMyLocationEnabled = true

            // Add a marker at the user's location (optional)
            // val location = LatLng(latitude, longitude)
            // googleMap.addMarker(MarkerOptions().position(location).title("My Location"))

            // Move the camera to the user's location
            // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        } else {
            // Handle the case where permission is denied
            Toast.makeText(this, "Location permission is required to show your location on the map.", Toast.LENGTH_LONG).show()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    private fun showNotification(message: String) {
        notificationTextView.text = message
    }
}
