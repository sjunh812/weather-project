package org.sjhstudio.weathertestapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainWeatherData(
    var weather: String? = null,    // 현재날씨
    var temp: String? = null,   // 현재기온
    var tmx: String? = null,    // 최고기온
    var tmn: String? = null,    // 최저기온
    var weathers: ArrayList<LocalWeather>? = null   // 오늘00시~내일23시
): Parcelable