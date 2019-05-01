package com.example.weatherapp.main

import com.example.weatherapp.model.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class WeatherPresenterImpl(private val weatherView: WeatherView,
                           private val locationRepository: LocationRepository) : WeatherPresenter {

    private var disposables: CompositeDisposable = CompositeDisposable()
    var date: Long = System.currentTimeMillis() / 1000

    override fun loadWeather(timestamp: Long, position: Int) {
        loadHourlyWeatherData(timestamp)
        loadWeeklyWeatherData()
        loadCurrentWeatherData(position)
    }

    override fun loadHourlyWeatherData(timestamp: Long) {
        val apiService = ApiService.initialize()
        val lastLocation = locationRepository.lastLocation()

        if (lastLocation != null){
            disposables.add(apiService.getHourlyWeatherByCoord(lastLocation.latitude, lastLocation.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        weatherView.showHourlyWeather(it.list.filter { item ->
                            getDayOfWeek(item.date) == getDayOfWeek(timestamp)
                        })
                    },
                    {
                        it.printStackTrace()
                    }
                ))
        } else {

        }
    }

    override fun loadCurrentWeatherData(position: Int) {
        val apiService = ApiService.initialize()
        val lastLocation = locationRepository.lastLocation()

        if (lastLocation != null){
            disposables.add(apiService.getWeeklyWeatherByCoord(lastLocation.latitude, lastLocation.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        weatherView.showMainWeatherInfo(it.city.name, it.list[position])
                    },
                    {
                        it.printStackTrace()
                    }
                ))
        } else {

        }
    }

    override fun loadWeeklyWeatherData() {
        val apiService = ApiService.initialize()
        val lastLocation = locationRepository.lastLocation()

        if (lastLocation != null){
            disposables.add(apiService.getWeeklyWeatherByCoord(lastLocation.latitude, lastLocation.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        weatherView.showWeeklyWeather(it.list.filter { item ->
                            getDayOfWeek(item.date) >= getDayOfWeek(date)
                        })
                    },
                    {
                        it.printStackTrace()
                    }
                ))
        } else {

        }
    }

    override fun onSelectLocation(lat: Double, lon: Double) {
        locationRepository.saveLastLocation(lat, lon)
    }

    private fun getDayOfWeek(unixTimestamp: Long): Int {
        val date = Date(unixTimestamp.times(1000))

        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date

        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    override fun onStop() {
        disposables.clear()
    }
}