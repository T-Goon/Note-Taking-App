package com.timothygoon.note_taking_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import java.io.File
import java.util.*

private const val TAG = "NoteDetailFragment"
private const val ARG_NOTE_ID = "note_id"
private const val REQUEST_PHOTO = 1

class NoteDetailFragment: Fragment(), LocationListener {

    private lateinit var note: Note

    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText

    private lateinit var imageView: ImageView
    private lateinit var GPSImageButton: ImageButton
    private lateinit var cameraImageButton: ImageButton
    private lateinit var locationTextView: TextView

    private val locationPermissionCode = 2
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var locality: String = ""

    private var locationManager : LocationManager? = null

    var photoFile: File? = null
    var photoUri: Uri? = null

    private val noteDetailViewModel: NoteDetailViewModel by lazy {
        ViewModelProvider(this).get(NoteDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)
        note = Note()
        val noteId: UUID = arguments?.getSerializable(ARG_NOTE_ID) as UUID
        noteDetailViewModel.loadNote(noteId)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult() called")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode) {

            // Checking whether user granted the permission or not.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
                }
            }
        }

        Log.d(TAG, "permission granted")
        Log.d(TAG, permissions.toString())
        Log.d(TAG, grantResults.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_detail, container, false)

        titleEditText = view.findViewById(R.id.noteTitleEditText) as EditText
        bodyEditText = view.findViewById(R.id.noteBodyEditText) as EditText
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
                    noteDetailViewModel.photoFile = noteDetailViewModel.getPhotoFile(note)
                    noteDetailViewModel.photoUri = FileProvider.getUriForFile(requireActivity(),
                        "com.timothygoon.note_taking_app.fileprovider",
                        noteDetailViewModel.photoFile!!
                    )
                    photoFile = noteDetailViewModel.photoFile
                    photoUri = noteDetailViewModel.photoUri

                    updateUI()
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
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

        cameraImageButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }

        GPSImageButton.setOnClickListener { view: View ->

            noteDetailViewModel.isLocationBtnPressed = true

            if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                Log.d(TAG, "Requesting permission to use location services")
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            }
            else{
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            }

            noteDetailViewModel.noteLocation = locality
            note.location = noteDetailViewModel.noteLocation
            locationTextView.setText(noteDetailViewModel.noteLocation)
        }
    }

    override fun onPause() {
        super.onPause()

        locationManager?.removeUpdates(this);
    }

    override fun onStop() {
        super.onStop()
        noteDetailViewModel.saveNote(note)
    }

    private fun updateUI() {
        titleEditText.setText(note.title)
        bodyEditText.setText(note.noteBody)
        locationTextView.setText(note.location)

        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (noteDetailViewModel.photoFile?.exists() == true) {
            val bitmap = PictureUtils.getScaledBitmap(photoFile!!.path, requireActivity())
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageDrawable(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult()")
        Log.d(TAG, noteDetailViewModel.photoUri.toString())

        when {
            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView()
            }
        }
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

        // check if the location button is pressed before
        if(noteDetailViewModel.isLocationBtnPressed){
            noteDetailViewModel.noteLocation = locality
            note.location = noteDetailViewModel.noteLocation
            locationTextView.setText(noteDetailViewModel.noteLocation)

            noteDetailViewModel.isLocationBtnPressed = false
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