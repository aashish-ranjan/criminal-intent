package com.example.criminalintent

import android.icu.text.DateFormat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.criminalintent.databinding.ListItemNormalCrimeBinding
import com.example.criminalintent.model.Crime
import java.util.UUID

class NormalCrimeHolder(private val binding: ListItemNormalCrimeBinding): ViewHolder(binding.root) {
    fun bind(crime: Crime, onCrimeItemClick: (UUID) -> Unit) {
        binding.apply {
            crimeTitleTextview.text = crime.title
            crimeDateTextview.text = DateFormat.getDateInstance(DateFormat.FULL).format(crime.date)

            root.setOnClickListener{
                onCrimeItemClick(crime.id)
            }
        }
    }
}