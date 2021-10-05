package com.timothygoon.note_taking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*

private const val TAG: String = "MainActivity";

class MainActivity : AppCompatActivity(), LoginFragment.Callbacks, NoteListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                    .add(R.id.container, LoginFragment.newInstance())
                    .commit()
        }
    }

    override fun onLogin() {
//        val fragment = NoteListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, NoteListFragment.newInstance())
            .commit()
    }

    override fun onNoteSelected(noteId: UUID) {
        val fragment = NoteDetailFragment.newInstance(noteId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}