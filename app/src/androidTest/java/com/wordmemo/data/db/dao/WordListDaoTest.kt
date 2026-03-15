package com.wordmemo.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.data.db.AppDatabase
import com.wordmemo.data.entity.WordList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class WordListDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var wordListDao: WordListDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        wordListDao = database.wordListDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndGetWordList() = runBlocking {
        val wordList = WordList(
            name = "CET-4",
            description = "大学英语四级词汇",
            type = "preset"
        )
        
        val id = wordListDao.insert(wordList)
        assertTrue(id > 0)
        
        val retrieved = wordListDao.getWordListById(id)
        assertNotNull(retrieved)
        assertEquals("CET-4", retrieved?.name)
        assertEquals("大学英语四级词汇", retrieved?.description)
    }

    @Test
    fun testInsertMultipleWordLists() = runBlocking {
        val lists = listOf(
            WordList(name = "CET-4", description = "四级"),
            WordList(name = "CET-6", description = "六级"),
            WordList(name = "GRE", description = "GRE")
        )
        
        wordListDao.insertAll(lists)
        
        val count = wordListDao.getWordListCount()
        assertEquals(3, count)
    }

    @Test
    fun testUpdateWordList() = runBlocking {
        val wordList = WordList(name = "CET-4", description = "四级")
        val id = wordListDao.insert(wordList)
        
        val updated = wordList.copy(id = id.toInt(), description = "四级词汇（已更新）")
        wordListDao.update(updated)
        
        val retrieved = wordListDao.getWordListById(id)
        assertEquals("四级词汇（已更新）", retrieved?.description)
    }

    @Test
    fun testDeleteWordList() = runBlocking {
        val wordList = WordList(
            name = "Temp",
            description = "临时词库"
        )
        
        val id = wordListDao.insert(wordList)
        wordListDao.delete(wordList.copy(id = id.toInt()))
        
        val retrieved = wordListDao.getWordListById(id)
        assertNull(retrieved)
    }

    @Test
    fun testGetWordListByName() = runBlocking {
        val wordList = WordList(
            name = "TOEFL",
            description = "托福词汇"
        )
        
        wordListDao.insert(wordList)
        
        val retrieved = wordListDao.getWordListByName("TOEFL")
        assertNotNull(retrieved)
        assertEquals("托福词汇", retrieved?.description)
    }

    @Test
    fun testGetAllWordLists() = runBlocking {
        val lists = listOf(
            WordList(name = "List1", description = "描述1"),
            WordList(name = "List2", description = "描述2"),
            WordList(name = "List3", description = "描述3")
        )
        
        wordListDao.insertAll(lists)
        
        val allLists = wordListDao.getAllWordLists()
        assertEquals(3, allLists.size)
    }

    @Test
    fun testDeleteAll() = runBlocking {
        val lists = listOf(
            WordList(name = "List1", description = "描述1"),
            WordList(name = "List2", description = "描述2")
        )
        
        wordListDao.insertAll(lists)
        assertEquals(2, wordListDao.getWordListCount())
        
        wordListDao.deleteAll()
        assertEquals(0, wordListDao.getWordListCount())
    }

    @Test
    fun testGetWordListsFlow() = runBlocking {
        val lists = listOf(
            WordList(name = "List1", description = "描述1"),
            WordList(name = "List2", description = "描述2")
        )
        
        wordListDao.insertAll(lists)
        
        val flow = wordListDao.getAllWordListsFlow()
        assertNotNull(flow)
    }
}
