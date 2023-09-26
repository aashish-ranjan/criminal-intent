package com.example.criminalintent

import android.content.Context
import androidx.room.Room
import com.example.criminalintent.database.CrimeDatabase
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeRepository private constructor(context: Context, private val scope: CoroutineScope = GlobalScope) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    fun getCrimes(): Flow<List<Crime>> {
        return database.crimeDao().getCrimes()
    }

    fun getCrime(id: UUID): Crime = database.crimeDao().getCrime(id)

    fun updateCrime(crime: Crime) {
        scope.launch {
            database.crimeDao().updateCrime(crime)
        }
    }

    fun insertCrime(crime: Crime) {
        scope.launch {
            database.crimeDao().insertCrime(crime)
        }
    }

    fun deleteCrime(crime: Crime) {
        scope.launch {
            database.crimeDao().deleteCrime(crime)
        }
    }

    companion object {
        private const val DATABASE_NAME = "crime-database"
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context.applicationContext)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initalized")
        }
    }
}