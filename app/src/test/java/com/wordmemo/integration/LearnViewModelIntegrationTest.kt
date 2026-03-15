package com.wordmemo.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.Word
import com.wordmemo.domain.usecase.LearningUseCase
import com.wordmemo.ui.viewmodel.LearnViewModel
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
class LearnViewModelIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var wordDao: WordDao

    @Mock
    private lateinit var wordListDao: WordListDao

    @Mock
    private lateinit var learningRecordDao: LearningRecordDao

    @Mock
    private lateinit var learningUseCase: LearningUseCase

    private lateinit var viewModel: LearnViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = LearnViewModel(learningUseCase)
    }

    @Test
    fun testCompleteLearnFlow() {
        runBlocking {
            val words = listOf(
                Word(id = 1, content = "hello", translation = "你好"),
                Word(id = 2, content = "world", translation = "世界")
            )
            whenever(learningUseCase.getWordsToLearnToday(1)).thenReturn(words)
            whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
                com.wordmemo.domain.usecase.LearningStatistics(
                    todayLearningCount = 0,
                    todayReviewCount = 0,
                    consecutiveDays = 0
                )
            ))
            whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)
            viewModel.initializeLearning(1)
            mainCoroutineRule.advanceUntilIdle()
            assert(viewModel.currentWord.value?.id == 1)
            assert(viewModel.currentWordIndex.value == 0)
            assert(viewModel.totalWords.value == 2)
            viewModel.recordFeedback(4)
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase).recordFeedback(1, 1, 4)
            assert(viewModel.feedbackMessage.value?.contains("记得不错") == true)
        }
    }

    @Test
    fun testCardFlipIntegration() {
        runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好")
        )

        whenever(learningUseCase.getWordsToLearnToday(1)).thenReturn(words)
        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
            whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            viewModel.initializeLearning(1)
            mainCoroutineRule.advanceUntilIdle()
            val initialState = viewModel.isCardFlipped.value ?: false
            viewModel.flipCard()
            assert(viewModel.isCardFlipped.value != initialState)
            viewModel.flipCard()
            assert(viewModel.isCardFlipped.value == initialState)
        }
    }

    @Test
    fun testWordNavigationIntegration() {
        runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界"),
            Word(id = 3, content = "test", translation = "测试")
        )

        whenever(learningUseCase.getWordsToLearnToday(1)).thenReturn(words)
        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)
            viewModel.initializeLearning(1)
            mainCoroutineRule.advanceUntilIdle()
            assert(viewModel.currentWord.value?.id == 1)
            assert(viewModel.currentWordIndex.value == 0)
            viewModel.moveToNextWord()
            assert(viewModel.currentWord.value?.id == 2)
            assert(viewModel.currentWordIndex.value == 1)
            viewModel.moveToPreviousWord()
            assert(viewModel.currentWord.value?.id == 1)
            assert(viewModel.currentWordIndex.value == 0)
        }
    }

    @Test
    fun testLearningCompleteIntegration() {
        runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好")
        )

        whenever(learningUseCase.getWordsToLearnToday(1)).thenReturn(words)
        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)
            viewModel.initializeLearning(1)
            mainCoroutineRule.advanceUntilIdle()
            viewModel.recordFeedback(4)
            mainCoroutineRule.advanceUntilIdle()
            assert(viewModel.isLearningComplete.value == true)
            assert(viewModel.currentWord.value == null)
        }
    }

    @Test
    fun testFeedbackQualityIntegration() {
        runBlocking {
            val words = listOf(Word(id = 1, content = "hello", translation = "你好"))
            whenever(learningUseCase.getWordsToLearnToday(1)).thenReturn(words)
            whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
            whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
                com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
            ))
            whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)
            viewModel.initializeLearning(1)
            mainCoroutineRule.advanceUntilIdle()
            viewModel.recordFeedback(4)
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase, atLeast(1)).recordFeedback(1, 1, 4)
        }
    }

    @Test
    fun testSkipWordIntegration() {
        runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

        whenever(learningUseCase.getWordsToLearnToday(1)).thenReturn(words)
        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
            whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            viewModel.initializeLearning(1)
            mainCoroutineRule.advanceUntilIdle()
            assert(viewModel.currentWord.value?.id == 1)
            viewModel.skipWord()
            assert(viewModel.currentWord.value?.id == 2)
        }
    }
}
