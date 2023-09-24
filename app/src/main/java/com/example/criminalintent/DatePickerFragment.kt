package com.example.criminalintent

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.Calendar
import java.util.GregorianCalendar

class DatePickerFragment: DialogFragment() {

    private val args: DatePickerFragmentArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateSetListener = OnDateSetListener { _, year, month, dayOfMonth ->
            val date = GregorianCalendar(year, month, dayOfMonth).time
            val bundle = bundleOf(BUNDLE_KEY_SELECTED_DATE to date)
            setFragmentResult(REQ_KEY_PICK_DATE, bundle)
        }

        val calendar = Calendar.getInstance()
        calendar.time = args.crimeDate
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialYear = calendar.get(Calendar.YEAR)

        return DatePickerDialog(
            requireContext(),
            dateSetListener,
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