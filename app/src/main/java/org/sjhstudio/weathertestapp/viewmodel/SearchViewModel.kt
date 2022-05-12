package org.sjhstudio.weathertestapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.sjhstudio.weathertestapp.model.Addresses
import org.sjhstudio.weathertestapp.repository.MainRepository
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import retrofit2.await
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository
    ): AndroidViewModel(application) {

    private var _addrList = MutableLiveData<List<Addresses>>()
    val addrList: LiveData<List<Addresses>>
        get() = _addrList

    fun getAddressList(query: String) {
        viewModelScope.launch {
            try {
                val call = mainRepository.geocoding(query)
                val response = call.await()
                _addrList.value = response.addresses
            } catch(e: Exception) {
                e.printStackTrace()
                Log.e(DEBUG, "Geocoding Api Error")
            }
        }
    }

}