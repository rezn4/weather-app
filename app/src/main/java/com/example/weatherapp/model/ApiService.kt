package com.example.weatherapp.model
import android.widget.HorizontalScrollView
import com.example.weatherapp.model.ApiService.RetrofitInstance.API_KEY
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // weekly requests
    @GET("forecast/daily?lat?&lon?&units=metric&APPID=$API_KEY&cnt=$FORECAST_DAYS")
    fun getWeeklyWeatherByCoord(@Query("lat") lat: Double,
                                @Query("lon") lon: Double): Observable<WeeklyWeatherResponse>

    // hourly requests

    @GET("forecast/hourly?lat?&lon?&units=metric&appid=$API_KEY")
    fun getHourlyWeatherByCoord(@Query("lat") lat: Double,
                                @Query("lon") lon: Double): Observable<HourlyWeatherResponse>

    companion object RetrofitInstance {
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        private const val API_KEY = "7cdaad99ad019b77e06832ff3e29f829"
        private const val FORECAST_DAYS = "5"


        fun initialize(): ApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}