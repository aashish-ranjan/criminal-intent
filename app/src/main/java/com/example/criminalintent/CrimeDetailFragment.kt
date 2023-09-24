package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import com.example.criminalintent.model.Crime
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.crimeTitleEdittext.text.isNullOrBlank()) {
                    Snackbar.make(binding.root, R.string.empty_title_warning, Snackbar.LENGTH_SHORT).show()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setFragmentResultListener(DatePickerFragment.REQ_KEY_PICK_DATE) { _, bundle ->
            val selectedDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_SELECTED_DATE) as Date
            crimeDetailViewModel.updateCrime { oldCrime ->
                oldCrime.copy(date = selectedDate)
            }
        }
        setFragmentResultListener(TimePickerFragment.REQ_KEY_PICK_TIME) { _, bundle ->
            val hourOfDay = bundle.getInt(TimePickerFragment.BUNDLE_KEY_HOUR)
            val minute = bundle.getInt(TimePickerFragment.BUNDLE_KEY_MINUTE)
            crimeDetailViewModel.updateCrime { oldCrime ->
                val newDate = crimeDetailViewModel.getNewDate(oldCrime.date, hourOfDay, minute)
                oldCrime.copy(date = newDate)

            }
        }
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

            crimeDatePickerButton.setOnClickListener {
                val crimeDate = crimeDetailViewModel.crimeStateFlow.value?.date ?: Date()
                val action = CrimeDetailFragmentDirections.actionCrimeDetailFragmentToDatePickerFragment(crimeDate)
                findNavController().navigate(action)
            }

            crimeTimePickerButton.setOnClickListener {
                val crimeDate = crimeDetailViewModel.crimeStateFlow.value?.date ?: Date()
                val action = CrimeDetailFragmentDirections.actionCrimeDetailFragmentToTimePickerFragment(crimeDate)
                findNavController().navigate(action)
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
            crimeDatePickerButton.text = crime.date.toString()
            crimeSolvedCheckbox.isChecked = crime.isSolved
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}