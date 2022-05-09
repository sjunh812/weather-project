package org.sjhstudio.weathertestapp.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val context: Context
) {

    suspend fun getAddress(locName: String): List<Address>? = withContext(IO) {
        var list: List<Address>? = null

        try {
            list = Geocoder(context).getFromLocationName(locName, 10)
        } catch(e: IOException) {
            e.printStackTrace()
        }

        list
    }

}