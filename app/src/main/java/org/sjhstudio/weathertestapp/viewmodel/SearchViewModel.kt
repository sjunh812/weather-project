package org.sjhstudio.weathertestapp.viewmodel

import android.app.Application
import android.location.Address
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.sjhstudio.weathertestapp.repository.MainRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository
    ): AndroidViewModel(application) {

    private var _addrList = MutableLiveData<List<Address>>()
    val addrList: LiveData<List<Address>>
        get() = _addrList

    fun getAddressList(locName: String) {
        viewModelScope.launch {
            val list = mainRepository.getAddressList(locName)
            list?.let { it -> _addrList.value = it }
        }
    }

}