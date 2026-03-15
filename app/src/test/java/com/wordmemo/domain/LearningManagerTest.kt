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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LearningManagerTest {

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
    fun testGetTodayLearningCount() {
        runBlocking {
            whenever(learningRecordDao.getTodayLearningCount(1L)).thenReturn(flowOf(5))
            learningManager.getTodayLearningCount(1)
            verify(learningRecordDao).getTodayLearningCount(1L)
        }
    }

    @Test
    fun testGetTodayReviewCount() {
        runBlocking {
            whenever(learningRecordDao.getTodayReviewCountFlow(1L)).thenReturn(flowOf(3))
            learningManager.getTodayReviewCount(1)
            verify(learningRecordDao).getTodayReviewCountFlow(1L)
        }
    }

    @Test
    fun testRecordLearningFeedback_NewRecord() {
        runBlocking {
        val wordId = 1
        val listId = 1
        val quality = 4

        whenever(learningRecordDao.getRecordByWordAndList(wordId, listId)).thenReturn(null)
        whenever(learningRecordDao.insert(any())).thenReturn(1L)

            learningManager.recordLearningFeedback(wordId, listId, quality)
            verify(learningRecordDao).getRecordByWordAndList(wordId, listId)
            verify(learningRecordDao).insert(any())
        }
    }

    @Test
    fun testRecordLearningFeedback_ExistingRecord() {
        runBlocking {
        val wordId = 1
        val listId = 1
        val quality = 4
        val existingRecord = LearningRecord(
            id = 1,
            wordId = wordId,
            listId = listId,
            quality = 3,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )

        whenever(learningRecordDao.getRecordByWordAndList(wordId, listId)).thenReturn(existingRecord)
        whenever(learningRecordDao.update(any())).thenReturn(Unit)

            learningManager.recordLearningFeedback(wordId, listId, quality)
            verify(learningRecordDao).getRecordByWordAndList(wordId, listId)
            verify(learningRecordDao).update(any())
        }
    }

    @Test
    fun testGetLearningRecord() {
        runBlocking {
        val wordId = 1
        val listId = 1
        val record = LearningRecord(
            id = 1,
            wordId = wordId,
            listId = listId,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )

            whenever(learningRecordDao.getRecordByWordAndList(wordId, listId)).thenReturn(record)
            learningManager.getLearningRecord(wordId, listId)
            verify(learningRecordDao).getRecordByWordAndList(wordId, listId)
        }
    }

    @Test
    fun testGetConsecutiveLearningDays() {
        runBlocking {
            val listId = 1
            val days = learningManager.getConsecutiveLearningDays(listId)
            assert(days >= 0)
        }
    }
}
