package com.example.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel: ViewModel() {
    private val repository = CrimeRepository.get()

    fun loadCrimes() = repository.getCrimes()
}