package com.example.weatherapp.main

interface WeatherPresenter {
    fun loadWeather(timestamp: Long, position: Int)
    fun loadWeeklyWeatherData()
    fun loadHourlyWeatherData(timestamp: Long)
    fun loadCurrentWeatherData(position: Int)
    fun onStop()
    fun onSelectLocation(lat: Double, lon: Double)
}