package com.wordmemo.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wordmemo.domain.usecase.LearningStatistics
import com.wordmemo.domain.usecase.LearningUseCase
import com.wordmemo.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class StatsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var learningUseCase: LearningUseCase

    private lateinit var viewModel: StatsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = StatsViewModel(learningUseCase)
    }

    @Test
    fun testInitializeStats() {
        runBlocking {
            val stats = LearningStatistics(
                todayLearningCount = 5,
                todayReviewCount = 3,
                consecutiveDays = 7
            )
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(stats))
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase).getTodayStatistics(1)
            assert(viewModel.todayLearningCount.value == 5)
            assert(viewModel.todayReviewCount.value == 3)
            assert(viewModel.consecutiveDays.value == 7)
        }
    }

    @Test
    fun testLoadLearningProgress() {
        runBlocking {
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(LearningStatistics(0, 0, 0)))
            whenever(learningUseCase.getLearningProgress(1)).thenReturn(45)
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            viewModel.loadLearningProgress()
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase).getLearningProgress(1)
            assert(viewModel.learningProgress.value == 45)
        }
    }

    @Test
    fun testRefreshStatistics() {
        runBlocking {
            val stats = LearningStatistics(10, 5, 14)
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(stats))
            whenever(learningUseCase.getLearningProgress(1)).thenReturn(60)
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            viewModel.refreshStatistics()
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase, atLeast(1)).getTodayStatistics(1)
            verify(learningUseCase, atLeast(1)).getLearningProgress(1)
        }
    }

    @Test
    fun testGetStatisticsSummary() {
        runBlocking {
            val stats = LearningStatistics(5, 3, 7)
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(stats))
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            val summary = viewModel.getStatisticsSummary()
            assert(summary.contains("5"))
            assert(summary.contains("3"))
            assert(summary.contains("7"))
        }
    }

    @Test
    fun testGetLearningStatusDescription_NoLearning() {
        runBlocking {
            val stats = LearningStatistics(0, 0, 0)
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(stats))
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            val description = viewModel.getLearningStatusDescription()
            assert(description.contains("还没有学习"))
        }
    }

    @Test
    fun testGetLearningStatusDescription_OnlyLearning() {
        runBlocking {
            val stats = LearningStatistics(5, 0, 0)
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(stats))
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            val description = viewModel.getLearningStatusDescription()
            assert(description.contains("已学习"))
            assert(description.contains("5"))
        }
    }

    @Test
    fun testGetLearningStatusDescription_OnlyReview() {
        runBlocking {
            val stats = LearningStatistics(0, 3, 0)
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(stats))
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            val description = viewModel.getLearningStatusDescription()
            assert(description.contains("已复习"))
            assert(description.contains("3"))
        }
    }

    @Test
    fun testGetLearningStatusDescription_BothLearningAndReview() {
        runBlocking {
            val stats = LearningStatistics(5, 3, 7)
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(stats))
            viewModel.initializeStats(1)
            mainCoroutineRule.advanceUntilIdle()
            val description = viewModel.getLearningStatusDescription()
            assert(description.contains("已学习"))
            assert(description.contains("5"))
            assert(description.contains("复习"))
            assert(description.contains("3"))
        }
    }

    @Test
    fun testClearErrorMessage() {
        viewModel.clearErrorMessage()
        assert(viewModel.errorMessage.value == "")
    }
}
