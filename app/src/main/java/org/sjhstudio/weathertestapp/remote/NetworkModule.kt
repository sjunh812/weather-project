package org.sjhstudio.weathertestapp.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.MINUTES)
            .build()
    }

    fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getWeatherApi(url: String): WeatherApi {
        return getRetrofit(url).create(WeatherApi::class.java)
    }

}