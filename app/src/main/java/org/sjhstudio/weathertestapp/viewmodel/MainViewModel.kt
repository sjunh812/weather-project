package org.sjhstudio.weathertestapp.viewmodel

import android.app.Application
import android.location.Address
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.sjhstudio.weathertestapp.data.model.Weather
import org.sjhstudio.weathertestapp.repository.MainRepository
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import org.sjhstudio.weathertestapp.util.Constants.WEATHER_API_ERROR
import retrofit2.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository
): AndroidViewModel(application) {

    private var _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private var _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather>
        get() = _weather

    private var _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

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
                _error.value = WEATHER_API_ERROR
            }
        }
    }

}