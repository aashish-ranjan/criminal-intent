package com.example.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.criminalintent.databinding.ListItemNormalCrimeBinding
import com.example.criminalintent.model.Crime

class CrimeListAdapter: ListAdapter<Crime, NormalCrimeHolder>(CrimeItemComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalCrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemNormalCrimeBinding.inflate(inflater, parent, false)
        return NormalCrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: NormalCrimeHolder, position: Int) {
        val crime = getItem(position)
        holder.bind(crime)
    }
}