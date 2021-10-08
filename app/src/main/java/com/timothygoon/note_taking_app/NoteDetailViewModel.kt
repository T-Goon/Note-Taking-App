package com.timothygoon.note_taking_app

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class NoteDetailViewModel: ViewModel() {

    var id: UUID? = null
    var noteTitle = ""
    var noteBody = ""
    var noteLocation = ""
    var photoFile: File? = null
    var photoUri: Uri? = null

    var isLocationBtnPressed = false

    // TODO: Add a property for capturing image information

    private val noteRepository = NoteRepository.get()
    private val noteIdLiveData = MutableLiveData<UUID>()
    var noteLiveData: LiveData<Note?> =
        Transformations.switchMap(noteIdLiveData) { noteId ->
            noteRepository.getNote(noteId)
        }
    fun loadNote(noteId: UUID) {
        noteIdLiveData.value = noteId
    }

    fun saveNote(note: Note){
        noteRepository.updateNote(note)
    }

    fun getPhotoFile(note: Note): File {
        return noteRepository.getPhotoFile(note)
    }

}