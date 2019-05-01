package com.example.weatherapp.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.main.WeatherActivity.Companion.CLEAR
import com.example.weatherapp.main.WeatherActivity.Companion.CLOUDS
import com.example.weatherapp.main.WeatherActivity.Companion.RAIN
import com.example.weatherapp.model.HourlyWeatherResponse
import java.util.*

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>(){
    private var list: List<HourlyWeatherResponse.HourlyWeatherData>? = null

    fun setData(list: List<HourlyWeatherResponse.HourlyWeatherData>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): HourlyViewHolder {
        return HourlyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_hourly_weather,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val weatherInfo = list?.get(position)

        weatherInfo?.let {
            holder.temperature.text = convertTemperature(weatherInfo)
            holder.hours.text = convertDate(weatherInfo)
            Glide.with(holder.icon.context).load(getIconId(weatherInfo)).into(holder.icon)
        }
    }

    private fun getIconId(hourlyWeatherData: HourlyWeatherResponse.HourlyWeatherData): Int {
        val main = hourlyWeatherData.weather[0].main
        val iconId: Int

        iconId = when (main) {
            CLOUDS -> R.drawable.ic_white_day_cloudy
            RAIN -> R.drawable.ic_white_day_rain
            CLEAR -> R.drawable.ic_white_day_bright

            else -> R.drawable.ic_white_unavailable
        }

        return iconId
    }

    private fun convertTemperature(hourlyWeatherData: HourlyWeatherResponse.HourlyWeatherData): String {
        val maxTemp = hourlyWeatherData.main.temp_max.toInt()

        return "$maxTemp°"
    }

    private fun convertDate(hourlyWeatherData: HourlyWeatherResponse.HourlyWeatherData): String {
        val date = Date(hourlyWeatherData.date.times(1000))

        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        var day = calendar.get(Calendar.HOUR_OF_DAY).toString()

        if (day.length == 1){
            day = "0$day"
        }

        return day
    }


    class HourlyViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.hourlyWeatherIcon)
        val hours: TextView = v.findViewById(R.id.hoursTextView)
        val temperature: TextView = v.findViewById(R.id.hourlyTempValue)
    }
}