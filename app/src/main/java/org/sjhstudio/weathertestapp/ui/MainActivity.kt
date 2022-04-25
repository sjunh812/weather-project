package org.sjhstudio.weathertestapp.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.sjhstudio.weathertestapp.R
import org.sjhstudio.weathertestapp.databinding.ActivityMainBinding
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import org.sjhstudio.weathertestapp.util.Utils
import org.sjhstudio.weathertestapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainVm: MainViewModel
    private lateinit var locationManager: LocationManager
    private var locationListener = MyLocationListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainVm = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        requestLocationPermission()
        observeAddress()
        observeWeather()
        println("xxx ${Utils.calculateBaseTime2()}")
    }

    private fun observeAddress() {
        mainVm.address.observe(this) { addr ->
            val text = "${addr.subLocality ?: ""} ${addr.thoroughfare ?: ""}"
            binding.addressTv.text = text
        }
    }

    private fun observeWeather() {
        mainVm.weather.observe(this) { weather ->
            for(item in weather.response.body.items.item) {
                Log.e(DEBUG, "$item")
            }

            val weathers = Utils.getWeathers(weather.response.body.items.item)
            weathers.forEach { lw ->
                Log.e(DEBUG, "$lw")
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

                Log.e(DEBUG, "날씨요청 : baseDate(${Utils.calculateBaseDate()}), baseTime(${Utils.calculateBaseTime()})")
                mainVm.getWeather(84, 1, Utils.calculateBaseDate(), Utils.calculateBaseTime(),63, 89)
            } else {
                Snackbar.make(binding.root, getString(R.string.turn_on_gps_plz), 1000).show()
            }
        }
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
            val lat = location.latitude
            val long = location.longitude
            Log.e(DEBUG, "onLocationChanged() : 위도=$lat, 경도=$long")

            mainVm.getAddress(lat, long)
        }

    }

}