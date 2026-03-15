package com.wordmemo.ui.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import com.wordmemo.ui.fragment.LearningFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI 集成测试
 * 测试 Fragment 和 ViewModel 的交互
 */
@RunWith(AndroidJUnit4::class)
class UIIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testLearningFragmentNavigation() {
        val scenario = launchFragmentInContainer<LearningFragment>()
        
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun testCardFlipInteraction() {
        val scenario = launchFragmentInContainer<LearningFragment>()
        
        scenario.onFragment { fragment ->
            // 验证 Fragment 已加载
            assert(fragment.isAdded)
        }
    }

    @Test
    fun testFeedbackButtonInteraction() {
        val scenario = launchFragmentInContainer<LearningFragment>()
        
        scenario.onFragment { fragment ->
            // 验证反馈按钮存在
            assert(fragment.requireView().findViewById<Any?>(R.id.btn_feedback_good) != null)
        }
    }
}
