package com.joey.takenotes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.joey.takenotes.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    var rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkViewDisplay() {
        onView(withText(R.string.create_note)).check(matches(isDisplayed()))
        onView(withId(R.id.fab)).check(matches(isDisplayed())).perform(click())
    }
}
