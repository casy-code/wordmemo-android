package com.wordmemo.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningFragmentTest {

    @Test
    fun testLearningFragmentLaunches() {
        launchFragmentInContainer<LearningFragment>()
    }

    @Test
    fun testLearningFragmentViewsCreated() {
        val scenario = launchFragmentInContainer<LearningFragment>()
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
        }
    }
}
