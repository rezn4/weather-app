package com.example.weatherapp.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.maps.MapsActivity
import com.example.weatherapp.model.HourlyWeatherResponse
import com.example.weatherapp.model.WeeklyWeatherResponse
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class WeatherActivity : AppCompatActivity(), WeatherView {

    companion object {
        const val WEEKLY_ADAPTER_POSITION = "WEEKLYADAPTERPOSITION"
        const val CLOUDS = "Clouds"
        const val RAIN = "Rain"
        const val CLEAR = "Clear"
        const val MAPS_ACTIVITY_REQUEST_CODE = 1
        const val PLACES_ACTIVITY_REQUEST_CODE = 2
        const val LATITUDE_KEY = "LATITUDE"
        const val LONGITUDE_KEY = "LONGITUDE"
        const val START_OF_THE_DAY = 0

        val NORTH_RANGE = 0 until 30
        val NORTH_EAST_RANGE = 30 until 70
        val EAST_RANGE = 70 until 120
        val SOUTH_EAST_RANGE = 120 until 170
        val SOUTH_RANGE = 170 until 210
        val WEST_SOUTH_RANGE = 210 until 250
        val WEST_RANGE = 250 until 280
        val NORTH_WEST_RANGE = 280 until 350
        val NORTH_END_RANGE = 350 until 360
    }

    private lateinit var presenter: WeatherPresenter
    private lateinit var quickOptions: QuickPermissionsOptions

    private val weeklyAdapter: WeeklyAdapter = WeeklyAdapter()
    private val hourlyAdapter: HourlyAdapter = HourlyAdapter()
    private val date: Long = System.currentTimeMillis() / 1000

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeWeeklyAdapter()
        initializeHourlyAdapter()

        quickOptions = QuickPermissionsOptions(
            rationaleMethod = { rationaleCallback(it) },
            permanentDeniedMethod = { permissionsPermanentlyDenied(it) }
        )

        presenter = WeatherPresenterImpl(
            this, PrefsLocationRepository(
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            )
        )

        presenter.loadWeather(date, weeklyAdapter.currentPosition)

        weeklyAdapter.setClickListener(object : WeeklyAdapter.ClickListener {
            override fun onItemClick(position: Int, date: Long) {
                presenter.loadCurrentWeatherData(position)
                presenter.loadHourlyWeatherData(date)
                weeklyAdapter.currentPosition = position
            }
        })

        mapsMarker.setOnClickListener {
            startPlacesIntent()
        }

        myLocation.setOnClickListener {
            runWithPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, options = quickOptions
            ) {
                val intent = Intent(this, MapsActivity::class.java)
                startActivityForResult(intent, MAPS_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun showWeeklyWeather(list: List<WeeklyWeatherResponse.WeeklyWeatherData>) {
        weeklyAdapter.setData(list)
    }

    override fun showHourlyWeather(list: List<HourlyWeatherResponse.HourlyWeatherData>) {
        hourlyAdapter.setData(list)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(WEEKLY_ADAPTER_POSITION, weeklyAdapter.currentPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val position: Int = savedInstanceState?.getInt(WEEKLY_ADAPTER_POSITION)!!
        weeklyAdapter.currentPosition = position
        setWeatherDataByPosition(weeklyAdapter.currentPosition)
    }

    override fun showMainWeatherInfo(city: String, weatherInfo: WeeklyWeatherResponse.WeeklyWeatherData) {
        cityName.text = if (city.isNotEmpty()) city else getString(R.string.unavailable)
        currentDate.text = weatherInfo.date.toString()
        temperatureValue.text = convertTemperature(weatherInfo)
        humidityValue.text = weatherInfo.humidity.toString().plus(getString(R.string.percent_symbol))
        windValue.text = weatherInfo.speed.toInt().toString().plus(getString(R.string.speed_symbol))
        currentDate.text = convertDate(weatherInfo)
        Glide.with(this).load(getIconId(weatherInfo)).into(weatherIcon)
        Glide.with(this).load(setWindDirectionIcon(weatherInfo)).into(windDirectionIcon)
    }

    private fun permissionsPermanentlyDenied(req: QuickPermissionsRequest) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.access_denied_permission))
        builder.setMessage(getString(R.string.permission_permadenied_text))
        builder.setPositiveButton(getString(R.string.permission_settings_text)) { dialog, which -> req.openAppSettings() }
        builder.setNegativeButton(getString(R.string.choose_city_dialog)) { dialog, which -> startPlacesIntent() }
        builder.setCancelable(true)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        changeDialogBtnColor(alertDialog)
    }

    private fun changeDialogBtnColor(alertDialog: AlertDialog) {
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary))
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorPrimary))
    }

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.access_denied_permission))
        builder.setMessage(getString(R.string.permission_rationale_text))
        builder.setPositiveButton(getString(R.string.retry_text)) { dialog, which -> req.proceed() }
        builder.setNegativeButton(getString(R.string.choose_city_dialog)) { dialog, which -> startPlacesIntent() }
        builder.setCancelable(true)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        changeDialogBtnColor(alertDialog)
    }

    private fun startPlacesIntent() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key))
        }

        val fields: List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.CITIES)
            .build(this)

        startActivityForResult(intent, PLACES_ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MAPS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val latitude = data?.getDoubleExtra(LATITUDE_KEY, 0.0)
                val longitude = data?.getDoubleExtra(LONGITUDE_KEY, 0.0)
                refreshWeatherData(latitude!!, longitude!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setWeatherDataByPosition(weeklyAdapter.currentPosition)
            }
        }

        if (requestCode == PLACES_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                val place: Place? = Autocomplete.getPlaceFromIntent(data!!)

                place?.let {
                    val latitude = it.latLng?.latitude
                    val longitude = it.latLng?.longitude
                    presenter.onSelectLocation(latitude!!, longitude!!)
                    setWeatherDataByPosition(weeklyAdapter.currentPosition)
                }
            }
        }
    }

    private fun setWeatherDataByPosition(position: Int){
        when (position){
            0 -> presenter.loadWeather(date, position)
            else -> presenter.loadWeather(START_OF_THE_DAY.toLong(), position)
        }
    }

    private fun refreshWeatherData(latitude: Double, longitude: Double) {
        presenter.onSelectLocation(latitude, longitude)
        presenter.loadWeather(0, weeklyAdapter.currentPosition)
    }

    private fun initializeWeeklyAdapter() {
        val layoutManager = LinearLayoutManager(this)
        weeklyRecyclerView.layoutManager = layoutManager
        weeklyRecyclerView.adapter = weeklyAdapter
    }

    private fun initializeHourlyAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        hourlyRecyclerView.layoutManager = layoutManager
        hourlyRecyclerView.adapter = hourlyAdapter
    }

    fun convertTemperature(weeklyWeatherData: WeeklyWeatherResponse.WeeklyWeatherData): String {
        val maxTemp = weeklyWeatherData.temperature.max.toInt()
        val minTemp = weeklyWeatherData.temperature.min.toInt()

        return "$maxTemp°/$minTemp°"
    }

    private fun convertDate(weeklyWeatherData: WeeklyWeatherResponse.WeeklyWeatherData): String {
        val dateInfo = Date(weeklyWeatherData.date.times(1000))
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = dateInfo

        val name = calendar.getDisplayName(
            Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale("RU")
        ).toString().toUpperCase()
        val date = calendar.get(Calendar.DATE)
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("RU")).toString()

        return "$name, $date $month"
    }

    private fun getIconId(weeklyWeatherData: WeeklyWeatherResponse.WeeklyWeatherData): Int {
        val main = weeklyWeatherData.weather[0].main
        val iconId: Int

        iconId = when (main) {
            CLOUDS -> R.drawable.ic_white_day_cloudy
            RAIN -> R.drawable.ic_white_day_rain
            CLEAR -> R.drawable.ic_white_day_bright

            else -> R.drawable.ic_white_unavailable
        }

        return iconId
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    /**
     * values based on the following document - https://uni.edu/storm/Wind%20Direction%20slide.pdf
     */

    private fun setWindDirectionIcon(weeklyWeatherData: WeeklyWeatherResponse.WeeklyWeatherData): Int {
        val degree = weeklyWeatherData.deg
        val iconId: Int

        iconId = when (degree) {
            in NORTH_RANGE -> R.drawable.ic_icon_wind_n
            in NORTH_EAST_RANGE -> R.drawable.ic_icon_wind_ne
            in EAST_RANGE -> R.drawable.ic_icon_wind_e
            in SOUTH_EAST_RANGE -> R.drawable.ic_icon_wind_se
            in SOUTH_RANGE -> R.drawable.ic_icon_wind_s
            in WEST_SOUTH_RANGE -> R.drawable.ic_icon_wind_ws
            in WEST_RANGE -> R.drawable.ic_icon_wind_w
            in NORTH_WEST_RANGE -> R.drawable.ic_icon_wind_wn
            in NORTH_END_RANGE -> R.drawable.ic_icon_wind_n
            else -> R.drawable.ic_white_unavailable
        }

        return iconId
    }
}
