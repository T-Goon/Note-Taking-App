package com.timothygoon.note_taking_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                notes?.let{
                    Log.i(TAG, "Got notes ${notes.size}")
                    updateUI(notes)
                }
            }
        )

        // TODO: Set up listener for Add button
        addNoteButton.setOnClickListener { view: View ->
            val newNote = Note()
            newNote.title = "New Note"
            newNote.noteBody = "..."
            noteListViewModel.addNote(newNote)
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