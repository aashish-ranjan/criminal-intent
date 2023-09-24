package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.Calendar

class TimePickerFragment: DialogFragment() {

    private val args: TimePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val onTimeDateSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val resultBundle = bundleOf(
                BUNDLE_KEY_HOUR to hourOfDay,
                BUNDLE_KEY_MINUTE to minute
            )
            setFragmentResult(REQ_KEY_PICK_TIME, resultBundle)
        }

        val calendar = Calendar.getInstance()
        calendar.time = args.crimeDate
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            onTimeDateSetListener,
            initialHour,
            initialMinute,
            true
        )
    }

    companion object {
        const val BUNDLE_KEY_HOUR = "selectedHour"
        const val BUNDLE_KEY_MINUTE = "selectedMinute"
        const val REQ_KEY_PICK_TIME = "pickTime"
    }
}