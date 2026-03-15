package com.wordmemo.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsFragmentTest {

    @Test
    fun statisticsFragmentLaunchesWithoutCrash() {
        val scenario = launchFragmentInContainer<StatisticsFragment>()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
        }
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun statisticsFragmentShowsStatsCards() {
        launchFragmentInContainer<StatisticsFragment>()
        onView(withText("学习统计")).check(matches(isDisplayed()))
        onView(withId(R.id.today_learning_count)).check(matches(isDisplayed()))
        onView(withId(R.id.today_review_count)).check(matches(isDisplayed()))
        onView(withId(R.id.consecutive_days)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun statisticsFragmentRefreshButtonExists() {
        launchFragmentInContainer<StatisticsFragment>()
        onView(withId(R.id.btn_refresh)).check(matches(isDisplayed()))
        onView(withText("刷新统计")).check(matches(isDisplayed()))
    }
}
