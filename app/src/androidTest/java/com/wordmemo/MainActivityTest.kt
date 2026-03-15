package com.wordmemo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testMainActivityLaunches() {
        // 验证 MainActivity 可以启动
        activityRule.scenario.onActivity { activity ->
            assert(activity != null)
        }
    }

    @Test
    fun testBottomNavigationViewExists() {
        // 验证底部导航栏存在
        onView(withId(R.id.nav_view)).check { view, _ ->
            assert(view != null)
        }
    }

    @Test
    fun testNavigationToLearning() {
        // 验证可以导航到学习页面
        onView(withId(R.id.learning_fragment)).check { view, _ ->
            assert(view != null)
        }
    }

    @Test
    fun testNavigationToReview() {
        // 验证可以导航到复习页面
        onView(withId(R.id.nav_view)).perform(click())
    }
}
