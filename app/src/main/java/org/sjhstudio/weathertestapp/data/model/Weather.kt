package org.sjhstudio.weathertestapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    val response: Response
): Parcelable

@Parcelize
data class Response(
    val header: Header,
    val body: Body
): Parcelable

@Parcelize
data class Header(
    val resultCode: String,
    val resultMsg: String
): Parcelable

@Parcelize
data class Body(
    val dataType: String,
    val items: Items,
    val pageNo: Int,
    val numOfRows: Int,
    val totalCount: Int
): Parcelable

@Parcelize
data class Items(
    val item: List<Item>
): Parcelable

@Parcelize
data class Item(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String,
    val nx: Int,
    val ny: Int
): Parcelable