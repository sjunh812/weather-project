package org.sjhstudio.weathertestapp.util

import android.graphics.Point
import android.util.Log
import org.sjhstudio.weathertestapp.R
import org.sjhstudio.weathertestapp.model.Item
import org.sjhstudio.weathertestapp.model.LocalWeather
import org.sjhstudio.weathertestapp.model.MainWeatherData
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import java.util.*
import kotlin.math.*

object WeatherHelper {

    private val calendar: Calendar = Calendar.getInstance().apply { time = Date() }
    private val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
    private val minute: Int = calendar.get(Calendar.MINUTE)

    fun calculateBaseTime(): String {
        // Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
        // API 제공 시간(~이후) : 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
        // 수식 : y = 3x - 1
        var newHour = hour

        if(hour < 2) newHour = 2

        val x = (newHour + 1) / 3
        var y = 3 * x - 1

        return if((newHour + 1) % 3 == 0 && minute <= 10) {
            y -= 3
            if(y < 0) "2300"
            else String.format("%04d", y * 100)
        } else {
            String.format("%04d", y * 100)
        }
    }

    fun getBaseDate(): String =
        if(hour >= 23 && minute >= 10) Utils.baseDateFormat.format(Date())
        else Utils.baseDateFormat.format(Utils.getYesterday())

    fun getBaseTime(): String =
        if(hour >= 23 && minute >= 10) "0200" else "2300"

    /**
     * Item -> MainWeatherData
     */
    fun getMainWeatherData(items: List<Item>): MainWeatherData {
        val mainWeatherData = MainWeatherData()
        val localWeathers = arrayListOf<LocalWeather>()
        var localWeather = LocalWeather()
        val today = Utils.baseDateFormat.format(Date())
        val curFcstTime = Utils.fcstTimeFormat.format(Date())
        var sky = ""    // 하늘상태
        var pty = ""    // 강수상태

        items.forEachIndexed { i, it ->
            when (it.category) {
                // 하늘상태
                "SKY" -> {
                    if (it.fcstValue == "1") sky = "맑음"
                    else if (it.fcstValue == "3") sky = "구름많음"
                    else if (it.fcstValue == "4") sky = "흐림"
                    else Log.d(DEBUG, "calculateWeather() : SKY 날씨파싱 에러(${it.fcstValue})")
                }

                // 강수형태
                "PTY" -> {
                    if (it.fcstValue == "1") pty = "비"
                    else if (it.fcstValue == "2") pty = "비/눈"
                    else if (it.fcstValue == "3") pty = "눈"
                    else if (it.fcstValue == "4") pty = "소나기"
                    else Log.d(DEBUG, "calculateWeather() : PTY 날씨파싱 에러(${it.fcstValue})")
                }

                // 강수확률
                "POP" -> localWeather.chanceOfShower = it.fcstValue

                // 기온
                "TMP" -> localWeather.temp = it.fcstValue

                // 최고기온
                "TMX" -> {
                    if (it.fcstDate == today) {
                        mainWeatherData.tmx = it.fcstValue
                        Log.e(DEBUG, "$today 최고기온 : ${it.fcstValue}")
                    }
                }

                //최저기온
                "TMN" -> {
                    if (it.fcstDate == today) {
                        mainWeatherData.tmn = it.fcstValue
                        Log.e(DEBUG, "$today 최저기온 : ${it.fcstValue}")
                    }
                }
            }

            if (items.lastIndex == i || items[i+1].fcstTime != it.fcstTime) {
                if(it.fcstDate == today && it.fcstTime == curFcstTime) {
                    // 현재시간
                    mainWeatherData.weather = pty.ifEmpty { sky }
                    mainWeatherData.temp = localWeather.temp
                    Log.e(DEBUG, "$today $curFcstTime(현재) : 날씨=${mainWeatherData.weather}, 기온=${mainWeatherData.temp}")
                } else if(it.fcstDate == today && it.fcstTime.toInt() > curFcstTime.toInt()
                    || it.fcstDate.toInt() > today.toInt()
                ) {
                    // 현재시간 이후
                    localWeathers.add(
                        localWeather.apply {
                            time = Utils.getDataFormatString(
                                it.fcstTime,
                                Utils.beforeTimeFormat,
                                Utils.afterTimeFormat
                            )
                            weather = pty.ifEmpty { sky }
                        }
                    )
                }

                localWeather = LocalWeather()
                sky = ""
                pty = ""
            }
        }

        mainWeatherData.weathers = localWeathers
        return mainWeatherData
    }

    /**
     * 위,경도를 이용하여
     * 기상청에서 사용하는 격자 좌표로 변환
     */
    fun coordinateTransformation(v1: Double, v2: Double) : Point {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0  // 격자 간격(km)
        val SLAT1 = 30.0    // 투영 위도1(degree)
        val SLAT2 = 60.0    // 투영 위도2(degree)
        val OLON = 126.0    // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43 // 기준점 X좌표(GRID)
        val YO = 136    // 기준점 Y좌표(GRID)
        val DEGRAD = Math.PI / 180.0
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        var sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = ln(cos(slat1) / cos(slat2)) / ln(sn)

        var sf = tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = sf.pow(sn) * cos(slat1) / sn

        var ro = tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / ro.pow(sn)

        var ra = tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5)
        ra = re * sf / ra.pow(sn)

        var theta = v2 * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val x = (ra * sin(theta) + XO + 0.5).toInt()
        val y = (ro - ra * cos(theta) + YO + 0.5).toInt()

        return Point(x, y)
    }

    /**
     * 날씨값을 이용해 리소스 반환
     * 1. Background color
     * 2. Image(main or item)
     */
    fun getWeatherResource(
        weather: String,    // 날씨
        isImage: Boolean = false,   // 이미지?
        isSmallImage: Boolean = false   // 작은이미지?
    ): Int {
        return when(weather) {
            "맑음" -> {
                if(isImage) {
                    if(isSmallImage) R.drawable.ic_sunny_small
                    else R.drawable.ic_sunny
                } else R.color.sunny
            }

            "구름많음" -> {
                if(isImage) {
                    if(isSmallImage) R.drawable.ic_cloudy_small
                    else R.drawable.ic_cloudy
                } else R.color.cloudy
            }

            "흐림" -> {
                if(isImage) {
                    if(isSmallImage) R.drawable.ic_overcast_small
                    else R.drawable.ic_overcast
                } else R.color.overcast
            }

            "비" -> {
                if(isImage) {
                    if(isSmallImage) R.drawable.ic_rainy_small
                    else R.drawable.ic_rainy
                } else R.color.rainy
            }

            "비/눈" -> {
                if(isImage) {
                    if(isSmallImage) R.drawable.ic_rain_snow_small
                    else R.drawable.ic_rain_snow
                } else R.color.rain_snow
            }

            "눈" -> {
                if(isImage) {
                    if(isSmallImage) R.drawable.ic_snow_small
                    else R.drawable.ic_snow
                }
                else R.color.snow
            }

            else -> {   // 소나기
                if(isImage) {
                    if(isSmallImage) R.drawable.ic_rainy_small
                    else R.drawable.ic_rainy
                } else R.color.shower
            }
        }
    }

}