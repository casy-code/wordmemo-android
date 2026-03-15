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
import com.wordmemo.domain.model.SM2Algorithm
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
 * 学习流程集成测试
 * 测试从单词加载、学习反馈、数据持久化的完整流程
 */
@RunWith(AndroidJUnit4::class)
class LearningFlowIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var wordDao: WordDao
    private lateinit var wordListDao: WordListDao
    private lateinit var learningRecordDao: LearningRecordDao
    private lateinit var learningManager: LearningManager
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
        learningManager = LearningManager(wordDao, learningRecordDao)
        learningUseCase = LearningUseCase(learningManager, wordDao, wordListDao, learningRecordDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCompleteLearnFlow() = runBlocking {
        // 1. 创建词库
        val wordList = WordList(
            name = "Test List",
            description = "Test Description"
        )
        val listId = wordListDao.insert(wordList).toInt()

        // 2. 添加单词并关联到词库
        val words = listOf(
            Word(content = "hello", translation = "你好", difficulty = 1),
            Word(content = "world", translation = "世界", difficulty = 1),
            Word(content = "test", translation = "测试", difficulty = 2)
        )
        wordDao.insertAll(words)

        // 3. 将单词关联到词库
        val allWords = wordDao.getAllWords()
        assertEquals(3, allWords.size)
        database.wordListItemDao().insertAll(
            allWords.map { com.wordmemo.data.entity.WordListItem(wordId = it.id, listId = listId) }
        )

        // 4. 记录学习反馈
        val word1 = allWords[0]
        learningManager.recordLearningFeedback(word1.id, listId, 4)

        // 5. 验证学习记录已保存
        val record = learningRecordDao.getRecordByWordAndList(word1.id, listId)
        assertNotNull(record)
        assertEquals(4, record?.quality)

        // 6. 验证间隔已更新
        assertTrue(record!!.interval > 1)
        assertTrue(record.nextReviewDate > System.currentTimeMillis())

        // 7. 继续学习其他单词
        val word2 = allWords[1]
        learningManager.recordLearningFeedback(word2.id, listId, 3)

        val record2 = learningRecordDao.getRecordByWordAndList(word2.id, listId)
        assertNotNull(record2)
        assertEquals(3, record2?.quality)

        // 8. 验证学习进度
        val progress = learningUseCase.getLearningProgress(listId)
        assertEquals(66, progress) // 2/3 单词已学习
    }

    @Test
    fun testDataPersistenceAcrossOperations() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Persistence Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val word = Word(content = "persist", translation = "持久化", difficulty = 1)
        val wordId = wordDao.insert(word).toInt()

        // 2. 记录学习反馈
        learningManager.recordLearningFeedback(wordId, listId, 5)

        // 3. 获取记录
        val record1 = learningRecordDao.getRecordByWordAndList(wordId, listId)
        assertNotNull(record1)
        val originalInterval = record1!!.interval
        val originalEaseFactor = record1.easeFactor

        // 4. 再次学习同一单词
        learningManager.recordLearningFeedback(wordId, listId, 4)

        // 5. 验证数据已更新
        val record2 = learningRecordDao.getRecordByWordAndList(wordId, listId)
        assertNotNull(record2)
        assertTrue(record2!!.interval > originalInterval)
        assertTrue(record2.easeFactor > originalEaseFactor)

        // 6. 验证历史数据完整性
        val allRecords = learningRecordDao.getRecordsByListId(listId)
        assertEquals(1, allRecords.size)
    }

    @Test
    fun testMultipleWordsLearningFlow() = runBlocking {
        // 1. 创建词库
        val wordList = WordList(name = "Multi Words", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        // 2. 添加多个单词
        val words = (1..10).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = (i % 3) + 1)
        }
        wordDao.insertAll(words)

        // 3. 学习所有单词
        val allWords = wordDao.getAllWords()
        for ((index, word) in allWords.withIndex()) {
            val quality = (index % 6) // 0-5
            learningManager.recordLearningFeedback(word.id, listId, quality)
        }

        // 4. 验证所有记录已保存
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(10, records.size)

        // 5. 验证不同质量的记录
        val qualityMap = records.groupBy { it.quality }
        assertTrue(qualityMap.size > 1) // 至少有多个不同的质量
    }

    @Test
    fun testReviewDueWordsFlow() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Review Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = listOf(
            Word(content = "review1", translation = "复习1", difficulty = 1),
            Word(content = "review2", translation = "复习2", difficulty = 1),
            Word(content = "review3", translation = "复习3", difficulty = 1)
        )
        wordDao.insertAll(words)

        // 2. 创建学习记录，其中一些已过期
        val now = System.currentTimeMillis()
        val allWords = wordDao.getAllWords()

        // 第一个单词：已过期
        val record1 = LearningRecord(
            wordId = allWords[0].id,
            listId = listId,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = now - 24 * 60 * 60 * 1000, // 1 天前
            reviewedAt = now
        )
        learningRecordDao.insert(record1)

        // 第二个单词：未过期
        val record2 = LearningRecord(
            wordId = allWords[1].id,
            listId = listId,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = now + 24 * 60 * 60 * 1000, // 1 天后
            reviewedAt = now
        )
        learningRecordDao.insert(record2)

        // 第三个单词：新单词，无记录
        // 不添加记录

        // 3. 验证复习流程
        val reviewDueWords = learningManager.getReviewDueWords(listId)
        assertTrue(reviewDueWords.isNotEmpty())
    }

    @Test
    fun testFailureAndRecoveryFlow() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Failure Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val word = Word(content = "failure", translation = "失败", difficulty = 1)
        val wordId = wordDao.insert(word).toInt()

        // 2. 第一次学习：成功
        learningManager.recordLearningFeedback(wordId, listId, 4)
        var record = learningRecordDao.getRecordByWordAndList(wordId, listId)
        val interval1 = record!!.interval
        val easeFactor1 = record.easeFactor

        // 3. 第二次学习：失败
        learningManager.recordLearningFeedback(wordId, listId, 0)
        record = learningRecordDao.getRecordByWordAndList(wordId, listId)

        // 4. 验证失败后的恢复
        assertEquals(1, record!!.interval) // 间隔重置为 1
        assertEquals(easeFactor1, record.easeFactor, 0.01) // 易度因子保持不变

        // 5. 第三次学习：恢复
        learningManager.recordLearningFeedback(wordId, listId, 4)
        record = learningRecordDao.getRecordByWordAndList(wordId, listId)

        // 6. 验证恢复后的进度
        assertTrue(record!!.interval > 1)
        assertTrue(record.easeFactor > easeFactor1)
    }

    @Test
    fun testConcurrentLearningOperations() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Concurrent Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..5).map { i ->
            Word(content = "concurrent$i", translation = "并发$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        // 2. 并发学习多个单词
        val allWords = wordDao.getAllWords()
        for (word in allWords) {
            learningManager.recordLearningFeedback(word.id, listId, 4)
        }

        // 3. 验证所有操作都成功
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(5, records.size)
        assertTrue(records.all { it.quality == 4 })
    }

    @Test
    fun testStatisticsCalculation() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Stats Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..10).map { i ->
            Word(content = "stats$i", translation = "统计$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        // 2. 学习一些单词
        val allWords = wordDao.getAllWords()
        for (i in 0 until 7) {
            learningManager.recordLearningFeedback(allWords[i].id, listId, 4)
        }

        // 3. 计算学习进度
        val progress = learningUseCase.getLearningProgress(listId)
        assertEquals(70, progress) // 7/10 = 70%

        // 4. 验证统计数据
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(7, records.size)
        assertTrue(records.all { it.quality >= 3 })
    }
}
