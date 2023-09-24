package com.example.criminalintent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.Calendar
import java.util.Date
import java.util.UUID

class CrimeDetailViewModel(private val crimeId: UUID): ViewModel() {

    private val _crimeStateFlow: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crimeStateFlow = _crimeStateFlow.asStateFlow()
    private val repository = CrimeRepository.get()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _crimeStateFlow.value = repository.getCrime(crimeId)
        }
    }

    fun updateCrime(onUpdate: (Crime) -> Crime) {
        _crimeStateFlow.update {
            it?.let { oldCrime ->
                onUpdate(oldCrime)
            }
        }
    }

    override fun onCleared() {
        _crimeStateFlow.value?.let {
            repository.updateCrime(it)
        }
        super.onCleared()
    }

    fun getNewDate(date: Date, hourOfDay: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }.time
    }
}

class CrimeDetailViewModelFactory(private val crimeId: UUID): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CrimeDetailViewModel::class.java)) {
            return CrimeDetailViewModel(crimeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}