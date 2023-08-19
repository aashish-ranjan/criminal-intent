package com.example.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.criminalintent.databinding.ListItemNormalCrimeBinding
import com.example.criminalintent.databinding.ListItemSeriousCrimeBinding
import com.example.criminalintent.model.Crime

class CrimeListAdapter(private val crimeList: List<Crime>): Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            val binding = ListItemNormalCrimeBinding.inflate(inflater, parent, false)
            NormalCrimeHolder(binding)
        } else {
            val binding = ListItemSeriousCrimeBinding.inflate(inflater, parent, false)
            SeriousCrimeViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = crimeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crime = crimeList[position]
        if (holder is NormalCrimeHolder) holder.bind(crime)
        else if (holder is SeriousCrimeViewHolder) holder.bind(crime)
    }

    override fun getItemViewType(position: Int): Int {
        val crime = crimeList[position]
        return if (crime.isSerious) 1 else 0
    }

}