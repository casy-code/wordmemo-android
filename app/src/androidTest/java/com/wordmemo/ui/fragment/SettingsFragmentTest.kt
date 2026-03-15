package com.wordmemo.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    @Test
    fun settingsFragmentLaunches() {
        val scenario = launchFragmentInContainer<SettingsFragment>()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
        }
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun settingsFragmentShowsContent() {
        launchFragmentInContainer<SettingsFragment>()
        onView(withText("Settings Page")).check(matches(isDisplayed()))
        onView(withText("应用设置将在此显示")).check(matches(isDisplayed()))
    }
}
