package com.timothygoon.note_taking_app

import androidx.lifecycle.ViewModel
import java.io.File
import java.util.ArrayList

class NoteListViewModel: ViewModel() {

//    val notes = mutableListOf<Note>()

//    init{
//        for(i in 0 until 10){
//            val note = Note()
//            note.title = "Note #$i"
//            note.noteBody = "lorem ipsum"
//            notes += note
//        }
//    }

    private val noteRepository = NoteRepository.get()

    fun addNote(note: Note){
        noteRepository.addNote(note)
    }

    fun deleteNotes(){
        noteRepository.deleteNotes()
    }

    val noteListLiveData = noteRepository.getNotes()

    fun getPhotoFile(note: Note) : File {
        return noteRepository.getPhotoFile(note)
    }

    var notes : List<Note> = ArrayList<Note>()

}