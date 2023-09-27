package com.example.criminalintent.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    fun getReadableDateAndTime(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(date)
    }
}