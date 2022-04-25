package org.sjhstudio.weathertestapp.viewmodel

import android.app.Application
import android.location.Address
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sjhstudio.weathertestapp.model.Weather
import org.sjhstudio.weathertestapp.repository.MainRepository
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import retrofit2.await

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val mainRepository = MainRepository(application)

    private var _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private var _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather>
        get() = _weather

    fun getAddress(lat: Double, long: Double) {
        viewModelScope.launch {
            val address = mainRepository.getAddress(lat, long)
            address?.let { _address.value = it }
        }
    }

    fun getWeather(
        numOfRows: Int, pageNo: Int,
        baseData: String, baseTime: String,
        nx: Int, ny: Int
    ) {
        viewModelScope.launch {
            try {
                val call = mainRepository.getWeather(numOfRows, pageNo, baseData, baseTime, nx, ny)
                val response = call.await()
                _weather.value = response
            } catch(e: Exception) {
                e.printStackTrace()
                Log.e(DEBUG, "단기예보 API 에러")
            }
        }
    }

}