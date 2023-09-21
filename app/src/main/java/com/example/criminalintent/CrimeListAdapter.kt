package com.example.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.criminalintent.databinding.ListItemNormalCrimeBinding
import com.example.criminalintent.model.Crime

class CrimeListAdapter(private val crimeList: List<Crime>): Adapter<NormalCrimeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalCrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemNormalCrimeBinding.inflate(inflater, parent, false)
        return NormalCrimeHolder(binding)
    }

    override fun getItemCount(): Int = crimeList.size

    override fun onBindViewHolder(holder: NormalCrimeHolder, position: Int) {
        val crime = crimeList[position]
        holder.bind(crime)
    }

}