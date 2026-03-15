package com.wordmemo.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * 设置 Main 协程调度器为 StandardTestDispatcher，用于 ViewModel 单元测试
 */
@ExperimentalCoroutinesApi
class MainCoroutineRule : TestWatcher() {

    val testDispatcher = StandardTestDispatcher()

    fun advanceUntilIdle() = testDispatcher.scheduler.advanceUntilIdle()

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
