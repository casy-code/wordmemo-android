package com.wordmemo.domain.usecase

import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.LearningRecord
import com.wordmemo.data.entity.Word
import com.wordmemo.data.entity.WordList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LearningUseCaseTest {

    @Mock
    private lateinit var learningManager: LearningManager

    @Mock
    private lateinit var wordDao: WordDao

    @Mock
    private lateinit var wordListDao: WordListDao

    @Mock
    private lateinit var learningRecordDao: LearningRecordDao

    private lateinit var learningUseCase: LearningUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        learningUseCase = LearningUseCase(learningManager, wordDao, wordListDao, learningRecordDao)
    }

    @Test
    fun testGetWordList() {
        runBlocking {
            val wordList = WordList(
                id = 1,
                name = "CET-4",
                description = "College English Test Level 4",
                type = "preset"
            )
            whenever(wordListDao.getWordListById(1L)).thenReturn(wordList)
            learningUseCase.getWordList(1)
            verify(wordListDao).getWordListById(1L)
        }
    }

    @Test
    fun testGetTodayStatistics() {
        runBlocking {
            whenever(learningRecordDao.getTodayLearningCount(1L)).thenReturn(flowOf(5))
            whenever(learningRecordDao.getTodayReviewCountFlow(1L)).thenReturn(flowOf(3))
            learningUseCase.getTodayStatistics(1)
            verify(learningRecordDao).getTodayLearningCount(1L)
            verify(learningRecordDao).getTodayReviewCountFlow(1L)
        }
    }

    @Test
    fun testGetReviewDueWords() {
        runBlocking {
        val word1 = Word(id = 1, content = "hello", translation = "你好")
        val word2 = Word(id = 2, content = "world", translation = "世界")

            whenever(learningManager.getReviewDueWords(1)).thenReturn(listOf(word1, word2))
            learningUseCase.getReviewDueWords(1)
            verify(learningManager).getReviewDueWords(1)
        }
    }

    @Test
    fun testRecordFeedback() {
        runBlocking {
        whenever(learningManager.recordLearningFeedback(1, 1, 4)).thenReturn(Unit)

            learningUseCase.recordFeedback(1, 1, 4)
            verify(learningManager).recordLearningFeedback(1, 1, 4)
        }
    }

    @Test
    fun testRecordFeedback_InvalidQuality() {
        runBlocking {
            try {
                learningUseCase.recordFeedback(1, 1, 6)
                assert(false) { "Should throw IllegalArgumentException" }
            } catch (e: IllegalArgumentException) {
                assert(true)
            }
        }
    }

    @Test
    fun testGetWordProgress_NewWord() {
        runBlocking {
        val word = Word(id = 1, content = "hello", translation = "你好")

        whenever(wordDao.getWordById(1L)).thenReturn(word)
        whenever(learningManager.getLearningRecord(1, 1)).thenReturn(null)
        whenever(learningRecordDao.getRecordCountForWord(1, 1)).thenReturn(0)

        val progress = learningUseCase.getWordProgress(1, 1)

        assert(progress != null)
        assert(progress!!.quality == 0)
        assert(progress.interval == 1)
            assert(progress.easeFactor == 2.5)
            assert(progress.isReviewDue)
        }
    }

    @Test
    fun testGetWordProgress_LearnedWord() {
        runBlocking {
        val word = Word(id = 1, content = "hello", translation = "你好")
        val record = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L,
            reviewedAt = System.currentTimeMillis()
        )

        whenever(wordDao.getWordById(1L)).thenReturn(word)
        whenever(learningManager.getLearningRecord(1, 1)).thenReturn(record)
        whenever(learningRecordDao.getRecordCountForWord(1, 1)).thenReturn(1)

        val progress = learningUseCase.getWordProgress(1, 1)

        assert(progress != null)
        assert(progress!!.quality == 4)
        assert(progress.interval == 3)
        assert(progress.easeFactor == 2.6)
            assert(!progress.isReviewDue)
        }
    }

    @Test
    fun testHasReviewDueWords_True() {
        runBlocking {
        val word = Word(id = 1, content = "hello", translation = "你好")

            whenever(learningManager.getReviewDueWords(1)).thenReturn(listOf(word))
            assert(learningUseCase.hasReviewDueWords(1))
        }
    }

    @Test
    fun testHasReviewDueWords_False() {
        runBlocking {
            whenever(learningManager.getReviewDueWords(1)).thenReturn(emptyList())
            assert(!learningUseCase.hasReviewDueWords(1))
        }
    }

    @Test
    fun testGetWordsToLearnToday() {
        runBlocking {
            val allWords = listOf(
                Word(id = 1, content = "a", translation = "一"),
                Word(id = 2, content = "b", translation = "二"),
                Word(id = 3, content = "c", translation = "三")
            )
            val todayRecords = listOf(
                LearningRecord(wordId = 1, listId = 1, reviewedAt = System.currentTimeMillis())
            )
            whenever(wordDao.getWordsByListId(1)).thenReturn(allWords)
            whenever(learningRecordDao.getTodayLearnedRecords(1L)).thenReturn(todayRecords)
            val result = learningUseCase.getWordsToLearnToday(1)
            assert(result.size == 2)
            assert(result.any { it.id == 2 })
            assert(result.any { it.id == 3 })
        }
    }

    @Test
    fun testGetTodayLearnedWords() {
        runBlocking {
            val word = Word(id = 1, content = "hello", translation = "你好")
            val records = listOf(
                LearningRecord(wordId = 1, listId = 1, reviewedAt = System.currentTimeMillis())
            )
            whenever(learningRecordDao.getTodayLearnedRecords(1L)).thenReturn(records)
            whenever(wordDao.getWordById(1L)).thenReturn(word)
            val result = learningUseCase.getTodayLearnedWords(1)
            assert(result.size == 1)
            assert(result[0].content == "hello")
        }
    }

    @Test
    fun testGetLearningProgress() {
        runBlocking {
        val word1 = Word(id = 1, content = "hello", translation = "你好")
        val word2 = Word(id = 2, content = "world", translation = "世界")

        whenever(wordDao.getWordsByListId(1)).thenReturn(listOf(word1, word2))
        whenever(learningManager.getLearningRecord(1, 1)).thenReturn(
            LearningRecord(
                id = 1,
                wordId = 1,
                listId = 1,
                quality = 4,
                interval = 3,
                easeFactor = 2.6,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
        )
        whenever(learningManager.getLearningRecord(2, 1)).thenReturn(
            LearningRecord(
                id = 2,
                wordId = 2,
                listId = 1,
                quality = 2,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
        )
            val progress = learningUseCase.getLearningProgress(1)
            assert(progress == 50)
        }
    }
}
