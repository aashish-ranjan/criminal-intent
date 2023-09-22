package com.example.criminalintent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CrimeListViewModel: ViewModel() {
    private  val TAG = "CrimeListViewModel"
    private val repository = CrimeRepository.get()
    private val _crimeListStateFlow = MutableStateFlow<List<Crime>>(emptyList())
    val crimeListStateFlow
        get() = _crimeListStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCrimes().collectLatest {
                _crimeListStateFlow.value = it
            }
        }
    }
}