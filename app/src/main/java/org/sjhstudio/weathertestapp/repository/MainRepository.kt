package org.sjhstudio.weathertestapp.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.sjhstudio.weathertestapp.model.Weather
import org.sjhstudio.weathertestapp.data.remote.WeatherApi
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import retrofit2.Call
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    @ApplicationContext private val application: Context,
    private val weatherApi: WeatherApi
    ) {

    suspend fun getAddress(lat: Double, long: Double): Address? = withContext(IO) {
        var addr: Address? = null

         try {
             val addrList = Geocoder(application).getFromLocation(lat, long, 1)
             addr = addrList[0]
         } catch(e: IOException) {
             e.printStackTrace()
             Log.e(DEBUG, "주소변환 에러")
         }

        addr
    }

    fun getWeather(
        numOfRows: Int, pageNo: Int,
        baseData: String, baseTime: String,
        nx: Int, ny: Int
    ): Call<Weather> {
        return weatherApi.getWeather(numOfRows, pageNo, baseData, baseTime, nx, ny)
    }

}