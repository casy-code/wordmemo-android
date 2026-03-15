package com.wordmemo.performance

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
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * 性能测试
 * 测试应用在大数据量下的性能表现
 */
@RunWith(AndroidJUnit4::class)
class PerformanceTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var wordDao: WordDao
    private lateinit var wordListDao: WordListDao
    private lateinit var learningRecordDao: LearningRecordDao
    private lateinit var learningManager: LearningManager

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
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testBulkInsertPerformance() = runBlocking {
        // 测试批量插入 1000 个单词的性能
        val words = (1..1000).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = (i % 5) + 1)
        }

        val startTime = System.currentTimeMillis()
        wordDao.insertAll(words)
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime
        println("Bulk insert 1000 words: ${duration}ms")

        // 验证性能：应该在 1 秒以内
        assertTrue(duration < 1000)

        // 验证数据完整性
        val count = wordDao.getWordCount()
        assertEquals(1000, count)
    }

    @Test
    fun testSearchPerformance() = runBlocking {
        // 准备数据：1000 个单词
        val words = (1..1000).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = (i % 5) + 1)
        }
        wordDao.insertAll(words)

        // 测试搜索性能
        val startTime = System.currentTimeMillis()
        val results = wordDao.searchWords("word1")
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime
        println("Search 1000 words: ${duration}ms")

        // 验证性能：应该在 500ms 以内
        assertTrue(duration < 500)

        // 验证搜索结果
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun testSM2AlgorithmPerformance() {
        // 测试 SM-2 算法的性能
        val iterations = 10000

        val startTime = System.currentTimeMillis()
        for (i in 0 until iterations) {
            SM2Algorithm.calculateNextReview(
                quality = (i % 6),
                currentInterval = (i % 10) + 1,
                currentEaseFactor = 2.5,
                currentReviewDate = System.currentTimeMillis()
            )
        }
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime
        val avgTime = duration.toDouble() / iterations
        println("SM-2 algorithm $iterations iterations: ${duration}ms (avg: ${avgTime}ms)")

        // 验证性能：平均每次计算应该在 0.1ms 以内
        assertTrue(avgTime < 0.1)
    }

    @Test
    fun testLearningRecordInsertPerformance() = runBlocking {
        // 创建词库和单词
        val wordList = WordList(name = "Performance Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..100).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        val allWords = wordDao.getAllWords()

        // 测试学习记录插入性能
        val startTime = System.currentTimeMillis()
        for (word in allWords) {
            val record = LearningRecord(
                wordId = word.id,
                listId = listId,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
            learningRecordDao.insert(record)
        }
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime
        println("Insert 100 learning records: ${duration}ms")

        // 验证性能：应该在 500ms 以内
        assertTrue(duration < 500)
    }

    @Test
    fun testQueryPerformance() = runBlocking {
        // 创建词库和学习记录
        val wordList = WordList(name = "Query Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..500).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        val allWords = wordDao.getAllWords()
        for (word in allWords) {
            val record = LearningRecord(
                wordId = word.id,
                listId = listId,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
            learningRecordDao.insert(record)
        }

        // 测试查询性能
        val startTime = System.currentTimeMillis()
        val records = learningRecordDao.getRecordsByListId(listId)
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime
        println("Query 500 learning records: ${duration}ms")

        // 验证性能：应该在 200ms 以内
        assertTrue(duration < 200)

        assertEquals(500, records.size)
    }

    @Test
    fun testMemoryUsageWithLargeDataset() = runBlocking {
        // 测试大数据集下的内存使用
        val runtime = Runtime.getRuntime()
        val memBefore = runtime.totalMemory() - runtime.freeMemory()

        // 创建 5000 个单词
        val words = (1..5000).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = (i % 5) + 1)
        }
        wordDao.insertAll(words)

        val memAfter = runtime.totalMemory() - runtime.freeMemory()
        val memUsed = (memAfter - memBefore) / (1024 * 1024) // 转换为 MB

        println("Memory used for 5000 words: ${memUsed}MB")

        // 验证内存使用：应该在 50MB 以内
        assertTrue(memUsed < 50)
    }

    @Test
    fun testConcurrentOperationsPerformance() = runBlocking {
        // 创建词库和单词
        val wordList = WordList(name = "Concurrent Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..200).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        val allWords = wordDao.getAllWords()

        // 测试并发操作性能
        val startTime = System.currentTimeMillis()
        for (word in allWords) {
            learningManager.recordLearningFeedback(word.id, listId, 4)
        }
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime
        println("Concurrent learning feedback for 200 words: ${duration}ms")

        // 验证性能：应该在 1 秒以内
        assertTrue(duration < 1000)
    }

    @Test
    fun testUpdatePerformance() = runBlocking {
        // 创建学习记录
        val records = (1..100).map { i ->
            LearningRecord(
                wordId = i,
                listId = 1,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewedAt = System.currentTimeMillis()
            )
        }

        for (record in records) {
            learningRecordDao.insert(record)
        }

        // 测试更新性能
        val startTime = System.currentTimeMillis()
        for (i in records.indices) {
            val updated = records[i].copy(quality = 5, interval = 7)
            learningRecordDao.update(updated)
        }
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime
        println("Update 100 learning records: ${duration}ms")

        // 验证性能：应该在 500ms 以内
        assertTrue(duration < 500)
    }
}
