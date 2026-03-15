package com.wordmemo.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wordmemo.data.entity.Word
import kotlinx.coroutines.flow.Flow

/**
 * Word DAO - 单词数据访问对象
 * 提供单词的 CRUD 操作和查询功能
 */
@Dao
interface WordDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)

    @Update
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?

    @Query("SELECT * FROM words WHERE content = :content")
    suspend fun getWordByContent(content: String): Word?

    @Query("SELECT * FROM words ORDER BY content ASC")
    suspend fun getAllWords(): List<Word>

    @Query("SELECT * FROM words ORDER BY content ASC")
    fun getAllWordsFlow(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE difficulty = :difficulty ORDER BY content ASC")
    fun getWordsByDifficultyFlow(difficulty: Int): Flow<List<Word>>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Query("DELETE FROM words")
    suspend fun deleteAll()

    @Query("SELECT * FROM words WHERE content LIKE '%' || :keyword || '%' ORDER BY content ASC")
    suspend fun searchWords(keyword: String): List<Word>

    @Query("SELECT * FROM words WHERE content LIKE '%' || :query || '%'")
    fun searchWordsFlow(query: String): Flow<List<Word>>

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Long)
}
