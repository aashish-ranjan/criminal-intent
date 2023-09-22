package com.example.criminalintent

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.criminalintent.database.CrimeDatabase
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CrimeRepository private constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).createFromAsset(DATABASE_NAME).build()

    fun getCrimes(): Flow<List<Crime>> {
        Log.d("CRIMEREPOSITORY", "fetching crimelist")
        return database.crimeDao().getCrimes()
    }

    fun getCrime(id: UUID): Crime = database.crimeDao().getCrime(id)

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