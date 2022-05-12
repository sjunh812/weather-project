package org.sjhstudio.weathertestapp.data.remote

import org.sjhstudio.weathertestapp.model.Geocoder
import org.sjhstudio.weathertestapp.model.ReverseGeocoder
import org.sjhstudio.weathertestapp.util.ApiKey.NAVER_MAP_ACCEPT
import org.sjhstudio.weathertestapp.util.ApiKey.NAVER_MAP_CLIENT_ID
import org.sjhstudio.weathertestapp.util.ApiKey.NAVER_MAP_CLIENT_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaverMapApi {

    @Headers(
        "X-NCP-APIGW-API-KEY-ID: $NAVER_MAP_CLIENT_ID",
        "X-NCP-APIGW-API-KEY: $NAVER_MAP_CLIENT_KEY",
        "Accept: $NAVER_MAP_ACCEPT"
    )
    @GET("/map-geocode/v2/geocode")
    fun geocoding(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("count") count: Int = 10
    ): Call<Geocoder>

    @Headers(
        "X-NCP-APIGW-API-KEY-ID: $NAVER_MAP_CLIENT_ID",
        "X-NCP-APIGW-API-KEY: $NAVER_MAP_CLIENT_KEY"
    )
    @GET("/map-reversegeocode/v2/gc")
    fun reverseGeocoding(
        @Query("coords") coords: String,
        @Query("output") output: String = "json"
    ): Call<ReverseGeocoder>

}