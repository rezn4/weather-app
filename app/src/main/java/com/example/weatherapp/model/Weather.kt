package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Weather(
    var id: Int,
    @SerializedName("main")
    var main: String,
    var description: String
)