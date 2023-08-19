package com.example.criminalintent

import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.criminalintent.databinding.ListItemCrimeBinding
import com.example.criminalintent.model.Crime

class CrimeHolder(private val binding: ListItemCrimeBinding): ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.apply {
            crimeTitleTextview.text = crime.title
            crimeDateTextview.text = crime.date.toString()

            root.setOnClickListener{
                Toast.makeText(root.context, "${crime.title} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}