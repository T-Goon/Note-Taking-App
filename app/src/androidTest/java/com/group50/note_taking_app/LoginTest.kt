package com.group50.note_taking_app

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginTest {

    private val mainActivity: ActivityTestRule<MainActivity> =
        ActivityTestRule(MainActivity::class.java)
    private var a: Activity? = null

    @Before
    fun setUp() {
        a = mainActivity.launchActivity(Intent())

        val dir: File? = (a as MainActivity?)?.getFilesDir()
        val file = File(dir, "loginToken")
        val deleted = file.delete()
        Log.d("test", deleted.toString())

        (a as MainActivity?)?.finish()
    }

    @Test
    fun testLogin() {

        a = mainActivity.launchActivity(Intent())

        // Try to login
        Espresso.onView(withId(R.id.usernameEditText)).perform(ViewActions.typeText("testUser"))
        Espresso.pressBack()
        Espresso.onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText("testUserPassword"))
        Espresso.pressBack()

        Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click())

        Thread.sleep(1000)
        // Check button text that is only in the list of tasks fragment
        Espresso.onView(withId(R.id.addNoteBtn)).check(matches(withText("Add Note")))

        mainActivity.finishActivity()
    }

}