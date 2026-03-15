package com.wordmemo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivityLaunches() {
        activityRule.scenario.onActivity { activity ->
            assert(activity != null)
        }
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun bottomNavigationViewIsDisplayed() {
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun learningFragmentIsStartDestination() {
        // 默认显示学习页面，应看到 "Learning Page"
        onView(withText("Learning Page")).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun navHostFragmentIsDisplayed() {
        onView(withId(R.id.nav_host_fragment)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun canNavigateToStatisticsTab() {
        onView(withText("Statistics")).perform(click())
        onView(withText("学习统计")).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun canNavigateToSettingsTab() {
        onView(withText("Settings")).perform(click())
        onView(withText("Settings Page")).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun canNavigateToReviewTab() {
        onView(withText("Review")).perform(click())
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun canNavigateBackToLearningTab() {
        onView(withText("Statistics")).perform(click())
        onView(withText("Learning")).perform(click())
        onView(withText("Learning Page")).check(matches(isDisplayed()))
    }
}
