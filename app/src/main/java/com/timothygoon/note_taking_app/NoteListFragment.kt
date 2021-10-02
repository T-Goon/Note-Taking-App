package com.timothygoon.note_taking_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG: String = "NoteListFragment";
class NoteListFragment: Fragment() {

    private lateinit var noteRecyclerView : RecyclerView
    private var adapter: NoteAdapter? = null

    private val noteListViewModel: NoteListViewModel by lazy {
        ViewModelProvider(this).get(NoteListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${noteListViewModel.notes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes_list, container, false)
        noteRecyclerView =
            view.findViewById(R.id.notesRecyclerview) as RecyclerView
        noteRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return view
    }

    private inner class NoteHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        private lateinit var note: Note

        val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
        val previewTextView: TextView = itemView.findViewById(R.id.notePreviewTextView)

        // TODO: Adding setOnClickListener and onClick functions

        fun bind(note: Note){
            this.note = note
            titleTextView.text = this.note.title
            previewTextView.text = this.note.noteBody
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

    private fun updateUI() {
        val notes = noteListViewModel.notes
        adapter = NoteAdapter(notes)
        noteRecyclerView.adapter = adapter
    }


    companion object {
        fun newInstance(): NoteListFragment {
            return NoteListFragment()
        }
    }

}