package org.sjhstudio.weathertestapp.util

import com.google.android.play.core.install.model.AppUpdateType

object Constants {

    const val DEBUG = "debug"
    const val JSON = "JSON"

    const val APIS_DATA_URL = "http://apis.data.go.kr"

    const val WEATHER_NUM_OF_ROWS = 580 // 한페이지 결과수
    const val WEATHER_PAGE_NO = 1   // 페이지번호
    const val WEATHER_API_ERROR = "weather_api_error"

    const val IN_APP_UPDATE_TYPE = AppUpdateType.IMMEDIATE  // 인앱업데이트=강제
    const val REQ_IN_APP_UPDATE = 10

}