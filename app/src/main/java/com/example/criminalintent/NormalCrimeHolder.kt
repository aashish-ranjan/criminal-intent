package com.example.criminalintent

import android.icu.text.DateFormat
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.criminalintent.databinding.ListItemNormalCrimeBinding
import com.example.criminalintent.model.Crime

class NormalCrimeHolder(private val binding: ListItemNormalCrimeBinding): ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.apply {
            crimeTitleTextview.text = crime.title
            crimeDateTextview.text = DateFormat.getDateInstance(DateFormat.FULL).format(crime.date)

            root.setOnClickListener{
                Toast.makeText(root.context, "${crime.title} clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}