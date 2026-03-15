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
class ReviewFragmentTest {

    /** 使用应用主题，确保布局正确加载（Material 组件、主题属性等） */
    private fun launchReviewFragment() = launchFragmentInContainer<ReviewFragment>(
        themeResId = R.style.Theme_WordMemo
    )

    private fun assertNotErrorView(fragment: ReviewFragment) {
        val root = fragment.view ?: fail("fragment.view 为 null")
        if (root is TextView && root.text == "页面加载失败") {
            fail("布局加载失败，显示「页面加载失败」")
        }
    }

    @Test
    fun testReviewFragmentLaunches() {
        val scenario = launchReviewFragment()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            assertNotErrorView(fragment)
        }
    }

    @Test
    fun testReviewFragmentViewsCreated() {
        val scenario = launchReviewFragment()
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
            assertNotNull(fragment.view?.rootView)
            assertNotErrorView(fragment)
        }
    }

    @Test
    fun testReviewFragmentHasWordCard() {
        val scenario = launchReviewFragment()
        scenario.onFragment { fragment ->
            val root: View? = fragment.view
            val wordCard = root?.findViewById<View>(R.id.word_card)
            assertNotNull("word_card 应在布局中存在", wordCard)
        }
    }

    @Test
    fun testReviewFragmentHasFeedbackButtons() {
        val scenario = launchReviewFragment()
        scenario.onFragment { fragment ->
            val root = fragment.view ?: return@onFragment
            assertNotNull("btn_forgot 应在布局中存在", root.findViewById<View>(R.id.btn_forgot))
            assertNotNull("btn_hard 应在布局中存在", root.findViewById<View>(R.id.btn_hard))
            assertNotNull("btn_normal 应在布局中存在", root.findViewById<View>(R.id.btn_normal))
            assertNotNull("btn_good 应在布局中存在", root.findViewById<View>(R.id.btn_good))
            assertNotNull("btn_perfect 应在布局中存在", root.findViewById<View>(R.id.btn_perfect))
        }
    }

    @Test
    fun testReviewFragmentHasContentTextView() {
        val scenario = launchReviewFragment()
        scenario.onFragment { fragment ->
            val root = fragment.view ?: return@onFragment
            val tvContent = root.findViewById<android.widget.TextView>(R.id.tv_content)
            assertNotNull("tv_content 应在布局中存在", tvContent)
        }
    }
}
