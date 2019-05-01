package com.example.weatherapp.main

import android.support.constraint.ConstraintLayout
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
import com.example.weatherapp.model.WeeklyWeatherResponse
import java.util.*

class WeeklyAdapter : RecyclerView.Adapter<WeeklyAdapter.WeeklyViewHolder>() {

    var currentPosition: Int = 0
    var list: List<WeeklyWeatherResponse.WeeklyWeatherData>? = null

    fun setData(list: List<WeeklyWeatherResponse.WeeklyWeatherData>?){
        this.list = list
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onItemClick(position: Int, date: Long)
    }

    private lateinit var clickListener: ClickListener

    fun setClickListener(clickListener: ClickListener){
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): WeeklyViewHolder {
        return WeeklyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_weekly_weather,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onBindViewHolder(holder: WeeklyViewHolder, position: Int) {
        val weatherInfo = list?.get(position)

        weatherInfo?.let {
            holder.temperature.text = convertTemperature(it)
            holder.day.text = convertDate(it).toUpperCase()
            Glide.with(holder.icon.context).load(getIconId(it)).into(holder.icon)

        }

        if (currentPosition == holder.adapterPosition){
            holder.layout.setBackgroundColor(getColor(holder, R.color.selected_item))
            changeSelectedItemColor(holder, R.color.colorPrimary)
        } else {
            holder.layout.setBackgroundColor(holder.layout.resources.getColor(R.color.white))
            changeSelectedItemColor(holder, R.color.black)
        }

        holder.layout.setOnClickListener {
            clickListener.onItemClick(position, weatherInfo!!.date)
            currentPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    fun changeSelectedItemColor(holder: WeeklyViewHolder, colorId: Int){
        holder.temperature.setTextColor(getColor(holder, colorId))
        holder.day.setTextColor(getColor(holder, colorId))
        holder.icon.setColorFilter(getColor(holder, colorId))
    }

    fun getColor(holder: WeeklyViewHolder, colorId: Int): Int {
        return holder.layout.resources.getColor(colorId)
    }

    class WeeklyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val temperature: TextView = v.findViewById(R.id.weeklyTemperature)
        val day: TextView = v.findViewById(R.id.weeklyDayTextview)
        val icon: ImageView = v.findViewById(R.id.weeklyWeatherIcon)
        val layout: ConstraintLayout = v.findViewById(R.id.weeklyLayout)
    }

    private fun getIconId(weeklyWeatherData: WeeklyWeatherResponse.WeeklyWeatherData): Int {
        val main = weeklyWeatherData.weather[0].main
        val iconId: Int

        iconId = when (main){
            CLOUDS -> R.drawable.ic_black_day_cloudy
            RAIN -> R.drawable.ic_black_day_rain
            CLEAR -> R.drawable.ic_black_day_bright

            else -> R.drawable.ic_black_unavailable
        }

        return iconId
    }

    fun convertTemperature(weeklyWeatherData: WeeklyWeatherResponse.WeeklyWeatherData): String {
        val maxTemp = weeklyWeatherData.temperature.max.toInt()
        val minTemp = weeklyWeatherData.temperature.min.toInt()

        return "$maxTemp°/$minTemp°"
    }

    private fun convertDate(weeklyWeatherData: WeeklyWeatherResponse.WeeklyWeatherData): String {
        val date = Date(weeklyWeatherData.date.times(1000))

        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date

        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale("RU")).toString()
    }
}