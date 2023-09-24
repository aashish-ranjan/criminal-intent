package com.example.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import java.util.Calendar

class DatePickerFragment: DialogFragment() {

    private val args: DatePickerFragmentArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        calendar.time = args.crimeDate
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialYear = calendar.get(Calendar.YEAR)

        return DatePickerDialog(
            requireContext(),
            null,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        const val REQ_KEY_PICK_DATE = "pickDate"
        const val BUNDLE_KEY_SELECTED_DATE = "selectedDate"
    }


}