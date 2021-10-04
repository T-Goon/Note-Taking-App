package com.timothygoon.note_taking_app

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(@PrimaryKey var id: UUID = UUID.randomUUID(),
                var title: String = "",
                var noteBody: String = "",
                var location: String = ""
){
    val notePhoto
        get() = "NOTE_IMG_$id.jpg"
}
