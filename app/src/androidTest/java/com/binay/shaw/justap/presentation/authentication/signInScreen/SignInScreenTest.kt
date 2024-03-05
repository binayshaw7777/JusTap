package com.binay.shaw.justap.presentation.authentication.signInScreen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.binay.shaw.justap.R

import org.junit.Rule
import org.junit.Test


class SignInScreenTest {

    fun ViewInteraction.isGone() = getViewAssertion(Visibility.GONE)

    fun ViewInteraction.isVisible() = getViewAssertion(Visibility.VISIBLE)

    fun ViewInteraction.isInvisible() = getViewAssertion(Visibility.INVISIBLE)

    private fun getViewAssertion(visibility: Visibility): ViewAssertion? {
        return ViewAssertions.matches(withEffectiveVisibility(visibility))
    }

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(SignInScreen::class.java)

    @Test
    fun email_is_empty () {
        onView(withId(R.id.etEmail)).perform(typeText(""))
        onView(withId(R.id.etPassword)).perform(typeText("Test@1234"))
        onView(withId(R.id.btnLogIn)).perform(click())
        onView(withId(R.id.emailHelperTV)).isVisible()
        onView(withId(R.id.emailHelperTV)).check(matches(withText("Enter your email")))
    }

    @Test
    fun password_is_empty () {
        onView(withId(R.id.etEmail)).perform(typeText("binayshaw7777@gmail.com"))
        onView(withId(R.id.etPassword)).perform(typeText(""))
        onView(withId(R.id.btnLogIn)).perform(click())
        onView(withId(R.id.passwordHelperTV)).isVisible()
        onView(withId(R.id.passwordHelperTV)).check(matches(withText("Password is empty")))
    }

    @Test
    fun email_is_invalid () {
        onView(withId(R.id.etEmail)).perform(typeText("binayshaw7777"))
        onView(withId(R.id.etPassword)).perform(typeText("Test@1234"))
        onView(withId(R.id.btnLogIn)).perform(click())
        onView(withId(R.id.emailHelperTV)).isVisible()
        onView(withId(R.id.emailHelperTV)).check(matches(withText("Email is not valid")))
    }

    @Test
    fun password_is_invalid () {
        onView(withId(R.id.etEmail)).perform(typeText("binayshaw7777@gmail.com"))
        onView(withId(R.id.etPassword)).perform(typeText("test12345"))
        onView(withId(R.id.btnLogIn)).perform(click())
        onView(withId(R.id.passwordHelperTV)).isVisible()
        onView(withId(R.id.passwordHelperTV)).check(matches(withText("Password must contains uppercase, lowercase, digit and symbol")))
    }

    @Test
    fun password_length_is_less_than_eight () {
        onView(withId(R.id.etEmail)).perform(typeText("binayshaw7777@gmail.com"))
        onView(withId(R.id.etPassword)).perform(typeText("Test@1"))
        onView(withId(R.id.btnLogIn)).perform(click())
        onView(withId(R.id.passwordHelperTV)).isVisible()
        onView(withId(R.id.passwordHelperTV)).check(matches(withText("Password length less than 8 letters")))
    }

    @Test
    fun sign_in_is_successful () {
        onView(withId(R.id.etEmail)).perform(typeText("binayshaw7777@gmail.com"))
        onView(withId(R.id.etPassword)).perform(typeText("Test@1234"))
        onView(withId(R.id.btnLogIn)).perform(click())
    }

}