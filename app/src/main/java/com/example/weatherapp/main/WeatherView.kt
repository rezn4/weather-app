package com.example.weatherapp.main

import com.example.weatherapp.model.HourlyWeatherResponse
import com.example.weatherapp.model.WeeklyWeatherResponse

interface WeatherView {
    fun showWeeklyWeather(list: List<WeeklyWeatherResponse.WeeklyWeatherData>)
    fun showMainWeatherInfo(city: String, weatherInfo: WeeklyWeatherResponse.WeeklyWeatherData)
    fun showHourlyWeather(list: List<HourlyWeatherResponse.HourlyWeatherData>)
}