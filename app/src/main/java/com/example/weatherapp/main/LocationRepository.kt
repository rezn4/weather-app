package com.example.weatherapp.main

import android.content.SharedPreferences

interface LocationRepository {
    fun lastLocation():Location?
    fun saveLastLocation(latitude: Double, longitude: Double)
}

class PrefsLocationRepository(private val prefs: SharedPreferences): LocationRepository {
    override fun lastLocation(): Location? {
        val latitude = prefs.getDouble(WeatherActivity.LATITUDE_KEY, 0.0)
        val longitude = prefs.getDouble(WeatherActivity.LONGITUDE_KEY, 0.0)
        return Location(latitude, longitude)
    }

    override fun saveLastLocation(latitude: Double, longitude: Double) {
        val editor = prefs.edit()
        editor.putDouble(WeatherActivity.LATITUDE_KEY, latitude)
        editor.putDouble(WeatherActivity.LONGITUDE_KEY, longitude)
        editor.apply()
    }

    fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
        putLong(key, java.lang.Double.doubleToRawLongBits(double))

    fun SharedPreferences.getDouble(key: String, default: Double) =
        java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))

}

data class Location(val latitude: Double, val longitude: Double)