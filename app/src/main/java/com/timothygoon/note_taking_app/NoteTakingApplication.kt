package com.timothygoon.note_taking_app

import android.app.Application

class NoteTakingApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        NoteRepository.initialize(this)
    }
}