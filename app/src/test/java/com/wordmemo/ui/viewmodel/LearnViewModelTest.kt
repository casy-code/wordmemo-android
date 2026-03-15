package com.wordmemo.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wordmemo.data.entity.Word
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
class LearnViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var learningUseCase: LearningUseCase

    private lateinit var viewModel: LearnViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = LearnViewModel(learningUseCase)
    }

    @Test
    fun testInitializeLearning() {
        runBlocking {
            val words = listOf(
                Word(id = 1, content = "hello", translation = "你好"),
                Word(id = 2, content = "world", translation = "世界")
            )
            whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
            viewModel.initializeLearning(1)
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase, atLeast(1)).getAllWordsInList(1)
        }
    }

    @Test
    fun testFlipCard() {
        val initialState = viewModel.isCardFlipped.value ?: false
        viewModel.flipCard()
        
        assert(viewModel.isCardFlipped.value != initialState)
    }

    @Test
    fun testRecordFeedback() {
        runBlocking {
            val word = Word(id = 1, content = "hello", translation = "你好")
            whenever(learningUseCase.recordFeedback(1, 1, 4)).thenReturn(Unit)
            whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            viewModel.setCurrentWordForTesting(word)
            viewModel.setCurrentListIdForTesting(1)
            viewModel.recordFeedback(4)
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase).recordFeedback(1, 1, 4)
        }
    }

    @Test
    fun testMoveToNextWord() {
        runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))

        viewModel.initializeLearning(1)
        mainCoroutineRule.advanceUntilIdle()
        
        val initialIndex = viewModel.getCurrentWordIndex()
        viewModel.moveToNextWord()
        
            assert(viewModel.getCurrentWordIndex() > initialIndex)
        }
    }

    @Test
    fun testMoveToPreviousWord() {
        runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))

        viewModel.initializeLearning(1)
        mainCoroutineRule.advanceUntilIdle()
        viewModel.moveToNextWord()
        
        val indexBeforeMove = viewModel.getCurrentWordIndex()
        viewModel.moveToPreviousWord()
        
            assert(viewModel.getCurrentWordIndex() < indexBeforeMove)
        }
    }

    @Test
    fun testGetTotalWords() {
        runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界"),
            Word(id = 3, content = "test", translation = "测试")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))

        viewModel.initializeLearning(1)
        mainCoroutineRule.advanceUntilIdle()
        
            assert(viewModel.getTotalWords() == 3)
        }
    }

    @Test
    fun testClearFeedbackMessage() {
        viewModel.clearFeedbackMessage()
        
        assert(viewModel.feedbackMessage.value == "")
    }

    @Test
    fun testFeedbackQualityMessages() {
        runBlocking {
            val word = Word(id = 1, content = "hello", translation = "你好")
            whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)
            whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
            viewModel.setCurrentWordForTesting(word)
            viewModel.setCurrentListIdForTesting(1)
            viewModel.recordFeedback(4)
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase, atLeast(1)).recordFeedback(1, 1, 4)
        }
    }
}
