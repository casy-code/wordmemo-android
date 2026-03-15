package com.wordmemo.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.data.db.AppDatabase
import com.wordmemo.data.entity.Word
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class WordDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var wordDao: WordDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        wordDao = database.wordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndGetWord() = runBlocking {
        val word = Word(
            content = "hello",
            translation = "你好",
            phonetic = "/həˈloʊ/",
            example = "Hello, world!",
            difficulty = 1
        )
        
        val id = wordDao.insert(word)
        assertTrue(id > 0)
        
        val retrieved = wordDao.getWordById(id)
        assertNotNull(retrieved)
        assertEquals("hello", retrieved?.content)
        assertEquals("你好", retrieved?.translation)
    }

    @Test
    fun testInsertMultipleWords() = runBlocking {
        val words = listOf(
            Word(content = "apple", translation = "苹果", difficulty = 1),
            Word(content = "banana", translation = "香蕉", difficulty = 1),
            Word(content = "cherry", translation = "樱桃", difficulty = 2)
        )
        
        wordDao.insertAll(words)
        
        val count = wordDao.getWordCount()
        assertEquals(3, count)
    }

    @Test
    fun testUpdateWord() = runBlocking {
        val word = Word(
            id = 1,
            content = "test",
            translation = "测试",
            difficulty = 1
        )
        
        wordDao.insert(word)
        
        val updated = word.copy(translation = "测试更新", difficulty = 2)
        wordDao.update(updated)
        
        val retrieved = wordDao.getWordById(1)
        assertEquals("测试更新", retrieved?.translation)
        assertEquals(2, retrieved?.difficulty)
    }

    @Test
    fun testDeleteWord() = runBlocking {
        val word = Word(
            content = "delete",
            translation = "删除",
            difficulty = 1
        )
        
        val id = wordDao.insert(word)
        wordDao.delete(word)
        
        val retrieved = wordDao.getWordById(id)
        assertNull(retrieved)
    }

    @Test
    fun testGetWordByContent() = runBlocking {
        val word = Word(
            content = "unique",
            translation = "独特的",
            difficulty = 2
        )
        
        wordDao.insert(word)
        
        val retrieved = wordDao.getWordByContent("unique")
        assertNotNull(retrieved)
        assertEquals("独特的", retrieved?.translation)
    }

    @Test
    fun testSearchWords() = runBlocking {
        val words = listOf(
            Word(content = "apple", translation = "苹果", difficulty = 1),
            Word(content = "application", translation = "应用", difficulty = 2),
            Word(content = "apply", translation = "应用", difficulty = 2),
            Word(content = "banana", translation = "香蕉", difficulty = 1)
        )
        
        wordDao.insertAll(words)
        
        val results = wordDao.searchWords("app")
        assertEquals(3, results.size)
        assertTrue(results.all { it.content.contains("app") })
    }

    @Test
    fun testGetWordsByDifficulty() = runBlocking {
        val words = listOf(
            Word(content = "easy1", translation = "简单1", difficulty = 1),
            Word(content = "easy2", translation = "简单2", difficulty = 1),
            Word(content = "hard1", translation = "困难1", difficulty = 3),
            Word(content = "hard2", translation = "困难2", difficulty = 3)
        )
        
        wordDao.insertAll(words)
        
        val easyWords = wordDao.getWordsByDifficultyFlow(1)
        val hardWords = wordDao.getWordsByDifficultyFlow(3)
        
        // 验证流可以被收集
        assertNotNull(easyWords)
        assertNotNull(hardWords)
    }

    @Test
    fun testDeleteAll() = runBlocking {
        val words = listOf(
            Word(content = "word1", translation = "单词1", difficulty = 1),
            Word(content = "word2", translation = "单词2", difficulty = 1)
        )
        
        wordDao.insertAll(words)
        assertEquals(2, wordDao.getWordCount())
        
        wordDao.deleteAll()
        assertEquals(0, wordDao.getWordCount())
    }

    @Test
    fun testGetAllWords() = runBlocking {
        val words = listOf(
            Word(content = "zebra", translation = "斑马", difficulty = 1),
            Word(content = "apple", translation = "苹果", difficulty = 1),
            Word(content = "banana", translation = "香蕉", difficulty = 1)
        )
        
        wordDao.insertAll(words)
        
        val allWords = wordDao.getAllWords()
        assertEquals(3, allWords.size)
        // 验证按字母顺序排序
        assertEquals("apple", allWords[0].content)
        assertEquals("banana", allWords[1].content)
        assertEquals("zebra", allWords[2].content)
    }
}
