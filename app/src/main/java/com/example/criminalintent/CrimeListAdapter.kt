package com.example.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.criminalintent.databinding.ListItemCrimeBinding
import com.example.criminalintent.model.Crime

class CrimeListAdapter(private val crimeList: List<Crime>): Adapter<CrimeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun getItemCount(): Int = crimeList.size

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimeList[position]
        holder.binding.apply {
            crimeTitleTextview.text = crime.title
            crimeDateTextview.text = crime.date.toString()
        }
    }

}