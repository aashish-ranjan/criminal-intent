package com.example.criminalintent

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
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
import com.example.criminalintent.utils.PictureUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
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

    private val capturePhotoContracts = registerForActivityResult(ActivityResultContracts.TakePicture()) { capturedSuccessfully ->
        if (capturedSuccessfully && crimeDetailViewModel.photoFileName != null) {
            crimeDetailViewModel.updateCrime {  oldCrime ->
                oldCrime.copy(photoFileName = crimeDetailViewModel.photoFileName)
            }
            announceToTalkBack(getString(R.string.crime_image_successful_update_feedback))
        } else {
            announceToTalkBack(getString(R.string.crime_image_failed_update_feedback))
            Snackbar.make(binding.root, R.string.failed_to_save_photo, Snackbar.LENGTH_SHORT).show()
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

            captureImageButton.setOnClickListener {
                if (canResolve(Intent(MediaStore.ACTION_IMAGE_CAPTURE))) {
                    launchCamera()
                } else {
                    Snackbar.make(binding.root, R.string.no_camera_app_available, Snackbar.LENGTH_SHORT).show()
                }
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

    private fun launchCamera() {
        crimeDetailViewModel.photoFileName = "IMG-${Date()}.JPG"
        val photoFile = File(requireContext().filesDir, crimeDetailViewModel.photoFileName)
        val photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", photoFile)
        capturePhotoContracts.launch(photoUri)
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
            updateCrimeImage(crime.photoFileName)
            if (crimeTitleEdittext.text.toString() != crime.title) {
                crimeTitleEdittext.setText(crime.title)
            }
            crimeDatePickerButton.text = DateTimeUtils.getReadableDateAndTime(crime.date)
            crimeSolvedCheckbox.isChecked = crime.isSolved
            selectSuspectButton.text = crime.suspect.ifEmpty { getString(R.string.select_suspect) }
        }
    }

    private fun updateCrimeImage(photoFileName: String?) {
        with(binding.crimeImage) {
            if (tag != photoFileName) {
                val photoFile = photoFileName?.let { File(requireContext().filesDir, it) }
                if (photoFile?.exists() == true) {
                    doOnLayout { measuredView ->
                        val scaledBitMap = PictureUtils.getScaledBitmap(photoFile.path, measuredView.width, measuredView.height)
                        setImageBitmap(scaledBitMap)
                        tag = photoFile
                        contentDescription = getString(R.string.crime_image_set)
                    }
                } else {
                    setImageBitmap(null)
                    tag = null
                    contentDescription = getString(R.string.crime_image_unset)
                }
            }
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

    private fun announceToTalkBack(message: String) {
        val accessibilityManager = requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            val event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                AccessibilityEvent(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            } else {
                AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            }
            event.text.add(message)
            accessibilityManager.sendAccessibilityEvent(event)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}