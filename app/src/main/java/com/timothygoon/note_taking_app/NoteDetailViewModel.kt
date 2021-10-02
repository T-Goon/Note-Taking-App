package com.timothygoon.note_taking_app

import androidx.lifecycle.ViewModel
import java.util.*

class NoteDetailViewModel: ViewModel() {

    var id: UUID? = null
    var noteTitle = ""
    var noteBody = ""
    var noteLocation = ""

}