package com.wordmemo.domain.usecase

import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.entity.LearningRecord
import com.wordmemo.data.entity.Word
import com.wordmemo.domain.model.SM2Algorithm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 学习流程管理器
 * 
 * 负责：
 * - 获取今日学习的单词
 * - 获取需要复习的单词
 * - 记录学习反馈
 * - 计算学习统计
 */
class LearningManager(
    private val wordDao: WordDao,
    private val learningRecordDao: LearningRecordDao
) {
    /**
     * 获取需要复习的单词列表
     * 
     * @param listId 词库 ID
     * @return 需要复习的单词列表
     */
    suspend fun getReviewDueWords(listId: Int): List<Word> {
        val currentTime = System.currentTimeMillis()
        val records = learningRecordDao.getDueRecords(listId.toLong(), currentTime)
        return records.mapNotNull { record ->
            wordDao.getWordById(record.wordId.toLong())
        }
    }

    /**
     * 获取今日学习的单词数量
     * 
     * @param listId 词库 ID
     * @return 今日学习数量
     */
    fun getTodayLearningCount(listId: Int): Flow<Int> {
        return learningRecordDao.getTodayLearningCount(listId.toLong())
    }

    /**
     * 获取今日复习的单词数量
     * 
     * @param listId 词库 ID
     * @return 今日复习数量
     */
    fun getTodayReviewCount(listId: Int): Flow<Int> {
        return learningRecordDao.getTodayReviewCountFlow(listId.toLong())
    }

    /**
     * 记录学习反馈并更新复习计划
     * 
     * @param wordId 单词 ID
     * @param listId 词库 ID
     * @param quality 用户反馈评分 (0-5)
     */
    suspend fun recordLearningFeedback(
        wordId: Int,
        listId: Int,
        quality: Int
    ) {
        // 获取或创建学习记录
        val existingRecord = learningRecordDao.getRecordByWordAndList(wordId, listId)
        
        val newRecord = if (existingRecord != null) {
            // 更新现有记录
            val sm2Result = SM2Algorithm.calculateNextReview(
                quality = quality,
                currentInterval = existingRecord.interval,
                currentEaseFactor = existingRecord.easeFactor,
                currentReviewDate = System.currentTimeMillis()
            )
            
            existingRecord.copy(
                quality = quality,
                interval = sm2Result.nextInterval,
                easeFactor = sm2Result.nextEaseFactor,
                nextReviewDate = sm2Result.nextReviewDate,
                reviewedAt = System.currentTimeMillis()
            )
        } else {
            // 创建新记录
            val (initialInterval, initialEaseFactor) = SM2Algorithm.initializeNewWord()
            val sm2Result = SM2Algorithm.calculateNextReview(
                quality = quality,
                currentInterval = initialInterval,
                currentEaseFactor = initialEaseFactor,
                currentReviewDate = System.currentTimeMillis()
            )
            
            LearningRecord(
                wordId = wordId,
                listId = listId,
                quality = quality,
                interval = sm2Result.nextInterval,
                easeFactor = sm2Result.nextEaseFactor,
                nextReviewDate = sm2Result.nextReviewDate,
                reviewedAt = System.currentTimeMillis()
            )
        }
        
        // 保存或更新记录
        if (existingRecord != null) {
            learningRecordDao.update(newRecord)
        } else {
            learningRecordDao.insert(newRecord)
        }
    }

    /**
     * 获取单词的学习记录
     * 
     * @param wordId 单词 ID
     * @param listId 词库 ID
     * @return 学习记录，如果不存在则返回 null
     */
    suspend fun getLearningRecord(wordId: Int, listId: Int): LearningRecord? {
        return learningRecordDao.getRecordByWordAndList(wordId, listId)
    }

    /**
     * 获取连续学习天数
     * 
     * @param listId 词库 ID
     * @return 连续学习天数
     */
    suspend fun getConsecutiveLearningDays(@Suppress("UNUSED_PARAMETER") listId: Int): Int {
        // 这是一个简化的实现，实际应该查询数据库中的学习记录
        // 计算从第一次学习到现在的连续天数
        return 0 // TODO: 实现完整的连续天数计算
    }
}
