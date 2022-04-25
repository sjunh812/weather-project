package org.sjhstudio.weathertestapp.remote

import org.sjhstudio.weathertestapp.model.Weather
import org.sjhstudio.weathertestapp.util.ApiKey.WEATHER_API_KEY
import org.sjhstudio.weathertestapp.util.Constants.JSON
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=$WEATHER_API_KEY")
    fun getWeather(
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int,
        @Query("dataType") dataType: String = JSON
    ): Call<Weather>

}