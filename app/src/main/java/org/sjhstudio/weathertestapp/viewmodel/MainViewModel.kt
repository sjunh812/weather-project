package org.sjhstudio.weathertestapp.viewmodel

import android.app.Application
import android.location.Address
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.sjhstudio.weathertestapp.model.ReverseGeocoder
import org.sjhstudio.weathertestapp.model.Weather
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

    private var _address = MutableLiveData<ReverseGeocoder>()
    val address: LiveData<ReverseGeocoder>
        get() = _address

    private var _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather>
        get() = _weather

    private var _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getAddress(lat: Double, long: Double) {
        viewModelScope.launch {
            try {
                val coords = "$long,$lat"
                val call = mainRepository.reverseGeocoding(coords)
                val response = call.await()
                _address.value = response
            } catch(e: Exception) {
                e.printStackTrace()
                Log.e(DEBUG, "Reverse Geocoding Api Error")
            }
        }
    }

    fun getWeather(
        numOfRows: Int, pageNo: Int,
        baseDate: String, baseTime: String,
        nx: Int, ny: Int
    ) {
        viewModelScope.launch {
            try {
                val call = mainRepository.getWeather(numOfRows, pageNo, baseDate, baseTime, nx, ny)
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