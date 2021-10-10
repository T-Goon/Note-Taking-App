package com.group50.note_taking_app

import android.content.Intent
import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
@LargeTest
class ListFragmentTest {
//    ASSUMPTIONS: Login works properly

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
        Espresso.onView(ViewMatchers.withId(R.id.usernameEditText)).perform(ViewActions.typeText("testUser"))
        Espresso.pressBack()
        Espresso.onView(ViewMatchers.withId(R.id.passwordEditText)).perform(ViewActions.typeText("testUserPassword"))
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
        Espresso.onView(withText("New Note")).check(
            matches(
                withText("New Note")
            )
        )

        mainActivity.finishActivity()
    }


    @Test
    fun saveAndLoadToServer() {
        a = mainActivity.launchActivity(Intent())

        // Add a note
        onView(ViewMatchers.withId(R.id.loadFromServerBtn)).perform(ViewActions.click())

        Thread.sleep(1000)
        // Check button text that is only in the list of tasks fragment
        onView(withText("testServerNote1")).check(
            matches(
                withText("testServerNote1")
            )
        )

        onView(ViewMatchers.withId(R.id.saveToServerBtn)).perform(ViewActions.click())

        onView(withText("Data is saved to the server!")).inRoot(
            withDecorView(
                not(
                    `is`(
                        a!!.getWindow().getDecorView()
                    )
                )
            )
        ).check(
            matches(
                isDisplayed()
            )
        )

        mainActivity.finishActivity()
    }
}