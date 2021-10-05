package com.timothygoon.note_taking_app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import java.util.*

private const val TAG = "NoteDetailFragment"
private const val ARG_NOTE_ID = "note_id"

class NoteDetailFragment: Fragment(), LocationListener {

    private lateinit var note: Note

    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var imageView: ImageView
    private lateinit var GPSImageButton: ImageButton
    private lateinit var cameraImageButton: ImageButton
    private lateinit var locationTextView: TextView

    private val locationPermissionCode = 2
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var locality: String = ""

    private val noteDetailViewModel: NoteDetailViewModel by lazy {
        ViewModelProvider(this).get(NoteDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = Note()
        val noteId: UUID = arguments?.getSerializable(ARG_NOTE_ID) as UUID
        noteDetailViewModel.loadNote(noteId)

        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_detail, container, false)

        titleEditText = view.findViewById(R.id.noteTitleEditText) as EditText
        bodyEditText = view.findViewById(R.id.noteBodyEditText) as EditText
        saveButton = view.findViewById(R.id.saveButton) as Button
        imageView = view.findViewById(R.id.noteImageView) as ImageView
        GPSImageButton = view.findViewById(R.id.addLocationButton) as ImageButton
        cameraImageButton = view.findViewById(R.id.addImageButton) as ImageButton
        locationTextView = view.findViewById(R.id.noteLocationTextView) as TextView

        titleEditText.setText(noteDetailViewModel.noteTitle)
        bodyEditText.setText(noteDetailViewModel.noteBody)
        locationTextView.setText(noteDetailViewModel.noteLocation)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteDetailViewModel.noteLiveData.observe(
            viewLifecycleOwner,
            Observer { note ->
                note?.let {
                    this.note = note

                    noteDetailViewModel.id = note.id
                    noteDetailViewModel.noteTitle = note.title
                    noteDetailViewModel.noteBody = note.noteBody
                    noteDetailViewModel.noteLocation = note.location

                    // TODO: Add the same sync functionality for image


                    updateUI()
                }
            })



        saveButton.setOnClickListener { view: View ->
            noteDetailViewModel.saveNote(note)
        }

        GPSImageButton.setOnClickListener { view: View ->
            noteDetailViewModel.noteLocation = locality
            note.location = noteDetailViewModel.noteLocation
            locationTextView.setText(noteDetailViewModel.noteLocation)
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                noteDetailViewModel.noteTitle = titleEditText.text.toString()
                note.title = titleEditText.text.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }
        titleEditText.addTextChangedListener(titleWatcher)

        val bodyWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                noteDetailViewModel.noteBody = bodyEditText.text.toString()
                note.noteBody = bodyEditText.text.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }
        bodyEditText.addTextChangedListener(bodyWatcher)


    }

    private fun updateUI() {
        titleEditText.setText(note.title)
        bodyEditText.setText(note.noteBody)
        locationTextView.setText(note.location)

        // TODO: Set up Image loading / saving
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged() called")
        lat = location.latitude
        lon = location.longitude

        val gcd = Geocoder(requireContext(), Locale.getDefault())
        val addresses:List<Address> = gcd.getFromLocation(lat, lon, 1)

        if (addresses.size > 0){
            locality = addresses.get(0).getAddressLine(0)
        }

//        Log.d(TAG, "locality: " + addresses.get(0).getAddressLine(0))

    }


    companion object{
        fun newInstance(noteId: UUID): NoteDetailFragment {
            val args = Bundle().apply {
                putSerializable(ARG_NOTE_ID, noteId)
            }
            return NoteDetailFragment().apply {
                arguments = args
            }
        }
    }

}