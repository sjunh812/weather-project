package org.sjhstudio.weathertestapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalWeather(
    var weather: String? = null,    // 날씨
    var time: String? = null,  // 시간
    var temp: String? = null,  // 기온
    var chanceOfShower: String? = null // 강수확률
): Parcelable