package com.example.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.criminalintent.databinding.FragmentCrimeListBinding

class CrimeListFragment: Fragment() {
    companion object {
        private const val TAG = "CrimeListFragment"
    }

    private var _binding: FragmentCrimeListBinding? = null
    private val crimeListViewModel: CrimeListViewModel by viewModels()
    private val binding
        get() = checkNotNull(_binding) {
            "Binding is null"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: crimeList size is ${crimeListViewModel.crimeList.size}")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}