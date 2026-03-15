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
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * 数据持久化集成测试
 * 测试数据库操作的完整性和一致性
 */
@RunWith(AndroidJUnit4::class)
class DataPersistenceIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var wordDao: WordDao
    private lateinit var wordListDao: WordListDao
    private lateinit var learningRecordDao: LearningRecordDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        wordDao = database.wordDao()
        wordListDao = database.wordListDao()
        learningRecordDao = database.learningRecordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testWordListPersistence() = runBlocking {
        // 1. 创建词库
        val wordList = WordList(
            name = "Persistence Test",
            description = "Test Description",
            wordCount = 100,
            type = "preset"
        )

        // 2. 保存词库
        val id = wordListDao.insert(wordList)
        assertTrue(id > 0)

        // 3. 从数据库读取
        val retrieved = wordListDao.getWordListById(id)
        assertNotNull(retrieved)
        assertEquals("Persistence Test", retrieved?.name)
        assertEquals(100, retrieved?.wordCount)

        // 4. 更新词库
        val updated = retrieved!!.copy(wordCount = 150)
        wordListDao.update(updated)

        // 5. 验证更新
        val updated2 = wordListDao.getWordListById(id)
        assertEquals(150, updated2?.wordCount)
    }

    @Test
    fun testWordPersistence() = runBlocking {
        // 1. 创建单词
        val word = Word(
            content = "persistence",
            translation = "持久化",
            phonetic = "/pərˈsɪstəns/",
            example = "Data persistence is important",
            difficulty = 3
        )

        // 2. 保存单词
        val id = wordDao.insert(word)
        assertTrue(id > 0)

        // 3. 从数据库读取
        val retrieved = wordDao.getWordById(id)
        assertNotNull(retrieved)
        assertEquals("persistence", retrieved?.content)
        assertEquals("持久化", retrieved?.translation)
        assertEquals(3, retrieved?.difficulty)

        // 4. 验证所有字段
        assertEquals("/pərˈsɪstəns/", retrieved?.phonetic)
        assertEquals("Data persistence is important", retrieved?.example)
    }

    @Test
    fun testLearningRecordPersistence() = runBlocking {
        // 1. 创建学习记录
        val now = System.currentTimeMillis()
        val record = LearningRecord(
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = now + 3 * 24 * 60 * 60 * 1000,
            reviewedAt = now
        )

        // 2. 保存记录
        val id = learningRecordDao.insert(record)
        assertTrue(id > 0)

        // 3. 从数据库读取
        val retrieved = learningRecordDao.getRecordById(id)
        assertNotNull(retrieved)
        assertEquals(1, retrieved?.wordId)
        assertEquals(4, retrieved?.quality)
        assertEquals(3, retrieved?.interval)
        assertEquals(2.6, retrieved?.easeFactor, 0.01)
    }

    @Test
    fun testTransactionIntegrity() = runBlocking {
        // 1. 创建词库
        val wordList = WordList(name = "Transaction Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        // 2. 创建多个单词
        val words = (1..5).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        // 3. 为每个单词创建学习记录
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

        // 4. 验证数据完整性
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(5, records.size)
        assertTrue(records.all { it.listId == listId })
    }

    @Test
    fun testCascadingDelete() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Delete Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val word = Word(content = "delete", translation = "删除", difficulty = 1)
        val wordId = wordDao.insert(word).toInt()

        // 2. 创建学习记录
        val record = LearningRecord(
            wordId = wordId,
            listId = listId,
            quality = 4,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )
        learningRecordDao.insert(record)

        // 3. 删除单词
        wordDao.deleteWordById(wordId.toLong())

        // 4. 验证单词已删除
        val deletedWord = wordDao.getWordById(wordId.toLong())
        assertNull(deletedWord)

        // 5. 验证学习记录仍然存在（因为没有外键约束）
        val records = learningRecordDao.getRecordsByListId(listId)
        assertEquals(1, records.size)
    }

    @Test
    fun testBulkOperations() = runBlocking {
        // 1. 批量创建词库
        val wordLists = (1..10).map { i ->
            WordList(name = "List$i", description = "Description$i", wordCount = i * 10)
        }
        wordListDao.insertAll(wordLists)

        // 2. 验证所有词库已保存
        val count = wordListDao.getWordListCount()
        assertEquals(10, count)

        // 3. 批量创建单词
        val words = (1..100).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = (i % 5) + 1)
        }
        wordDao.insertAll(words)

        // 4. 验证所有单词已保存
        val wordCount = wordDao.getWordCount()
        assertEquals(100, wordCount)
    }

    @Test
    fun testDataConsistency() = runBlocking {
        // 1. 创建词库和单词
        val wordList = WordList(name = "Consistency Test", description = "Test")
        val listId = wordListDao.insert(wordList).toInt()

        val words = (1..5).map { i ->
            Word(content = "word$i", translation = "单词$i", difficulty = 1)
        }
        wordDao.insertAll(words)

        // 2. 创建学习记录
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

        // 3. 验证数据一致性
        val records = learningRecordDao.getRecordsByListId(listId)
        val recordWordIds = records.map { it.wordId }.toSet()
        val wordIds = allWords.map { it.id }.toSet()

        assertEquals(recordWordIds, wordIds)
    }

    @Test
    fun testSearchAndFilter() = runBlocking {
        // 1. 创建多个单词
        val words = listOf(
            Word(content = "apple", translation = "苹果", difficulty = 1),
            Word(content = "application", translation = "应用", difficulty = 2),
            Word(content = "apply", translation = "应用", difficulty = 2),
            Word(content = "banana", translation = "香蕉", difficulty = 1),
            Word(content = "cherry", translation = "樱桃", difficulty = 3)
        )
        wordDao.insertAll(words)

        // 2. 搜索单词
        val results = wordDao.searchWords("app")
        assertEquals(3, results.size)
        assertTrue(results.all { it.content.contains("app") })

        // 3. 按难度过滤
        val easyWords = wordDao.getWordsByDifficultyFlow(1)
        assertNotNull(easyWords)
    }

    @Test
    fun testUpdateOperations() = runBlocking {
        // 1. 创建学习记录
        val record = LearningRecord(
            wordId = 1,
            listId = 1,
            quality = 2,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis(),
            reviewedAt = System.currentTimeMillis()
        )
        val id = learningRecordDao.insert(record)

        // 2. 更新记录
        val updated = record.copy(
            id = id.toInt(),
            quality = 5,
            interval = 7,
            easeFactor = 2.8
        )
        learningRecordDao.update(updated)

        // 3. 验证更新
        val retrieved = learningRecordDao.getRecordById(id)
        assertEquals(5, retrieved?.quality)
        assertEquals(7, retrieved?.interval)
        assertEquals(2.8, retrieved?.easeFactor, 0.01)
    }
}
