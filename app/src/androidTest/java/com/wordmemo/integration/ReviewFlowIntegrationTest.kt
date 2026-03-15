package com.wordmemo.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.data.db.AppDatabase
import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.LearningRecord
import com.wordmemo.data.entity.Word
import com.wordmemo.data.entity.WordList
import com.wordmemo.domain.usecase.LearningManager
import com.wordmemo.domain.usecase.LearningUseCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * 复习流程集成测试
 * 验证待复习单词加载、复习反馈、SM-2 更新
 */
@RunWith(AndroidJUnit4::class)
class ReviewFlowIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var wordDao: WordDao
    private lateinit var wordListDao: WordListDao
    private lateinit var learningRecordDao: LearningRecordDao
    private lateinit var learningUseCase: LearningUseCase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        wordDao = database.wordDao()
        wordListDao = database.wordListDao()
        learningRecordDao = database.learningRecordDao()
        val learningManager = LearningManager(wordDao, learningRecordDao)
        learningUseCase = LearningUseCase(learningManager, wordDao, wordListDao, learningRecordDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testGetReviewDueWords_returnsOnlyDueWords() = runBlocking {
        val wordList = WordList(name = "Review Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = listOf(
            Word(content = "due1", translation = "到期1", difficulty = 1),
            Word(content = "due2", translation = "到期2", difficulty = 1),
            Word(content = "notDue", translation = "未到期", difficulty = 1)
        )
        wordDao.insertAll(words)
        val allWords = wordDao.getAllWords()
        database.wordListItemDao().insertAll(
            allWords.map { com.wordmemo.data.entity.WordListItem(wordId = it.id, listId = listId) }
        )

        val now = System.currentTimeMillis()
        // 前两个单词：已过期
        learningRecordDao.insert(
            LearningRecord(
                wordId = allWords[0].id,
                listId = listId,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = now - 1000,
                reviewedAt = now
            )
        )
        learningRecordDao.insert(
            LearningRecord(
                wordId = allWords[1].id,
                listId = listId,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = now - 2000,
                reviewedAt = now
            )
        )
        // 第三个单词：未过期
        learningRecordDao.insert(
            LearningRecord(
                wordId = allWords[2].id,
                listId = listId,
                quality = 4,
                interval = 3,
                easeFactor = 2.5,
                nextReviewDate = now + 24 * 60 * 60 * 1000,
                reviewedAt = now
            )
        )

        val dueWords = learningUseCase.getReviewDueWords(listId)
        assertEquals(2, dueWords.size)
        assertTrue(dueWords.any { it.content == "due1" })
        assertTrue(dueWords.any { it.content == "due2" })
        assertFalse(dueWords.any { it.content == "notDue" })
    }

    @Test
    fun testReviewFeedback_updatesRecord() = runBlocking {
        val wordList = WordList(name = "Review Update", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val word = Word(content = "review", translation = "复习", difficulty = 1)
        val wordId = wordDao.insert(word).toInt()
        database.wordListItemDao().insertAll(
            listOf(com.wordmemo.data.entity.WordListItem(wordId = wordId, listId = listId))
        )

        val now = System.currentTimeMillis()
        learningRecordDao.insert(
            LearningRecord(
                wordId = wordId,
                listId = listId,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = now - 1000,
                reviewedAt = now
            )
        )

        learningUseCase.recordFeedback(wordId, listId, 5)

        val record = requireNotNull(learningRecordDao.getRecordByWordAndList(wordId, listId))
        assertEquals(5, record.quality)
        assertTrue(record.interval > 1)
        assertTrue(record.nextReviewDate > now)
    }

    @Test
    fun testEmptyReviewDueWords() = runBlocking {
        val wordList = WordList(name = "Empty", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val dueWords = learningUseCase.getReviewDueWords(listId)
        assertTrue(dueWords.isEmpty())
    }

    @Test
    fun testHasReviewDueWords() = runBlocking {
        val wordList = WordList(name = "Has Due", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val word = Word(content = "due", translation = "到期", difficulty = 1)
        val wordId = wordDao.insert(word).toInt()
        database.wordListItemDao().insertAll(
            listOf(com.wordmemo.data.entity.WordListItem(wordId = wordId, listId = listId))
        )

        val now = System.currentTimeMillis()
        learningRecordDao.insert(
            LearningRecord(
                wordId = wordId,
                listId = listId,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = now - 1000,
                reviewedAt = now
            )
        )

        assertTrue(learningUseCase.hasReviewDueWords(listId))
    }
}
