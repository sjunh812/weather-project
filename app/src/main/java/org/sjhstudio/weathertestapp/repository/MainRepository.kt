package org.sjhstudio.weathertestapp.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import org.sjhstudio.weathertestapp.model.Weather
import org.sjhstudio.weathertestapp.data.remote.WeatherApi
import retrofit2.Call
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    @ApplicationContext private val application: Context,
    private val weatherApi: WeatherApi
    ) {

    fun getAddress(lat: Double, long: Double): Address?  {
        println("xxx getAddress() : lat=$lat, long=$long")
        var addr: Address? = null

        try {
            val addrList = Geocoder(application).getFromLocation(lat, long, 5)
            if(addrList.isNotEmpty()) addr = addrList[0]
        } catch(e: IOException) {
            e.printStackTrace()
        }

        return addr
    }

    fun getAddressList(locName: String): List<Address>? {
        println("xxx getAddressList() : locName=$locName")
        var list: List<Address>? = null

        try {
            list = Geocoder(application).getFromLocationName(locName, 10)
        } catch(e: IOException) {
            e.printStackTrace()
        }

        return list
    }

    fun getWeather(
        numOfRows: Int, pageNo: Int,
        baseData: String, baseTime: String,
        nx: Int, ny: Int
    ): Call<Weather> {
        return weatherApi.getWeather(numOfRows, pageNo, baseData, baseTime, nx, ny)
    }

}