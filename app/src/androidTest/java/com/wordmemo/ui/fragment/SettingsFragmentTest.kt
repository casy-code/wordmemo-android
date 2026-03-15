package com.wordmemo.ui.fragment

import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    private fun launchSettingsFragment() = launchFragmentInContainer<SettingsFragment>(
        themeResId = R.style.Theme_WordMemo
    )

    private fun assertNotErrorView(fragment: SettingsFragment) {
        val root = fragment.requireView()
        if (root is TextView && root.text == "页面加载失败") {
            fail("布局加载失败，显示「页面加载失败」")
        }
    }

    @Test
    fun settingsFragmentLaunches() {
        val scenario = launchSettingsFragment()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            assertNotErrorView(fragment)
        }
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun settingsFragmentShowsContent() {
        launchSettingsFragment()
        onView(withText("设置")).check(matches(isDisplayed()))
        onView(withText("应用设置将在此显示")).check(matches(isDisplayed()))
    }
}
