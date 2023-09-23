package com.example.criminalintent.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=:id")
    fun getCrime(id: UUID): Crime

    @Update
    fun updateCrime(crime: Crime)

}