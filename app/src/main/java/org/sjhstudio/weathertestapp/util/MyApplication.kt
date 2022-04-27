package org.sjhstudio.weathertestapp.util

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt 을 사용하는 모든 앱은
 * @HiltAndroidApp 으로 지정된 Application 클래스를
 * 반드시 포함해야 한다!!
 */
@HiltAndroidApp
class MyApplication: Application() {
}