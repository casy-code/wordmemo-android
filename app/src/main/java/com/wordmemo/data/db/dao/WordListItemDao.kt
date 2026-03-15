package com.wordmemo.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordmemo.data.entity.WordListItem

/**
 * WordListItem DAO - 词库-单词关联数据访问对象
 */
@Dao
interface WordListItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WordListItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WordListItem>)

    @Query("DELETE FROM word_list_items WHERE listId = :listId")
    suspend fun deleteByListId(listId: Int)

    @Query("DELETE FROM word_list_items")
    suspend fun deleteAll()
}
