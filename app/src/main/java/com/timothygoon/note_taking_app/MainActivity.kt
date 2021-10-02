package com.timothygoon.note_taking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

private const val TAG: String = "MainActivity";

class MainActivity : AppCompatActivity(), LoginFragment.Callbacks {

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
            .addToBackStack(null)
            .commit()
    }
}