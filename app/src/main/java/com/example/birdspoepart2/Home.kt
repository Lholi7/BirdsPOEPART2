package com.example.birdspoepart2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Home : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val locationPermissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Check and request location permission
        if (isLocationPermissionGranted()) {
            initMap()
        } else {
            requestLocationPermission()
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, initialize the map
                    initMap()
                } else {
                    // Permission denied, handle it (e.g., show a message)
                    Toast.makeText(this, "Location permission denied. Cannot show your location on the map.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
