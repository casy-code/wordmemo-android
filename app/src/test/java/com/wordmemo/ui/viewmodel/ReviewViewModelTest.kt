package com.wordmemo.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wordmemo.data.entity.Word
import com.wordmemo.domain.usecase.LearningUseCase
import com.wordmemo.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ReviewViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var learningUseCase: LearningUseCase

    private lateinit var viewModel: ReviewViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = ReviewViewModel(learningUseCase)
    }

    @Test
    fun initializeReview_loadsDueWords() {
        runBlocking {
            val words = listOf(
                Word(id = 1, content = "hello", translation = "你好"),
                Word(id = 2, content = "world", translation = "世界")
            )
            whenever(learningUseCase.getReviewDueWords(1)).thenReturn(words)

            viewModel.initializeReview(1)
            mainCoroutineRule.advanceUntilIdle()

            verify(learningUseCase).getReviewDueWords(1)
            assert(viewModel.getTotalWords() == 2)
            assert(viewModel.currentWord.value?.content == "hello")
        }
    }

    @Test
    fun initializeReview_emptyList_showsComplete() {
        runBlocking {
            whenever(learningUseCase.getReviewDueWords(1)).thenReturn(emptyList())

            viewModel.initializeReview(1)
            mainCoroutineRule.advanceUntilIdle()

            assert(viewModel.getTotalWords() == 0)
            assert(viewModel.isReviewComplete.value == true)
            assert(viewModel.currentWord.value == null)
        }
    }

    @Test
    fun flipCard_togglesState() {
        val initial = viewModel.isCardFlipped.value ?: false
        viewModel.flipCard()
        assert(viewModel.isCardFlipped.value != initial)
        viewModel.flipCard()
        assert(viewModel.isCardFlipped.value == initial)
    }

    @Test
    fun recordFeedback_callsUseCase() {
        runBlocking {
            val word = Word(id = 1, content = "hello", translation = "你好")
            whenever(learningUseCase.recordFeedback(1, 1, 4)).thenReturn(Unit)
            viewModel.setCurrentWordForTesting(word)
            viewModel.setCurrentListIdForTesting(1)

            viewModel.recordFeedback(4)
            mainCoroutineRule.advanceUntilIdle()

            verify(learningUseCase).recordFeedback(1, 1, 4)
        }
    }

    @Test
    fun recordFeedback_allQualities() {
        runBlocking {
            val words = (1..3).map { i ->
                Word(id = i, content = "word$i", translation = "词$i")
            }
            whenever(learningUseCase.getReviewDueWords(1)).thenReturn(words)
            whenever(learningUseCase.recordFeedback(any(), any(), any())).thenReturn(Unit)

            viewModel.initializeReview(1)
            mainCoroutineRule.advanceUntilIdle()

            for (quality in listOf(0, 3, 5)) {
                viewModel.recordFeedback(quality)
                mainCoroutineRule.advanceUntilIdle()
            }
            verify(learningUseCase, atLeast(3)).recordFeedback(any(), any(), any())
        }
    }

    @Test
    fun moveToNextWord_advancesIndex() {
        runBlocking {
            val words = listOf(
                Word(id = 1, content = "a", translation = "A"),
                Word(id = 2, content = "b", translation = "B")
            )
            whenever(learningUseCase.getReviewDueWords(1)).thenReturn(words)

            viewModel.initializeReview(1)
            mainCoroutineRule.advanceUntilIdle()

            assert(viewModel.getCurrentWordIndex() == 0)
            viewModel.moveToNextWord()
            assert(viewModel.getCurrentWordIndex() == 1)
            assert(viewModel.currentWord.value?.content == "b")
        }
    }

    @Test
    fun moveToNextWord_lastWord_setsComplete() {
        runBlocking {
            val words = listOf(Word(id = 1, content = "only", translation = "唯一"))
            whenever(learningUseCase.getReviewDueWords(1)).thenReturn(words)

            viewModel.initializeReview(1)
            mainCoroutineRule.advanceUntilIdle()

            viewModel.moveToNextWord()
            assert(viewModel.isReviewComplete.value == true)
            assert(viewModel.currentWord.value == null)
        }
    }

    @Test
    fun clearFeedbackMessage() {
        viewModel.clearFeedbackMessage()
        assert(viewModel.feedbackMessage.value == "")
    }

    @Test
    fun refreshReview_reloadsWords() {
        runBlocking {
            val words1 = listOf(Word(id = 1, content = "a", translation = "A"))
            val words2 = listOf(
                Word(id = 1, content = "a", translation = "A"),
                Word(id = 2, content = "b", translation = "B")
            )
            whenever(learningUseCase.getReviewDueWords(1)).thenReturn(words1).thenReturn(words2)

            viewModel.initializeReview(1)
            mainCoroutineRule.advanceUntilIdle()
            assert(viewModel.getTotalWords() == 1)

            viewModel.refreshReview()
            mainCoroutineRule.advanceUntilIdle()
            verify(learningUseCase, atLeast(2)).getReviewDueWords(1)
        }
    }
}
