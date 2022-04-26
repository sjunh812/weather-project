package org.sjhstudio.weathertestapp.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.sjhstudio.weathertestapp.R
import org.sjhstudio.weathertestapp.adapter.WeatherAdapter
import org.sjhstudio.weathertestapp.databinding.ActivityMainBinding
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import org.sjhstudio.weathertestapp.util.Constants.WEATHER_BASE_TIME
import org.sjhstudio.weathertestapp.util.Constants.WEATHER_NUM_OF_ROWS
import org.sjhstudio.weathertestapp.util.Constants.WEATHER_PAGE_NO
import org.sjhstudio.weathertestapp.util.Utils
import org.sjhstudio.weathertestapp.util.WeatherHelper
import org.sjhstudio.weathertestapp.viewmodel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainVm: MainViewModel

    private lateinit var locationManager: LocationManager
    private var locationListener = MyLocationListener()

    private var weatherAdapter = WeatherAdapter()

    private var isReady = false

    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(locationListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainVm = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        requestLocationPermission()
        observeAddress()
        observeWeather()

        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (isReady) {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        })
    }

    private fun observeAddress() {
        mainVm.address.observe(this) { addr ->
            val text = "${addr.subLocality ?: ""} ${addr.thoroughfare ?: ""}"
            binding.addressTv.text = text
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeWeather() {
        mainVm.weather.observe(this) { weather ->
            val items = weather.response.body.items.item
            isReady = true  // 화면출력

            for(item in items) {
                Log.e(DEBUG, "$item")
            }

            val data = WeatherHelper.getMainWeatherData(items)

            Utils.setStatusBarColor(this, WeatherHelper.getWeatherResource(data.weather!!))
            binding.container.setBackgroundColor(ContextCompat.getColor(this, WeatherHelper.getWeatherResource(data.weather!!)))
            binding.weatherImg.setImageResource(WeatherHelper.getWeatherResource(data.weather!!, true))
            binding.dateTv.text = Utils.dateFormat.format(Date())
            binding.weatherTv.text = data.weather
            binding.tempTv.text = "${data.temp}"
            binding.tmxTmnTv.text = "최고 ${data.tmx}° / 최저 ${data.tmn}°"

            binding.weatherRv.apply {
                weatherAdapter.items = data.weathers!!
                adapter = weatherAdapter
                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }

    // 위치권한 요청
    private fun requestLocationPermission() {
        locationPermissionResult.launch(
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLocation() {
        if(Utils.checkLocPermission(this, binding.root))  {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                lastLocation?.let { loc ->
                    println("xxx hi")
                    setMainViewModelData(loc)
                }

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
            } else {
                Snackbar.make(binding.root, getString(R.string.turn_on_gps_plz), 1000).show()
            }
        }
    }

    private fun setMainViewModelData(location: Location) {
        val lat = location.latitude
        val long = location.longitude
        val point = WeatherHelper.coordinateTransformation(lat, long)
        val baseDate = Utils.baseDateFormat.format(Utils.getYesterday())

        mainVm.getAddress(lat, long)
        mainVm.getWeather(
            WEATHER_NUM_OF_ROWS,
            WEATHER_PAGE_NO,
            baseDate,
            WEATHER_BASE_TIME,
            point.x,
            point.y
        )

        Log.e(DEBUG, "getAddress() : 위도=$lat, 경도=$long")
        Log.e(DEBUG, "getWeather() : baseDate($baseDate), baseTime($WEATHER_BASE_TIME) nx=${point.x}, ny=${point.y}")
    }

    private val locationPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                Log.e(DEBUG, "정확한 위치권한 허용")
                getLocation()
            }

            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                Log.e(DEBUG, "대략적 위치권한 허용")
                getLocation()
            }

            else -> {
                Log.e(DEBUG, "위치권한 거부")
            }
        }
    }

    inner class MyLocationListener: LocationListener  {

        override fun onLocationChanged(location: Location)  {
            Log.e(DEBUG, "onLocationChanged()")
            setMainViewModelData(location)
            locationManager.removeUpdates(locationListener)
        }

    }

}