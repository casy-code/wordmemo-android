package com.wordmemo

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 应用启动测试
 *
 * 验证应用能正常启动并进入首页，防止 ViewModel 未初始化等导致的闪退。
 * 此类问题单元测试无法发现，必须通过仪器化测试覆盖。
 */
@RunWith(AndroidJUnit4::class)
class AppLaunchTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun appLaunchesSuccessfully() {
        // 若 MainActivity 或 LearningFragment 崩溃，ActivityScenarioRule 会抛出异常
        activityRule.scenario.onActivity { activity ->
            assertNotNull(activity)
            assertNotNull(activity.findViewById(R.id.nav_host_fragment))
            assertNotNull(activity.findViewById(R.id.nav_view))
        }
    }

    @Test
    fun applicationContextIsWordMemoApplication() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        assert(appContext is WordMemoApplication) {
            "Application 应为 WordMemoApplication，实际为 ${appContext::class.java.name}"
        }
    }
}
