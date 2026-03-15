package com.wordmemo.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wordmemo.data.entity.WordList
import kotlinx.coroutines.flow.Flow

/**
 * WordList DAO - 词库数据访问对象
 * 提供词库的 CRUD 操作和查询功能
 */
@Dao
interface WordListDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wordList: WordList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(wordLists: List<WordList>)

    @Update
    suspend fun update(wordList: WordList)

    @Delete
    suspend fun delete(wordList: WordList)

    @Query("SELECT * FROM word_lists WHERE id = :id")
    suspend fun getWordListById(id: Long): WordList?

    @Query("SELECT * FROM word_lists WHERE name = :name")
    suspend fun getWordListByName(name: String): WordList?

    @Query("SELECT * FROM word_lists ORDER BY createdAt DESC")
    suspend fun getAllWordLists(): List<WordList>

    @Query("SELECT * FROM word_lists ORDER BY createdAt DESC")
    fun getAllWordListsFlow(): Flow<List<WordList>>

    @Query("SELECT * FROM word_lists WHERE type = :type ORDER BY createdAt DESC")
    suspend fun getWordListsByType(type: String): List<WordList>

    @Query("SELECT * FROM word_lists WHERE type = :type ORDER BY createdAt DESC")
    fun getWordListsByTypeFlow(type: String): Flow<List<WordList>>

    @Query("SELECT COUNT(*) FROM word_lists")
    suspend fun getWordListCount(): Int

    @Query("SELECT COUNT(*) FROM word_lists WHERE type = :type")
    suspend fun getWordListCountByType(type: String): Int

    @Query("DELETE FROM word_lists")
    suspend fun deleteAll()
}
