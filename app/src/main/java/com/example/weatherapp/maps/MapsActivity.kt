package com.example.weatherapp.maps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.main.WeatherActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*


class MapsActivity: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true

        map.setOnMapClickListener {latlng ->
            val markerOptions = MarkerOptions()
            markerOptions.position(latlng)
            googleMap.clear()
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
            googleMap.addMarker(markerOptions)
            showWeatherFab.show()

            showWeatherFab.setOnClickListener {
                latlng?.let {
                    val intent = Intent()
                    intent.putExtra(WeatherActivity.LATITUDE_KEY, latlng.latitude)
                    intent.putExtra(WeatherActivity.LONGITUDE_KEY, latlng.longitude)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
    }
}