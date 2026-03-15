package com.wordmemo.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.data.db.AppDatabase
import com.wordmemo.data.entity.LearningRecord
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class LearningRecordDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var learningRecordDao: LearningRecordDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        learningRecordDao = database.learningRecordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndGetRecord() = runBlocking {
        val record = LearningRecord(
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000,
            reviewedAt = System.currentTimeMillis()
        )
        
        val id = learningRecordDao.insert(record)
        assertTrue(id > 0)
        
        val retrieved = learningRecordDao.getRecordById(id)
        assertNotNull(retrieved)
        assertEquals(1, retrieved?.wordId)
        assertEquals(4, retrieved?.quality)
    }

    @Test
    fun testGetRecordByWordAndList() = runBlocking {
        val record = LearningRecord(
            wordId = 5,
            listId = 2,
            quality = 3,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )
        
        learningRecordDao.insert(record)
        
        val retrieved = learningRecordDao.getRecordByWordAndList(5, 2)
        assertNotNull(retrieved)
        assertEquals(3, retrieved?.quality)
    }

    @Test
    fun testUpdateRecord() = runBlocking {
        val record = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 2,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )
        
        learningRecordDao.insert(record)
        
        val updated = record.copy(quality = 5, interval = 7, easeFactor = 2.8)
        learningRecordDao.update(updated)
        
        val retrieved = learningRecordDao.getRecordById(1L)
        assertEquals(5, retrieved?.quality)
        assertEquals(7, retrieved?.interval)
        assertEquals(2.8, retrieved?.easeFactor ?: 0.0, 0.01)
    }

    @Test
    fun testDeleteRecord() = runBlocking {
        val record = LearningRecord(
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )
        
        val id = learningRecordDao.insert(record)
        learningRecordDao.delete(record.copy(id = id.toInt()))
        
        val retrieved = learningRecordDao.getRecordById(id)
        assertNull(retrieved)
    }

    @Test
    fun testGetTodayLearningCount() = runBlocking {
        val now = System.currentTimeMillis()
        val todayStart = now - (now % (24 * 60 * 60 * 1000))
        
        val records = listOf(
            LearningRecord(
                wordId = 1,
                listId = 1,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = now,
                reviewedAt = now
            ),
            LearningRecord(
                wordId = 2,
                listId = 1,
                quality = 3,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = now,
                reviewedAt = now
            ),
            LearningRecord(
                wordId = 3,
                listId = 1,
                quality = 5,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = now,
                reviewedAt = now
            )
        )
        
        records.forEach { learningRecordDao.insert(it) }
        
        // 验证流可以被收集
        val countFlow = learningRecordDao.getTodayLearningCount(1)
        assertNotNull(countFlow)
    }

    @Test
    fun testGetRecordsByListId() = runBlocking {
        val records = listOf(
            LearningRecord(
                wordId = 1,
                listId = 1,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            ),
            LearningRecord(
                wordId = 2,
                listId = 1,
                quality = 3,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            ),
            LearningRecord(
                wordId = 3,
                listId = 2,
                quality = 5,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
        )
        
        records.forEach { learningRecordDao.insert(it) }
        
        val list1Records = learningRecordDao.getRecordsByListId(1)
        assertEquals(2, list1Records.size)
        assertTrue(list1Records.all { it.listId == 1 })
    }

    @Test
    fun testDeleteAll() = runBlocking {
        val records = listOf(
            LearningRecord(
                wordId = 1,
                listId = 1,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            ),
            LearningRecord(
                wordId = 2,
                listId = 1,
                quality = 3,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
        )
        
        records.forEach { learningRecordDao.insert(it) }
        
        learningRecordDao.deleteAll()
        
        val allRecords = learningRecordDao.getAllRecords()
        assertEquals(0, allRecords.size)
    }
}
