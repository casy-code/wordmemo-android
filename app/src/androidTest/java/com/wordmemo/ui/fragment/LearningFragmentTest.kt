package com.wordmemo.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningFragmentTest {

    @Test
    fun learningFragmentLaunchesWithoutCrash() {
        // 若 ViewModel 未初始化等导致崩溃，launchFragmentInContainer 或 onFragment 会抛出异常
        val scenario = launchFragmentInContainer<LearningFragment>()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
        }
    }

    @Test
    fun learningFragmentShowsContent() {
        val scenario = launchFragmentInContainer<LearningFragment>()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            assertNotNull(fragment.requireView().rootView)
        }
    }
}
