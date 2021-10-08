package com.timothygoon.note_taking_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.util.Base64;
import java.io.*
import java.util.*


private const val TAG: String = "NoteListFragment";
class NoteListFragment: Fragment() {

    interface Callbacks {
        fun onNoteSelected(noteId: UUID)
    }
    private var callbacks: Callbacks? = null

    private lateinit var noteRecyclerView : RecyclerView
    private lateinit var addNoteButton : Button
    private lateinit var saveToServerButton: Button
    private lateinit var loadFromServerButton: Button

    private var adapter: NoteAdapter? = NoteAdapter(emptyList())

    private val noteListViewModel: NoteListViewModel by lazy {
        ViewModelProvider(this).get(NoteListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "Total crimes: ${noteListViewModel.notes.size}")
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes_list, container, false)
        addNoteButton = view.findViewById(R.id.addNoteBtn)
        saveToServerButton = view.findViewById(R.id.saveToServerBtn)
        loadFromServerButton = view.findViewById(R.id.loadFromServerBtn)
        noteRecyclerView =
            view.findViewById(R.id.notesRecyclerview) as RecyclerView
        noteRecyclerView.layoutManager = LinearLayoutManager(context)
        noteRecyclerView.adapter = adapter

//        updateUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        noteListViewModel.noteListLiveData.observe(
            viewLifecycleOwner,
            Observer { notes ->
                val baos = ByteArrayOutputStream()

                for(note in notes) {
                    val imageFile = noteListViewModel.getPhotoFile(note)
                    var bitmap : Bitmap? = null

                    if (imageFile.exists() == true) {
                        bitmap = BitmapFactory.decodeFile(imageFile.path)
                    }

                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val imageBytes: ByteArray = baos.toByteArray()
                        val imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                        note.image= imageString
                    }
                }

                noteListViewModel.notes = notes

                notes?.let{
                    Log.i(TAG, "Got notes ${notes.size}")
                    updateUI(notes)
                }
            }
        )

        addNoteButton.setOnClickListener { view: View ->
            val newNote = Note()
            newNote.title = "New Note"
            newNote.noteBody = "..."
            noteListViewModel.addNote(newNote)
        }

        saveToServerButton.setOnClickListener { view: View ->
            var userTokenData : UserTokenData? = null
            try{
                val fis = requireActivity().openFileInput("loginToken")
                val ois = ObjectInputStream(fis)

                userTokenData = ois.readObject() as UserTokenData

                ois.close()
                fis.close()
            } catch(err: Throwable){
                Log.e(TAG, err.toString())
            }

            val mongoDBLiveData: LiveData<String> = MongoDBFetchr().saveToServer(userTokenData!!, noteListViewModel.notes )
            mongoDBLiveData.observe(
                viewLifecycleOwner,
                Observer { responseString ->

                    Log.d(TAG, "Received response is "+responseString)

                    Toast.makeText(requireContext(), "Data is saved to the server!", Toast.LENGTH_LONG).show()

                })
        }

        loadFromServerButton.setOnClickListener { view: View ->
            var userTokenData : UserTokenData? = null
            try{
                val fis = requireActivity().openFileInput("loginToken")
                val ois = ObjectInputStream(fis)

                userTokenData = ois.readObject() as UserTokenData

                ois.close()
                fis.close()
            } catch(err: Throwable){
                Log.e(TAG, err.toString())
            }

            val mongoDBLiveData: LiveData<List<LoadNote>> = MongoDBFetchr().loadFromServer(userTokenData!!.token)

            mongoDBLiveData.observe(
                viewLifecycleOwner,
                Observer { response ->

                    Log.d(TAG, "Received response is "+response.toString())

                    noteListViewModel.deleteNotes()

                    for( note in response){
                        val newNote = Note(UUID.fromString(note.appId), note.title, note.body, note.location)

                        // Decode image and save to jpeg file
                        if(note.image != null){
                            val imageBytes : ByteArray = Base64.decode(note.image, Base64.DEFAULT)
                            val decodedImage : Bitmap =
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            try{
                                val fos = requireActivity().openFileOutput(
                                    "NOTE_IMG_${note.appId}.jpg",
                                    Context.MODE_PRIVATE
                                )
                                decodedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                                fos.close()

                            } catch(err: Throwable){
                                Log.e(TAG, err.toString())
                            }
                        }

                        noteListViewModel.addNote(newNote)
                    }

                    Toast.makeText(requireContext(), "Data is loaded from the server!", Toast.LENGTH_LONG).show()
                })


        }
    }

    private inner class NoteHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var note: Note

        val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
        val previewTextView: TextView = itemView.findViewById(R.id.notePreviewTextView)

        init{
            itemView.setOnClickListener(this)
        }

        fun bind(note: Note){
            this.note = note
            titleTextView.text = this.note.title
            previewTextView.text = this.note.noteBody
        }

        override fun onClick(v: View?) {
            callbacks?.onNoteSelected(note.id)
        }
    }


    private inner class NoteAdapter(var notes: List<Note>)
        : RecyclerView.Adapter<NoteHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : NoteHolder {
            val view = layoutInflater.inflate(R.layout.list_item_note, parent, false)
            return NoteHolder(view)
        }
        override fun getItemCount() = notes.size

        override fun onBindViewHolder(holder: NoteHolder, position: Int) {
            val note = notes[position]
            holder.bind(note)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    private fun updateUI(notes: List<Note>) {
//        val notes = noteListViewModel.notes
        adapter = NoteAdapter(notes)
        noteRecyclerView.adapter = adapter
    }


    companion object {
        fun newInstance(): NoteListFragment {
            return NoteListFragment()
        }
    }

}