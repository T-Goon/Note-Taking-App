package com.timothygoon.note_taking_app.database

import androidx.room.TypeConverter
import java.util.*

class NoteTypeConverters {

//    @TypeConverter
//    fun fromDate(date: Date?): Long? {
//        return date?.time
//    }
//    @TypeConverter
//    fun toDate(millisSinceEpoch: Long?): Date? {
//        return millisSinceEpoch?.let {
//            Date(it)
//        }
//    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

}