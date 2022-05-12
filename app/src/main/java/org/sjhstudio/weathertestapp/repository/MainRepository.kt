package org.sjhstudio.weathertestapp.repository

import org.sjhstudio.weathertestapp.data.remote.NaverMapApi
import org.sjhstudio.weathertestapp.model.Weather
import org.sjhstudio.weathertestapp.data.remote.WeatherApi
import org.sjhstudio.weathertestapp.model.Geocoder
import org.sjhstudio.weathertestapp.model.ReverseGeocoder
import retrofit2.Call
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val naverMapApi: NaverMapApi
    ) {

    fun geocoding(query: String): Call<Geocoder> {
        println("xxx geocoding()")
        return naverMapApi.geocoding(query)
    }

    fun reverseGeocoding(coords: String): Call<ReverseGeocoder> {
        println("xxx reverseGeocoding()")
        return naverMapApi.reverseGeocoding(coords)
    }

    fun getWeather(
        numOfRows: Int, pageNo: Int,
        baseData: String, baseTime: String,
        nx: Int, ny: Int
    ): Call<Weather> {
        return weatherApi.getWeather(numOfRows, pageNo, baseData, baseTime, nx, ny)
    }

}