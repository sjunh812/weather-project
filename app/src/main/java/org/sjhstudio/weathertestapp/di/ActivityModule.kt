package org.sjhstudio.weathertestapp.di

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @ActivityScoped
    @Provides
    fun getLocationManager(activity: Activity): LocationManager {
        return activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @ActivityScoped
    @Provides
    fun getAppUpdateManager(activity: Activity): AppUpdateManager {
        return AppUpdateManagerFactory.create(activity)
    }

}