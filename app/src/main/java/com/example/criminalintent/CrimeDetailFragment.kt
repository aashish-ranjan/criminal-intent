package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class CrimeDetailFragment: Fragment() {

    private var _binding: FragmentCrimeDetailBinding? = null
    private val args: CrimeDetailFragmentArgs by navArgs()
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }
    private val binding
        get() = checkNotNull(_binding) {
            "Binding is null"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crimeStateFlow.collectLatest {crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }
        attachListeners()
    }

    private fun attachListeners() {
        with(binding) {
            crimeTitleEdittext.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeDateButton.apply {
                isEnabled = false
            }

            crimeSolvedCheckbox.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }
        }
    }

    private fun updateUi(crime: Crime) {
        with(binding) {
            if (crimeTitleEdittext.text.toString() != crime.title) {
                crimeTitleEdittext.setText(crime.title)
            }
            crimeDateButton.text = crime.date.toString()
            crimeSolvedCheckbox.isChecked = crime.isSolved
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}