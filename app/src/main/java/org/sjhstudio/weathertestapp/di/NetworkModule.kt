package org.sjhstudio.weathertestapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.sjhstudio.weathertestapp.data.remote.NaverMapApi
import org.sjhstudio.weathertestapp.data.remote.WeatherApi
import org.sjhstudio.weathertestapp.util.Constants.APIS_DATA_URL
import org.sjhstudio.weathertestapp.util.Constants.NAVER_MAP_API_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 외부라이브러리에 Hilt 종속성을
 * 부여하기위해 Module 생성.
 *
 * (2.28.1이상 버전 = ApplicationComponent -> SingletonComponent)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    annotation class RetrofitApis
    @Qualifier
    annotation class RetrofitNaverMap

    @Singleton
    @Provides
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.MINUTES)
            .build()
    }

    @Singleton
    @Provides
    @RetrofitApis
    fun getRetrofitApis(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(APIS_DATA_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @RetrofitNaverMap
    fun getRetrofitNaverMap(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(NAVER_MAP_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun getWeatherApi(@RetrofitApis retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun getNaverMapApi(@RetrofitNaverMap retrofit: Retrofit): NaverMapApi {
        return retrofit.create(NaverMapApi::class.java)
    }

}