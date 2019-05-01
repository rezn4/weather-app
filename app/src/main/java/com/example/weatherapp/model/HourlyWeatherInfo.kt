package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

class HourlyWeatherResponse(
    var city: City,
    var cod: Int,
    var message: Double,
    var cnt: Int,
    var list: List<HourlyWeatherData>) {

    inner class City(
        var id: Int,
        var name: String,
        var country: String
    )

    inner class HourlyWeatherData(
        @SerializedName("dt")
        var date: Long,
        var weather: List<Weather>,
        var clouds: Clouds,
        @SerializedName("main")
        var main: Main,
        var wind: Wind,
        var dt_txt: String){


        inner class Clouds(
            var all: Int
        )

        inner class Main(
            var temp: Double,
            var temp_min: Double,
            var temp_max: Double,
            var presure: Double,
            var sea_level: Double,
            var grnd_level: Double,
            var humidity: Int,
            var temp_kf: Double
        )

        inner class Wind(
            var speed: Double,
            var deg: Double
        )

    }

}