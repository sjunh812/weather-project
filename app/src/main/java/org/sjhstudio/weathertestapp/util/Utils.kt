package org.sjhstudio.weathertestapp.util

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import org.sjhstudio.weathertestapp.R
import org.sjhstudio.weathertestapp.model.Item
import org.sjhstudio.weathertestapp.model.LocalWeather
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object Utils {

    @SuppressLint("SimpleDateFormat")
    val baseDateFormat = SimpleDateFormat("yyyyMMdd")
    @SuppressLint("SimpleDateFormat")
    val baseTimeFormat = SimpleDateFormat("HH00")
    @SuppressLint("SimpleDateFormat")
    val fcstTimeFormat = SimpleDateFormat("HHmm")
    @SuppressLint("SimpleDateFormat")
    val timeFormat = SimpleDateFormat("a hh시")

    fun getDataFormatString(
        value: String,
        inputFormat: SimpleDateFormat,
        outputFormat: SimpleDateFormat
    ): String? {
        val date = inputFormat.parse(value)
        return date?.let { outputFormat.format(it) }
    }

    /**
     * 위치권한 체크
     * @param view : Snackbar에 이용
     */
    fun checkLocPermission(context: Context, view: View): Boolean {
        return if(
            ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            Snackbar.make(view, context.getString(R.string.permit_location_plz), 1000).show()
            false
        }
    }

    fun calculateBaseDate(): String {
        val cal = Calendar.getInstance()
        cal.time = Date()

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        if(hour in 0..1) {
            cal.set(Calendar.DATE, -1)
        } else if(hour==2 && minute<=10) {
                cal.set(Calendar.DATE, -1)
        }

        return baseDateFormat.format(cal.time)
    }

    fun calculateBaseTime2(): String {
        // 추후 수식으로 재정리필요 -> y=3x-1
        val cal = Calendar.getInstance()
        cal.time = Date()

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val y = (hour+1)/3
        val x = y*3-1
        val result = String.format("%04d", x*100)

        return result
    }

    fun calculateBaseTime(): String {
        // Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
        // API 제공 시간(~이후) : 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
        // 추후 수식으로 재정리필요 -> y=3x-1
        val cal = Calendar.getInstance()
        cal.time = Date()

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        when (hour) {
            in 2..4 -> {
                if(hour==2 && minute<=10) {
                    return "2300"
                }
                return "0200"
            }

            in 5..7 -> {
                if(hour==5 && minute<=10) {
                    return "0200"
                }
                return "0500"
            }

            in 8..10 -> {
                if(hour==8 && minute<=10) {
                    return "0500"
                }
                return "8200"
            }

            in 11..13 -> {
                if(hour==11 && minute<=10) {
                    return "0800"
                }
                return "1100"
            }

            in 14..16 -> {
                if(hour==14 && minute<=10) {
                    return "1100"
                }
                return "1400"
            }

            in 17..19 -> {
                if(hour==17 && minute<=10) {
                    return "1400"
                }
                return "1700"
            }

            in 20..22 -> {
                if(hour==20 && minute<=10) {
                    return "1700"
                }
                return "2000"
            }

            else -> {
                // 23~1
                if(hour==23 && minute<=10) {
                    return "2000"
                }
                return "2300"
            }
        }

    }

    fun getWeathers(items: List<Item>): ArrayList<LocalWeather> {
        val result = arrayListOf<LocalWeather>()
        var fcstTime = items[0].fcstTime
        var localWeather = LocalWeather()
        var sky = ""
        var pty = ""

        items.forEach {
            if(fcstTime.toInt() == it.fcstTime.toInt()) {
                when(it.category) {
                    "SKY" -> {  // 하늘상태
                        if(it.fcstValue == "1") sky = "맑음"
                        else if(it.fcstValue == "3") sky = "구름많음"
                        else if(it.fcstValue == "4") sky = "흐림"
                        else Log.d(DEBUG, "calculateWeather() : 날씨파싱 에러")
                    }

                    "PTY" -> {  // 강수형태
                        if(it.fcstValue == "1") pty = "비"
                        else if(it.fcstValue == "2") pty = "비/눈"
                        else if(it.fcstValue == "3") pty = "눈"
                        else if(it.fcstValue == "4") pty = "소나기"
                        else Log.d(DEBUG, "calculateWeather() : 날씨파싱 에러")
                    }

                    "POP" -> {  // 강수확률
                        if(it.fcstValue != "0") localWeather.chanceOfShower = it.fcstValue
                    }

                    "TMP" -> {  // 기온
                        localWeather.temp = it.fcstValue
                    }
                }
            } else {
                localWeather.apply {
                    time = getDataFormatString(
                        fcstTime,
                        fcstTimeFormat,
                        timeFormat
                    )
                    weather = pty.ifEmpty { sky }
                }
                result.add(localWeather)

                sky = ""
                pty = ""
                localWeather = LocalWeather()
                fcstTime = it.fcstTime
            }
        }

        localWeather.apply {
            time = getDataFormatString(
                fcstTime.toString(),
                fcstTimeFormat,
                timeFormat
            )
            weather = pty.ifEmpty { sky }
        }
        result.add(localWeather)

        return result
    }

}