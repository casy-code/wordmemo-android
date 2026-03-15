package com.wordmemo.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordmemo.data.entity.LearningRecord
import com.wordmemo.data.entity.Word
import com.wordmemo.data.entity.WordList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * AppDatabase 集成测试
 * 测试数据库的完整工作流
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=ON")
                }
            })
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testDatabaseCreation() {
        assertNotNull(database)
        assertNotNull(database.wordDao())
        assertNotNull(database.wordListDao())
        assertNotNull(database.learningRecordDao())
    }

    @Test
    fun testCompleteWorkflow() = runBlocking {
        val wordDao = database.wordDao()
        val wordListDao = database.wordListDao()
        val learningRecordDao = database.learningRecordDao()

        // 1. 创建词库
        val wordList = WordList(
            name = "四级词汇",
            description = "大学英语四级词汇",
            type = "preset"
        )
        val listId = wordListDao.insert(wordList).toInt()
        assertTrue(listId > 0)

        // 2. 创建单词
        val words = listOf(
            Word(content = "abandon", translation = "放弃", difficulty = 2),
            Word(content = "ability", translation = "能力", difficulty = 1),
            Word(content = "able", translation = "能够", difficulty = 1)
        )
        wordDao.insertAll(words)
        assertEquals(3, wordDao.getWordCount())

        // 3. 获取单词 ID
        val word1 = wordDao.getWordByContent("abandon")
        assertNotNull(word1)
        val word2 = wordDao.getWordByContent("ability")
        assertNotNull(word2)
        val word3 = wordDao.getWordByContent("able")
        assertNotNull(word3)

        // 4. 创建学习记录（2 个未来复习，1 个今日到期）
        val now = System.currentTimeMillis()
        val tomorrow = now + 86400000
        val listIdLong = listId.toLong()
        val records = listOf(
            LearningRecord(
                wordId = requireNotNull(word1).id,
                listId = listId,
                quality = 4,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = tomorrow
            ),
            LearningRecord(
                wordId = requireNotNull(word2).id,
                listId = listId,
                quality = 3,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = tomorrow
            ),
            LearningRecord(
                wordId = requireNotNull(word3).id,
                listId = listId,
                quality = 0,
                interval = 0,
                easeFactor = 2.5,
                nextReviewDate = now - 1000  // 明确设为过去，确保被 getDueRecords 命中
            )
        )
        learningRecordDao.insertAll(records)
        assertEquals(3, learningRecordDao.getRecordCount(listIdLong))

        // 5. 查询待复习单词（仅 nextReviewDate <= now 的）
        val dueRecords = learningRecordDao.getDueRecords(listIdLong, now)
        assertEquals(1, dueRecords.size)

        // 6. 更新学习记录（获取插入后的 id 再更新）
        val recordToUpdate = learningRecordDao.getRecordByWordAndList(requireNotNull(word3).id, listId)
            ?: error("待更新记录不存在")
        val updatedRecord = recordToUpdate.copy(
            quality = 4,
            interval = 1,
            nextReviewDate = now + 86400000
        )
        learningRecordDao.update(updatedRecord)

        // 7. 查询今日复习数
        val startOfDay = now - (now % 86400000)
        val endOfDay = startOfDay + 86400000
        val todayCount = learningRecordDao.getTodayReviewCount(listId.toLong(), startOfDay, endOfDay)
        assertEquals(1, todayCount)

        // 8. 验证数据完整性
        val allWords = wordDao.getAllWords()
        assertEquals(3, allWords.size)

        val allLists = wordListDao.getAllWordLists()
        assertEquals(1, allLists.size)

        val allRecords = learningRecordDao.getRecentRecords(listIdLong, 10)
        assertEquals(3, allRecords.size)
    }

    @Test
    fun testMultipleWordLists() = runBlocking {
        val wordDao = database.wordDao()
        val wordListDao = database.wordListDao()
        val learningRecordDao = database.learningRecordDao()

        // 创建多个词库
        val lists = listOf(
            WordList(name = "四级词汇", type = "preset"),
            WordList(name = "六级词汇", type = "preset"),
            WordList(name = "我的词库", type = "custom")
        )
        wordListDao.insertAll(lists)

        // 创建单词
        val words = listOf(
            Word(content = "abandon", translation = "放弃"),
            Word(content = "ability", translation = "能力")
        )
        wordDao.insertAll(words)

        // 获取词库 ID
        val list1 = requireNotNull(wordListDao.getWordListByName("四级词汇"))
        val list2 = requireNotNull(wordListDao.getWordListByName("六级词汇"))

        // 为不同词库创建学习记录
        val word1 = requireNotNull(wordDao.getWordByContent("abandon"))
        val word2 = requireNotNull(wordDao.getWordByContent("ability"))

        val records = listOf(
            LearningRecord(wordId = word1.id, listId = list1.id, quality = 4, nextReviewDate = System.currentTimeMillis()),
            LearningRecord(wordId = word2.id, listId = list1.id, quality = 3, nextReviewDate = System.currentTimeMillis()),
            LearningRecord(wordId = word1.id, listId = list2.id, quality = 2, nextReviewDate = System.currentTimeMillis())
        )
        learningRecordDao.insertAll(records)

        // 验证每个词库的学习记录数
        val id1 = list1.id.toLong()
        val id2 = list2.id.toLong()
        assertEquals(2, learningRecordDao.getRecordCount(id1))
        assertEquals(1, learningRecordDao.getRecordCount(id2))

        // 验证预设词库数量
        val presetLists = wordListDao.getWordListsByType("preset")
        assertEquals(2, presetLists.size)

        val customLists = wordListDao.getWordListsByType("custom")
        assertEquals(1, customLists.size)
    }

    @Test
    fun testCascadeDelete() = runBlocking {
        val wordDao = database.wordDao()
        val wordListDao = database.wordListDao()
        val learningRecordDao = database.learningRecordDao()

        // 创建词库和单词
        val wordList = WordList(name = "四级词汇", type = "preset")
        val listId = wordListDao.insert(wordList)

        val word = Word(content = "abandon", translation = "放弃")
        val wordId = wordDao.insert(word)

        // 创建学习记录
        val record = LearningRecord(
            wordId = wordId.toInt(),
            listId = listId.toInt(),
            quality = 4,
            nextReviewDate = System.currentTimeMillis()
        )
        learningRecordDao.insert(record)

        // 验证数据存在
        assertEquals(1, learningRecordDao.getRecordCount(listId))

        // 删除词库
        wordListDao.delete(wordList.copy(id = listId.toInt()))

        // 验证级联删除
        assertEquals(0, learningRecordDao.getRecordCount(listId))
    }
}
