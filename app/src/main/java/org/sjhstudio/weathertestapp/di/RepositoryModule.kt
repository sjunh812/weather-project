package org.sjhstudio.weathertestapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.sjhstudio.weathertestapp.data.remote.WeatherApi
import org.sjhstudio.weathertestapp.repository.MainRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun getMainRepository(
        @ApplicationContext context: Context,
        weatherApi: WeatherApi
    ): MainRepository {
        return MainRepository(context, weatherApi)
    }

}