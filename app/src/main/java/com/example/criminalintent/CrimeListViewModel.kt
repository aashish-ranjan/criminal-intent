package com.example.criminalintent

import androidx.lifecycle.ViewModel
import com.example.criminalintent.model.Crime
import java.util.Date
import java.util.UUID

class CrimeListViewModel: ViewModel() {

    val crimeList = mutableListOf<Crime>()

    fun loadCrimes() {
        for (i in 1..100) {
            crimeList.add(Crime(UUID.randomUUID(), "Crime #$i", Date(), i%2 == 0))
        }
    }
}