package com.wordmemo.ui.fragment

import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.R
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningFragmentTest {

    private fun launchLearningFragment() = launchFragmentInContainer<LearningFragment>(
        themeResId = R.style.Theme_WordMemo
    )

    private fun assertNotErrorView(fragment: LearningFragment) {
        val root = fragment.requireView()
        if (root is TextView && root.text == "页面加载失败") {
            fail("布局加载失败，显示「页面加载失败」")
        }
        val wordCard = root.findViewById<View>(R.id.word_card)
        assertNotNull("word_card 未找到，可能布局加载失败", wordCard)
    }

    @Test
    fun learningFragmentLaunchesWithoutCrash() {
        val scenario = launchLearningFragment()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            assertNotErrorView(fragment)
        }
    }

    @Test
    fun learningFragmentShowsContent() {
        val scenario = launchLearningFragment()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            assertNotNull(fragment.view?.rootView)
            assertNotErrorView(fragment)
        }
    }
}
