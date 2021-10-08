package com.timothygoon.note_taking_app

import android.content.Intent
import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Test
import java.io.File

class NoteDetailFragmentTest {
    // ASSUMPTIONS: Login works, adding a new note works
    // location permissions have been given

    private val mainActivity: ActivityTestRule<MainActivity> =
        ActivityTestRule(MainActivity::class.java)
    private var a: MainActivity? = null

    @Before
    fun setUp() {
        // Delete login file
        a = mainActivity.launchActivity(Intent())

        val dir: File? = a?.getFilesDir()
        val file = File(dir, "loginToken")
        val deleted = file.delete()
        Log.d("test", deleted.toString())

        NoteRepository.initialize(a!!)
        NoteRepository.get().deleteNotes()

        a?.finish()

        // Login with the test user
        a = mainActivity.launchActivity(Intent())

        // login
        Espresso.onView(ViewMatchers.withId(R.id.usernameEditText))
            .perform(ViewActions.typeText("testUser"))
        Espresso.pressBack()
        Espresso.onView(ViewMatchers.withId(R.id.passwordEditText))
            .perform(ViewActions.typeText("testUserPassword"))
        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click())

        Thread.sleep(1000)

        mainActivity.finishActivity()
    }

    @Test
    fun testAddNote() {
        a = mainActivity.launchActivity(Intent())

        // Add a note
        Espresso.onView(ViewMatchers.withId(R.id.addNoteBtn)).perform(ViewActions.click())

        Thread.sleep(500)
        // Check button text that is only in the list of tasks fragment
        Espresso.onView(ViewMatchers.withText("New Note")).check(
            ViewAssertions.matches(
                ViewMatchers.withText("New Note")
            )
        )

        // Enter note detail fragment
        Espresso.onView(ViewMatchers.withText("New Note")).perform(ViewActions.click())
        Thread.sleep(500)

        Espresso.onView(ViewMatchers.withId(R.id.noteTitleEditText))
            .perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.noteTitleEditText))
            .perform(ViewActions.typeText("editedTitle"))
        Espresso.pressBack()

        // Check that location button works
        Espresso.onView(ViewMatchers.withId(R.id.addLocationButton)).perform(ViewActions.click())
        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withText("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA")).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())

        // Go back to notes list fragment
        Espresso.pressBack()

        // Check that title was changed
        Espresso.onView(ViewMatchers.withText("editedTitle")).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        mainActivity.finishActivity()
    }
}