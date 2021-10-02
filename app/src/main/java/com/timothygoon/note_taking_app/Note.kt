package com.timothygoon.note_taking_app

import java.util.*

data class Note(var id: UUID = UUID.randomUUID(),
                var title: String = "",
                var noteBody: String = "",
                var location: String = ""
){
    val notePhoto
        get() = "NOTE_IMG_$id.jpg"
}
