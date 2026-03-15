package com.wordmemo.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.LearningRecord
import com.wordmemo.data.entity.Word
import com.wordmemo.domain.usecase.LearningUseCase
import com.wordmemo.ui.viewmodel.LearnViewModel
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

/**
 * 学习流程与 UI 集成测试
 * 
 * 测试 LearnViewModel 与 LearningUseCase 的完整集成
 */
class LearnViewModelIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

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
    fun testCompleteLearnFlow() = runBlocking {
        // 准备测试数据
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

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

        // 初始化学习
        viewModel.initializeLearning(1)

        // 验证初始状态
        assert(viewModel.currentWord.value?.id == 1)
        assert(viewModel.currentWordIndex.value == 0)
        assert(viewModel.totalWords.value == 2)

        // 记录反馈
        viewModel.recordFeedback(4)
        verify(learningUseCase).recordFeedback(1, 1, 4)

        // 验证反馈消息
        assert(viewModel.feedbackMessage.value?.contains("记得不错") == true)
    }

    @Test
    fun testCardFlipIntegration() = runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)

        viewModel.initializeLearning(1)

        // 测试卡片翻转
        val initialState = viewModel.isCardFlipped.value ?: false
        viewModel.flipCard()
        assert(viewModel.isCardFlipped.value != initialState)

        viewModel.flipCard()
        assert(viewModel.isCardFlipped.value == initialState)
    }

    @Test
    fun testWordNavigationIntegration() = runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界"),
            Word(id = 3, content = "test", translation = "测试")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
        whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)

        viewModel.initializeLearning(1)

        // 验证第一个单词
        assert(viewModel.currentWord.value?.id == 1)
        assert(viewModel.currentWordIndex.value == 0)

        // 记录反馈并移动到下一个单词
        viewModel.recordFeedback(4)
        kotlinx.coroutines.delay(1000)

        // 验证第二个单词
        assert(viewModel.currentWord.value?.id == 2)
        assert(viewModel.currentWordIndex.value == 1)

        // 移动到上一个单词
        viewModel.moveToPreviousWord()
        assert(viewModel.currentWord.value?.id == 1)
        assert(viewModel.currentWordIndex.value == 0)
    }

    @Test
    fun testLearningCompleteIntegration() = runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
        whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)

        viewModel.initializeLearning(1)

        // 记录反馈
        viewModel.recordFeedback(4)
        kotlinx.coroutines.delay(1000)

        // 验证学习完成
        assert(viewModel.isLearningComplete.value == true)
        assert(viewModel.currentWord.value == null)
    }

    @Test
    fun testFeedbackQualityIntegration() = runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)
        whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)

        viewModel.initializeLearning(1)

        // 测试不同质量的反馈
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

    @Test
    fun testSkipWordIntegration() = runBlocking {
        val words = listOf(
            Word(id = 1, content = "hello", translation = "你好"),
            Word(id = 2, content = "world", translation = "世界")
        )

        whenever(learningUseCase.getAllWordsInList(1)).thenReturn(flowOf(words))
        whenever(learningUseCase.getTodayStatistics(1)).thenReturn(flowOf(
            com.wordmemo.domain.usecase.LearningStatistics(0, 0, 0)
        ))
        whenever(learningUseCase.getWordProgress(any(), any())).thenReturn(null)

        viewModel.initializeLearning(1)

        // 验证第一个单词
        assert(viewModel.currentWord.value?.id == 1)

        // 跳过单词
        viewModel.skipWord()

        // 验证第二个单词
        assert(viewModel.currentWord.value?.id == 2)
    }
}
