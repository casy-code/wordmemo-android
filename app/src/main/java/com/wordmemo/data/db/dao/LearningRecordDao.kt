package com.wordmemo.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wordmemo.data.entity.LearningRecord
import kotlinx.coroutines.flow.Flow

/**
 * LearningRecord DAO - 学习记录数据访问对象
 * 提供学习记录的 CRUD 操作和学习统计查询
 */
@Dao
interface LearningRecordDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: LearningRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<LearningRecord>)

    @Update
    suspend fun update(record: LearningRecord)

    @Delete
    suspend fun delete(record: LearningRecord)

    @Query("SELECT * FROM learning_records WHERE id = :id")
    suspend fun getRecordById(id: Long): LearningRecord?

    @Query("SELECT * FROM learning_records WHERE wordId = :wordId AND listId = :listId ORDER BY reviewedAt DESC LIMIT 1")
    suspend fun getLatestRecord(wordId: Long, listId: Long): LearningRecord?

    @Query("SELECT * FROM learning_records WHERE listId = :listId AND nextReviewDate <= :currentDate ORDER BY nextReviewDate ASC")
    suspend fun getDueRecords(listId: Long, currentDate: Long): List<LearningRecord>

    @Query("SELECT * FROM learning_records WHERE listId = :listId AND nextReviewDate <= :currentDate ORDER BY nextReviewDate ASC")
    fun getDueRecordsFlow(listId: Long, currentDate: Long): Flow<List<LearningRecord>>

    @Query("SELECT COUNT(*) FROM learning_records WHERE listId = :listId AND reviewedAt >= :startDate AND reviewedAt < :endDate")
    suspend fun getTodayReviewCount(listId: Long, startDate: Long, endDate: Long): Int

    @Query("SELECT COUNT(*) FROM learning_records WHERE listId = :listId AND nextReviewDate <= :currentDate")
    suspend fun getDueCount(listId: Long, currentDate: Long): Int

    @Query("SELECT * FROM learning_records WHERE listId = :listId ORDER BY reviewedAt DESC")
    fun getAllRecordsByListFlow(listId: Long): Flow<List<LearningRecord>>

    @Query("SELECT * FROM learning_records WHERE listId = :listId ORDER BY reviewedAt DESC LIMIT :limit")
    suspend fun getRecentRecords(listId: Long, limit: Int): List<LearningRecord>

    @Query("DELETE FROM learning_records WHERE listId = :listId")
    suspend fun deleteByListId(listId: Long)

    @Query("DELETE FROM learning_records")
    suspend fun deleteAll()

    @Query("SELECT COUNT(DISTINCT DATE(reviewedAt / 1000, 'unixepoch')) FROM learning_records WHERE listId = :listId")
    suspend fun getConsecutiveLearningDays(listId: Long): Int

    @Query("SELECT COUNT(*) FROM learning_records WHERE listId = :listId")
    suspend fun getRecordCount(listId: Long): Int
}
