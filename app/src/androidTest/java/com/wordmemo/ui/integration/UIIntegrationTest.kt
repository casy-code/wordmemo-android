package com.wordmemo.ui.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.ui.fragment.LearningFragment
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

    @Test
    fun testLearningFragmentNavigation() {
        val scenario = launchFragmentInContainer<LearningFragment>()
        scenario.onFragment { fragment ->
            assert(fragment.isAdded)
        }
    }

    @Test
    fun testLearningFragmentLoads() {
        val scenario = launchFragmentInContainer<LearningFragment>()
        scenario.onFragment { fragment ->
            assert(fragment.requireView() != null)
        }
    }
}
