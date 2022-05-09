package org.sjhstudio.weathertestapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.sjhstudio.weathertestapp.repository.SearchRepository

@HiltViewModel
class SearchViewModel(private val searchRepository: SearchRepository): ViewModel() {



}