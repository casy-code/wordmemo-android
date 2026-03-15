package com.wordmemo.domain.usecase

import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.entity.LearningRecord
import com.wordmemo.data.entity.Word
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LearningManagerExtendedTest {

    @Mock
    private lateinit var wordDao: WordDao

    @Mock
    private lateinit var learningRecordDao: LearningRecordDao

    private lateinit var learningManager: LearningManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        learningManager = LearningManager(wordDao, learningRecordDao)
    }

    @Test
    fun testGetReviewDueWords() {
        runBlocking {
        val word1 = Word(id = 1, content = "hello", translation = "你好")
        val word2 = Word(id = 2, content = "world", translation = "世界")
        val record1 = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = System.currentTimeMillis() - 1000,
            reviewedAt = System.currentTimeMillis()
        )
        val record2 = LearningRecord(
            id = 2,
            wordId = 2,
            listId = 1,
            quality = 3,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis() - 1000,
            reviewedAt = System.currentTimeMillis()
        )

        whenever(learningRecordDao.getDueRecordsFlow(eq(1L), any())).thenReturn(
            flowOf(listOf(record1, record2))
        )
        whenever(wordDao.getWordById(1L)).thenReturn(word1)
        whenever(wordDao.getWordById(2L)).thenReturn(word2)

        val result = learningManager.getReviewDueWords(1)

            assert(result.size == 2)
            assert(result[0].id == 1)
            assert(result[1].id == 2)
        }
    }

    @Test
    fun testGetReviewDueWords_Empty() {
        runBlocking {
        whenever(learningRecordDao.getDueRecordsFlow(eq(1L), any())).thenReturn(flowOf(emptyList()))

            val result = learningManager.getReviewDueWords(1)
            assert(result.isEmpty())
        }
    }

    @Test
    fun testRecordLearningFeedback_NewRecord() {
        runBlocking {
        whenever(learningRecordDao.getRecordByWordAndList(1, 1)).thenReturn(null)
        whenever(learningRecordDao.insert(any())).thenReturn(1L)

        learningManager.recordLearningFeedback(1, 1, 4)

            verify(learningRecordDao).getRecordByWordAndList(1, 1)
            verify(learningRecordDao).insert(any())
        }
    }

    @Test
    fun testRecordLearningFeedback_UpdateRecord() {
        runBlocking {
        val existingRecord = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 3,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )

        whenever(learningRecordDao.getRecordByWordAndList(1, 1)).thenReturn(existingRecord)
        whenever(learningRecordDao.update(any())).thenReturn(Unit)

        learningManager.recordLearningFeedback(1, 1, 4)

            verify(learningRecordDao).getRecordByWordAndList(1, 1)
            verify(learningRecordDao).update(any())
        }
    }

    @Test
    fun testRecordLearningFeedback_FailureCase() {
        runBlocking {
        val existingRecord = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )

        whenever(learningRecordDao.getRecordByWordAndList(1, 1)).thenReturn(existingRecord)
        whenever(learningRecordDao.update(any())).thenReturn(Unit)

        // 记录失败反馈（quality = 1）
        learningManager.recordLearningFeedback(1, 1, 1)

            verify(learningRecordDao).update(any())
        }
    }

    @Test
    fun testGetLearningRecord() {
        runBlocking {
        val record = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )

        whenever(learningRecordDao.getRecordByWordAndList(1, 1)).thenReturn(record)

        val result = learningManager.getLearningRecord(1, 1)

            assert(result != null)
            assert(result!!.quality == 4)
        }
    }

    @Test
    fun testGetLearningRecord_NotFound() {
        runBlocking {
        whenever(learningRecordDao.getRecordByWordAndList(1, 1)).thenReturn(null)

            val result = learningManager.getLearningRecord(1, 1)
            assert(result == null)
        }
    }

    @Test
    fun testGetConsecutiveLearningDays() {
        runBlocking {
            val days = learningManager.getConsecutiveLearningDays(1)
            assert(days == 0)
        }
    }
}
