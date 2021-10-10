package com.group50.note_taking_app

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class Note(@PrimaryKey @SerializedName("appId") var id: UUID = UUID.randomUUID(),
                var title: String = "",
                @SerializedName("body") var noteBody: String = "",
                var location: String = "", var image: String = ""
){
    val notePhoto
        get() = "NOTE_IMG_$id.jpg"
}
