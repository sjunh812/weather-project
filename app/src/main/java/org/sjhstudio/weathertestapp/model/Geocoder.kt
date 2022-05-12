package org.sjhstudio.weathertestapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Geocoder(
    val status: String,
    val meta:  GMeta,
    val addresses: List<Addresses>,
    val errorMessage: String
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
    val englishAddress: String,
    val addressElements: List<AddressElements>,
    val x: String,
    val y: String,
    val distance: Double
): Parcelable

@Parcelize
data class AddressElements(
    val types: List<String>,
    val longName: String,
    val shortName: String,
    var code: String
): Parcelable