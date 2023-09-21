package com.example.criminalintent.database

import androidx.room.TypeConverter
import java.util.Date

class CrimeTypeConverter {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisInEpoch: Long): Date {
        return Date(millisInEpoch)
    }
}