package com.example.criminalintent

import androidx.recyclerview.widget.DiffUtil
import com.example.criminalintent.model.Crime

class CrimeItemComparator: DiffUtil.ItemCallback<Crime>() {
    override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem.id == newItem.id
    }
}