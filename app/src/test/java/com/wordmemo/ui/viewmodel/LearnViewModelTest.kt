package com.wordmemo.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.Word
import com.wordmemo.domain.usecase.LearningUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LearnViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var learningUseCase: LearningUseCase

    private lateinit var viewModel: LearnViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = LearnViewModel(learningUseCase)
    }

    @Test
    fun testInitializeLearning() = runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))

        viewModel.initializeLearning(1)

        verify(learningUseCase).getAllWordsInList(1)
    }

    @Test
    fun testFlipCard() {
        val initialState = viewModel.isCardFlipped.value ?: false
        viewModel.flipCard()
        
        assert(viewModel.isCardFlipped.value != initialState)
    }

    @Test
    fun testRecordFeedback() = runBlocking {
        val word = Word(id = 1, content = "hello", translation = "你好")
        
        whenever(learningUseCase.recordFeedback(1, 1, 4)).thenReturn(Unit)
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)

        // 设置当前单词
        viewModel.currentWord.value = word

        viewModel.recordFeedback(4)

        verify(learningUseCase).recordFeedback(1, 1, 4)
    }

    @Test
    fun testMoveToNextWord() {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

        // 模拟加载单词
        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))

        viewModel.initializeLearning(1)
        
        val initialIndex = viewModel.getCurrentWordIndex()
        viewModel.moveToNextWord()
        
        assert(viewModel.getCurrentWordIndex() > initialIndex)
    }

    @Test
    fun testMoveToPreviousWord() {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))

        viewModel.initializeLearning(1)
        viewModel.moveToNextWord()
        
        val indexBeforeMove = viewModel.getCurrentWordIndex()
        viewModel.moveToPreviousWord()
        
        assert(viewModel.getCurrentWordIndex() < indexBeforeMove)
    }

    @Test
    fun testGetTotalWords() {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界"),
            Word(id = 3, content = "test", translation = "测试")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))

        viewModel.initializeLearning(1)
        
        assert(viewModel.getTotalWords() == 3)
    }

    @Test
    fun testClearFeedbackMessage() {
        viewModel.clearFeedbackMessage()
        
        assert(viewModel.feedbackMessage.value == "")
    }

    @Test
    fun testFeedbackQualityMessages() = runBlocking {
        val word = Word(id = 1, content = "hello", translation = "你好")
        
        whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)

        viewModel.currentWord.value = word

        // 测试不同质量的反馈消息
        val qualityMessages = mapOf(
            0 to "完全忘记",
            1 to "非常困难",
            2 to "困难",
            3 to "勉强记得",
            4 to "记得不错",
            5 to "完美记得"
        )

        for ((quality, expectedMessage) in qualityMessages) {
            viewModel.recordFeedback(quality)
            assert(viewModel.feedbackMessage.value?.contains(expectedMessage) == true)
        }
    }
}
