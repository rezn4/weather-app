package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

class WeeklyWeatherResponse(
    var cod: String,
    var cnt: Int,
    var city: City,
    var list: List<WeeklyWeatherData>){

    class WeeklyWeatherData(
        @SerializedName("dt")
        var date: Long,
        @SerializedName("temp")
        var temperature: Temperature,
        var pressure: Double,
        var humidity: Int,
        var weather: List<Weather>,
        var speed: Double,
        var deg: Int,
        var clouds: Int

        ){

        inner class Temperature(
            var min: Double,
            var max: Double
        )
    }
}








