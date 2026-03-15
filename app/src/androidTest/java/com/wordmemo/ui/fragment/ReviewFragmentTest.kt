package com.wordmemo.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewFragmentTest {

    @Test
    fun testReviewFragmentLaunches() {
        launchFragmentInContainer<ReviewFragment>()
    }

    @Test
    fun testReviewFragmentViewsCreated() {
        val scenario = launchFragmentInContainer<ReviewFragment>()
        scenario.onFragment { fragment ->
            assert(fragment.view != null)
        }
    }
}
