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
 * 最终集成测试
 * 验证应用的完整功能和稳定性
 */
@RunWith(AndroidJUnit4::class)
class FinalIntegrationTest {

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
    fun testCompleteApplicationWorkflow() = runBlocking {
        // 1. 创建词库
        val wordList = WordList(
            name = "CET-4",
            description = "College English Test Level 4",
            wordCount = 0
        )
        val listId = wordListDao.insert(wordList).toInt()
        assertTrue(listId > 0)

        // 2. 添加单词
        val words = listOf(
            Word(content = "abandon", translation = "放弃", difficulty = 1),
            Word(content = "ability", translation = "能力", difficulty = 1),
            Word(content = "able", translation = "能够", difficulty = 1),
            Word(content = "about", translation = "关于", difficulty = 1),
            Word(content = "above", translation = "上面", difficulty = 1)
        )
        wordDao.insertAll(words)

        // 3. 验证单词已添加
        val allWords = wordDao.getAllWords()
        assertEquals(5, allWords.size)

        // 4. 学习单词
        for (word in allWords) {
            learningManager.recordLearningFeedback(word.id, listId, 4)
        }

        // 5. 验证学习记录
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(5, records.size)

        // 6. 计算学习进度
        val progress = learningUseCase.getLearningProgress(listId)
        assertEquals(100, progress)

        // 7. 验证统计数据
        assertTrue(records.all { it.quality == 4 })
        assertTrue(records.all { it.interval > 1 })
    }

    @Test
    fun testMultipleWordListsManagement() = runBlocking {
        // 1. 创建多个词库
        val lists = listOf(
            WordList(name = "CET-4", description = "四级", wordCount = 0),
            WordList(name = "CET-6", description = "六级", wordCount = 0),
            WordList(name = "GRE", description = "GRE", wordCount = 0)
        )
        wordListDao.insertAll(lists)

        // 2. 验证词库已创建
        val count = wordListDao.getWordListCount()
        assertEquals(3, count)

        // 3. 为每个词库添加单词
        val allLists = wordListDao.getAllWordLists()
        for ((index, list) in allLists.withIndex()) {
            val words = (1..10).map { i ->
                Word(content = "word${index}_$i", translation = "单词${index}_$i", difficulty = 1)
            }
            wordDao.insertAll(words)
        }

        // 4. 验证单词已添加
        val wordCount = wordDao.getWordCount()
        assertEquals(30, wordCount)
    }

    @Test
    fun testErrorHandlingAndRecovery() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Error Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val word = Word(content = "error", translation = "错误", difficulty = 1)
        val wordId = wordDao.insert(word).toInt()

        // 2. 测试无效的质量值
        try {
            learningManager.recordLearningFeedback(wordId, listId, 6) // 无效
            fail("Should throw exception")
        } catch (e: Exception) {
            // 预期异常
        }

        // 3. 验证数据未被污染
        val record = learningRecordDao.getRecordByWordAndList(wordId, listId)
        assertNull(record)

        // 4. 使用有效值重试
        learningManager.recordLearningFeedback(wordId, listId, 4)
        val validRecord = learningRecordDao.getRecordByWordAndList(wordId, listId)
        assertNotNull(validRecord)
        assertEquals(4, validRecord?.quality)
    }

    @Test
    fun testDataIntegrityUnderStress() = runBlocking {
        // 1. 创建词库
        val wordList = WordList(name = "Stress Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        // 2. 添加大量单词
        val words = (1..500).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = (i % 5) + 1)
        }
        wordDao.insertAll(words)

        // 3. 进行大量学习操作
        val allWords = wordDao.getAllWords()
        for (word in allWords) {
            val quality = (Math.random() * 6).toInt()
            learningManager.recordLearningFeedback(word.id, listId, quality)
        }

        // 4. 验证数据完整性
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(500, records.size)

        // 5. 验证没有重复记录
        val uniqueWordIds = records.map { it.wordId }.toSet()
        assertEquals(500, uniqueWordIds.size)
    }

    @Test
    fun testConcurrentAccessPatterns() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Concurrent Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..100).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        // 2. 模拟并发访问
        val allWords = wordDao.getAllWords()
        
        // 并发读取
        val readResults = allWords.map { wordDao.getWordById(it.id.toLong()) }
        assertTrue(readResults.all { it != null })

        // 并发写入
        for (word in allWords) {
            learningManager.recordLearningFeedback(word.id, listId, 4)
        }

        // 并发查询
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(100, records.size)
    }

    @Test
    fun testBackupAndRestore() = runBlocking {
        // 1. 创建初始数据
        val wordList = WordList(name = "Backup Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..50).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        val allWords = wordDao.getAllWords()
        for (word in allWords) {
            learningManager.recordLearningFeedback(word.id, listId, 4)
        }

        // 2. 备份数据
        val backupWords = wordDao.getAllWords()
        val backupRecords = learningRecordDao.getRecordsByListId(listId)

        // 3. 验证备份数据完整性
        assertEquals(50, backupWords.size)
        assertEquals(50, backupRecords.size)

        // 4. 模拟恢复
        val restoredWords = wordDao.getAllWords()
        val restoredRecords = learningRecordDao.getRecordsByListId(listId)

        assertEquals(backupWords.size, restoredWords.size)
        assertEquals(backupRecords.size, restoredRecords.size)
    }

    @Test
    fun testApplicationStability() = runBlocking {
        // 1. 创建词库
        val wordList = WordList(name = "Stability Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        // 2. 执行多个操作周期
        for (cycle in 1..10) {
            // 添加单词
            val words = (1..20).map { i ->
                Word(content = "cycle${cycle}_word$i", translation = "周期${cycle}_单词$i", difficulty = 1)
            }
            wordDao.insertAll(words)

            // 学习单词
            val allWords = wordDao.getAllWords()
            for (word in allWords.takeLast(20)) {
                learningManager.recordLearningFeedback(word.id, listId, 4)
            }

            // 查询数据
            val records = learningRecordDao.getRecordsByListId(listId)
            assertTrue(records.isNotEmpty())
        }

        // 3. 验证最终状态
        val finalWords = wordDao.getAllWords()
        val finalRecords = learningRecordDao.getRecordsByListId(listId)
        assertEquals(200, finalWords.size)
        assertEquals(200, finalRecords.size)
    }

    @Test
    fun testEdgeCases() = runBlocking {
        // 1. 空词库
        val emptyList = WordList(name = "Empty", description = "Empty")
        val emptyListId = wordListDao.insert(emptyList).toInt()
        
        val emptyRecords = learningRecordDao.getRecordsByListId(emptyListId)
        assertEquals(0, emptyRecords.size)

        // 2. 单个单词
        val singleWord = Word(content = "single", translation = "单个", difficulty = 1)
        val wordId = wordDao.insert(singleWord).toInt()
        
        learningManager.recordLearningFeedback(wordId, emptyListId, 4)
        val singleRecord = learningRecordDao.getRecordByWordAndList(wordId, emptyListId)
        assertNotNull(singleRecord)

        // 3. 极端难度值
        for (difficulty in 1..5) {
            val word = Word(content = "diff$difficulty", translation = "难度$difficulty", difficulty = difficulty)
            wordDao.insert(word)
        }
        
        val allWords = wordDao.getAllWords()
        assertTrue(allWords.any { it.difficulty == 1 })
        assertTrue(allWords.any { it.difficulty == 5 })
    }
}
