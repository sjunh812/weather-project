package org.sjhstudio.weathertestapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Geocoder(
    val status: String,

): Parcelable

@Parcelize
data class GMeta(
    val totalCount: Int,
    val page: Int,
    val count: Int
): Parcelable

@Parcelize
data class Addresses(
    val roadAddress: String,
    val jibunAddress: String,
    val englishAddress: String
): Parcelable