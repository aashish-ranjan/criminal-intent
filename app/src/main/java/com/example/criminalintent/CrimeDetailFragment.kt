package com.example.criminalintent

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.criminalintent.utils.DateTimeUtils
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

    private val selectSuspectFromContacts = registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        uri?.let {
            extractNameFromUri(uri)
        }
    }

    private fun extractNameFromUri(contactUri: Uri) {
        val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queryCursor = requireActivity().contentResolver.query(
            contactUri,
            projection,
            null,
            null,
            null
        )
        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val suspectName = cursor.getString(nameColumnIndex)
                crimeDetailViewModel.updateCrime {oldCrime ->
                    oldCrime.copy(suspect = suspectName)
                }
            }
        }
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
        setHasOptionsMenu(true)
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
                crimeDetailViewModel.crimeStateFlow.collectLatest {
                    updateUi(it)
                }
            }
        }
        attachListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_detail, menu)
        menu.findItem(R.id.menu_item_delete_crime).isVisible = !crimeDetailViewModel.addNewCrime
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_item_delete_crime -> {
                deleteCrime()
                true
            }
            else ->  super.onOptionsItemSelected(item)
        }
    }

    private fun deleteCrime() {
        crimeDetailViewModel.deleteCrime()
        requireActivity().onBackPressed()
    }

    private fun attachListeners() {
        with(binding) {
            crimeTitleEdittext.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeDatePickerButton.setOnClickListener {
                val crimeDate = crimeDetailViewModel.crimeStateFlow.value.date
                val action = CrimeDetailFragmentDirections.actionCrimeDetailFragmentToDatePickerFragment(crimeDate)
                findNavController().navigate(action)
            }

            crimeTimePickerButton.setOnClickListener {
                val crimeDate = crimeDetailViewModel.crimeStateFlow.value.date
                val action = CrimeDetailFragmentDirections.actionCrimeDetailFragmentToTimePickerFragment(crimeDate)
                findNavController().navigate(action)
            }

            crimeSolvedCheckbox.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }

            val selectSuspectIntent = selectSuspectFromContacts.contract.createIntent(
                requireContext(), null
            )
            if (canResolve(selectSuspectIntent)) {
                selectSuspectButton.setOnClickListener {
                    selectSuspectFromContacts.launch(null)
                }
            } else {
                selectSuspectButton.isEnabled = false
            }

            shareCrimeReportButton.setOnClickListener {
                if (crimeTitleEdittext.text.toString().isEmpty()) {
                    Snackbar.make(binding.root, R.string.empty_title_warning, Snackbar.LENGTH_SHORT).show()
                } else {
                    launchShareSheet()
                }
            }
        }
    }

    private fun launchShareSheet() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getCrimeReport(crimeDetailViewModel.crimeStateFlow.value))
            putExtra(Intent.EXTRA_TITLE, getString(R.string.crime_report_title))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun updateUi(crime: Crime) {
        with(binding) {
            if (crimeTitleEdittext.text.toString() != crime.title) {
                crimeTitleEdittext.setText(crime.title)
            }
            crimeDatePickerButton.text = crime.date.toString()
            crimeSolvedCheckbox.isChecked = crime.isSolved
            selectSuspectButton.text = crime.suspect.ifEmpty { getString(R.string.select_suspect) }
        }
    }
    private fun getCrimeReport(crime: Crime): String {
        val solvedString = getString(
            if (crime.isSolved) R.string.solved_case_subtext
            else R.string.unsolved_case_subtext
        )
        val dateString = DateTimeUtils.getReadableDateAndTime(crime.date)

        val suspectString = if (crime.suspect.isBlank()) getString(R.string.unknown_suspect_subtext)
                            else getString(R.string.known_suspect_subtext, crime.suspect)

        return getString(R.string.crime_report_text, crime.title, dateString, solvedString, suspectString)
    }

    private fun canResolve(intent: Intent): Boolean {
        val packageManager = requireActivity().packageManager
        val resolvedActivity = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolvedActivity != null
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}