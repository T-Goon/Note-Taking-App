package com.timothygoon.note_taking_app

import androidx.lifecycle.ViewModel

class NoteListViewModel: ViewModel() {

    val notes = mutableListOf<Note>()

    init{
        for(i in 0 until 10){
            val note = Note()
            note.title = "Note #$i"
            note.noteBody = "lorem ipsum"
            notes += note
        }
    }

}