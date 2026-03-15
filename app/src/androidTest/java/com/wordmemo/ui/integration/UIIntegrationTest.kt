package com.wordmemo.ui.integration

import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import com.wordmemo.ui.fragment.LearningFragment
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI 集成测试
 * 测试 Fragment 加载和基本交互
 */
@RunWith(AndroidJUnit4::class)
class UIIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private fun launchLearningFragment() = launchFragmentInContainer<LearningFragment>(
        themeResId = R.style.Theme_WordMemo
    )

    @Test
    fun testLearningFragmentNavigation() {
        val scenario = launchLearningFragment()
        scenario.onFragment { fragment ->
            assert(fragment.isAdded)
            val root = fragment.view ?: return@onFragment
            if (root is TextView && root.text == "页面加载失败") {
                fail("布局加载失败，显示「页面加载失败」")
            }
        }
    }

    @Test
    fun testLearningFragmentLoads() {
        val scenario = launchLearningFragment()
        scenario.onFragment { fragment ->
            val view = fragment.view ?: fail("fragment.view 为 null")
            if (view is TextView && view.text == "页面加载失败") {
                fail("布局加载失败，显示「页面加载失败」")
            }
        }
    }
}
