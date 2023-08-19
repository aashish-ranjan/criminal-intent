package com.example.criminalintent

import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ListItemSeriousCrimeBinding
import com.example.criminalintent.model.Crime

class SeriousCrimeViewHolder(private val binding: ListItemSeriousCrimeBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.apply {
            crimeTitleTextview.text = crime.title
            crimeDateTextview.text = crime.date.toString()

            callPoliceIcon.setOnClickListener {
                Toast.makeText(root.context, "Reporting ${crime.title} to police", Toast.LENGTH_SHORT).show()
            }

            root.setOnClickListener {
                Toast.makeText(root.context, "Crime ${crime.title} clicked", Toast.LENGTH_SHORT).show()
            }
        }

    }

}