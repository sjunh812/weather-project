package org.sjhstudio.weathertestapp.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import dagger.hilt.android.AndroidEntryPoint
import org.sjhstudio.weathertestapp.R
import org.sjhstudio.weathertestapp.ui.adapter.WeatherAdapter
import org.sjhstudio.weathertestapp.databinding.ActivityMainBinding
import org.sjhstudio.weathertestapp.model.Addresses
import org.sjhstudio.weathertestapp.util.*
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import org.sjhstudio.weathertestapp.util.Constants.RESULT_SEARCH_AREA
import org.sjhstudio.weathertestapp.util.Constants.WEATHER_API_ERROR
import org.sjhstudio.weathertestapp.util.Constants.WEATHER_NUM_OF_ROWS
import org.sjhstudio.weathertestapp.util.Constants.WEATHER_PAGE_NO
import org.sjhstudio.weathertestapp.viewmodel.MainViewModel
import java.util.*
import javax.inject.Inject

/**
 * AndroidEntryPoint : 객체가 주입되는 대상
 */
@AndroidEntryPoint
class MainActivity: BaseActivity() {

    private val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }
    private val mainVm: MainViewModel by viewModels()
    private val locationListener: MyLocationListener by lazy { MyLocationListener() }
    private val weatherAdapter: WeatherAdapter by lazy { WeatherAdapter() }

    @Inject
    lateinit var locationManager: LocationManager
    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    private var networkDialog: AlertDialog? = null
    private var isReady = false

    override fun onResume() {
        super.onResume()
        InAppUpdateHelper.resumeInAppUpdate(this, appUpdateManager)
    }

    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(locationListener)
        InAppUpdateHelper.setOnInAppUpdateCallback(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InAppUpdateHelper.apply {
            setOnInAppUpdateCallback(object: OnInAppUpdateCallback {
                override fun onFailed() {
                    requestLocationPermission()
                    observeAddress()
                    observeWeather()
                    observeError()
                }
            })
            inAppUpdate(this@MainActivity, appUpdateManager)
        }
        
        binding.searchBtn.setOnClickListener {  // 지역검색
            searchAreaResult.launch(Intent(this, SearchActivity::class.java))
        }
        binding.weatherRv.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        }

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
        mainVm.address.observe(this) { reverseGeocode ->
            if(reverseGeocode.results.isNotEmpty()) {
                val result = reverseGeocode.results.first()
                val country = result.region.area0.name  // 국가
                val sido = result.region.area1.name // 시도
                val gungu = result.region.area2.name    // 시군구
                val dong = result.region.area3.name // 읍면동
                val lee = result.region.area4.name  // 리
                val address = "$gungu $dong $lee"

                binding.addressTv.text = address
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeWeather() {
        mainVm.weather.observe(this) { weather ->
            println("xxx observeWeather()")
            isReady = true  // 화면출력
            val items = weather.response.body.items.item
            val data = WeatherHelper.getMainWeatherData(items)
            val colorRes = WeatherHelper.getWeatherResource(data.weather!!)
            val imgRes = WeatherHelper.getWeatherResource(data.weather!!, true)

            Utils.setStatusBarColor(this, colorRes)
            binding.mainWeatherData = data
            binding.container.visibility = View.VISIBLE
            binding.errorLayout.visibility = View.GONE
            binding.container.setBackgroundColor(ContextCompat.getColor(this, colorRes))
            binding.weatherImg.setImageResource(imgRes)
            binding.dateTv.text = Utils.dateFormat.format(Date())
            binding.tmxTmnTv.text = "최고 ${data.tmx}° / 최저 ${data.tmn}°"
            binding.hourlyForecastProgressBar.visibility = View.GONE
            weatherAdapter.setItems(data.weathers!!)
/*            for(item in items) {
                Log.e(DEBUG, "$item")
            }*/
        }
    }

    private fun observeError() {
        mainVm.error.observe(this) { err ->
            if(err == WEATHER_API_ERROR) {
                setErrorUi(getString(R.string.notice_server_network_error))

                if(networkDialog == null) {
                    networkDialog = Utils.getYesOrNOAlertDialog(
                        this,
                        { dialog, _ ->
                            getLocation()
                            dialog.dismiss()
                        },
                        { dialog, _ ->
                            dialog.dismiss()
                        }
                    )
                }

                if(!(networkDialog!!.isShowing)) {
                    networkDialog!!.show()
                }
            }
        }
    }

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
//                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                lastLocation?.let { loc ->
//                    Log.e(DEBUG, "getLastKnownLocation() : GPS_PROVIDER")
//                    setMainViewModelData(loc)
//                }

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0,
                    0f, locationListener
                )
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0,
                    0f, locationListener
                )
            } else {
                Snackbar.make(binding.root, getString(R.string.turn_on_gps_plz), 1000).show()
            }
        }
    }

    private fun requestMainViewModelLiveData(lat: Double, long: Double) {
        val point = WeatherHelper.coordinateTransformation(lat, long)
        val baseDate = WeatherHelper.getBaseDate()
        val baseTime = WeatherHelper.getBaseTime()

        mainVm.getAddress(lat, long)
        mainVm.getWeather(
            WEATHER_NUM_OF_ROWS, WEATHER_PAGE_NO,
            baseDate, baseTime,
            point.x, point.y
        )

        Log.e(DEBUG, "getAddress() : 위도=$lat, 경도=$long")
        Log.e(DEBUG, "getWeather() : baseDate($baseDate), baseTime($baseTime) nx=${point.x}, ny=${point.y}")
    }

    private fun setErrorUi(errMsg: String) {
        isReady = true  // 화면출력
        binding.container.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.errorTv.text = errMsg
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQ_IN_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                println("xxx Update flow failed! Result code: $resultCode")
                finishAffinity()    // 앱 종료
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
                setErrorUi(getString(R.string.permit_location_plz))
                Snackbar.make(binding.container, "앱 사용을 위해 위치권한을 허용해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private val searchAreaResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK) {
            // 지역검색 완료!!
            // 해당지역 날씨요청
            val item = result.data?.getParcelableExtra<Addresses>(RESULT_SEARCH_AREA)
            item?.let {
                try {
                    binding.hourlyForecastProgressBar.visibility = View.VISIBLE
                    weatherAdapter.clear()
                    requestMainViewModelLiveData(item.y.toDouble(), item.x.toDouble())
                } catch(e: Exception) {
                    e.printStackTrace()
                    Log.e(DEBUG, "위,경도 파싱에러")
                }
            }
        }
    }

    inner class MyLocationListener: LocationListener  {

        override fun onLocationChanged(location: Location)  {
            Log.e(DEBUG, "onLocationChanged()")
            requestMainViewModelLiveData(location.latitude, location.longitude)
            locationManager.removeUpdates(locationListener)
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
        }

    }

}