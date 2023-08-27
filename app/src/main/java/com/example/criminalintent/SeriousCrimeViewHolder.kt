package com.example.criminalintent

import android.icu.text.DateFormat
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ListItemSeriousCrimeBinding
import com.example.criminalintent.model.Crime

class SeriousCrimeViewHolder(private val binding: ListItemSeriousCrimeBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.apply {
            crimeTitleTextview.text = crime.title
            crimeDateTextview.text = DateFormat.getDateInstance(DateFormat.FULL).format(crime.date)

            seriousCrimeIcon.setOnClickListener {
                Toast.makeText(root.context, "Reporting ${crime.title} to police", Toast.LENGTH_SHORT).show()
            }

            root.setOnClickListener {
                Toast.makeText(root.context, "Crime ${crime.title} clicked", Toast.LENGTH_SHORT).show()
            }
        }

    }

}