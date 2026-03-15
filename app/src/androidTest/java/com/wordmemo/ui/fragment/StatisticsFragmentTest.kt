package com.wordmemo.ui.fragment

import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsFragmentTest {

    private fun launchStatisticsFragment() = launchFragmentInContainer<StatisticsFragment>(
        themeResId = R.style.Theme_WordMemo
    )

    private fun assertNotErrorView(fragment: StatisticsFragment) {
        val root = fragment.requireView()
        if (root is TextView && root.text == "页面加载失败") {
            fail("布局加载失败，显示「页面加载失败」")
        }
        val statsCard = root.findViewById<View>(R.id.today_learning_count)
        assertNotNull("today_learning_count 未找到，可能布局加载失败", statsCard)
    }

    @Test
    fun statisticsFragmentLaunchesWithoutCrash() {
        val scenario = launchStatisticsFragment()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            assertNotErrorView(fragment)
        }
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun statisticsFragmentShowsStatsCards() {
        launchStatisticsFragment()
        onView(withText("学习统计")).check(matches(isDisplayed()))
        onView(withId(R.id.today_learning_count)).check(matches(isDisplayed()))
        onView(withId(R.id.today_review_count)).check(matches(isDisplayed()))
        onView(withId(R.id.consecutive_days)).check(matches(isDisplayed()))
    }

    @Test
    @Ignore("Espresso InputManager 与 API 36 不兼容，请在 API 34 及以下模拟器运行")
    fun statisticsFragmentRefreshButtonExists() {
        launchStatisticsFragment()
        onView(withId(R.id.btn_refresh)).check(matches(isDisplayed()))
        onView(withText("刷新统计")).check(matches(isDisplayed()))
    }

    @Test
    fun statisticsFragmentTodayCardsAreClickable() {
        val scenario = launchStatisticsFragment()
        scenario.onFragment { fragment ->
            assertNotErrorView(fragment)
            val cardLearning = fragment.requireView().findViewById<View>(R.id.card_today_learning)
            val cardReview = fragment.requireView().findViewById<View>(R.id.card_today_review)
            assertNotNull("今日学习卡片应存在", cardLearning)
            assertNotNull("今日复习卡片应存在", cardReview)
            assert(cardLearning.isClickable)
            assert(cardReview.isClickable)
        }
    }
}
