package com.wordmemo.domain.usecase

import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.Word
import com.wordmemo.data.entity.WordList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * 学习用例类
 * 
 * 提供高级的学习业务逻辑，包括：
 * - 获取今日待学习单词
 * - 获取今日待复习单词
 * - 获取学习统计信息
 * - 管理学习进度
 */
class LearningUseCase(
    private val learningManager: LearningManager,
    private val wordDao: WordDao,
    private val wordListDao: WordListDao,
    private val learningRecordDao: LearningRecordDao
) {
    /**
     * 获取词库信息
     * 
     * @param listId 词库 ID
     * @return 词库信息
     */
    suspend fun getWordList(listId: Int): WordList? {
        return wordListDao.getWordListById(listId)
    }

    /**
     * 获取今日学习统计
     * 
     * @param listId 词库 ID
     * @return 包含学习数、复习数、连续天数的统计信息
     */
    fun getTodayStatistics(listId: Int): Flow<LearningStatistics> {
        return combine(
            learningRecordDao.getTodayLearningCount(listId),
            learningRecordDao.getTodayReviewCount(listId)
        ) { learningCount, reviewCount ->
            LearningStatistics(
                todayLearningCount = learningCount,
                todayReviewCount = reviewCount,
                consecutiveDays = 0 // TODO: 实现连续天数计算
            )
        }
    }

    /**
     * 获取需要复习的单词列表
     * 
     * @param listId 词库 ID
     * @return 需要复习的单词列表
     */
    suspend fun getReviewDueWords(listId: Int): List<Word> {
        return learningManager.getReviewDueWords(listId)
    }

    /**
     * 获取词库中的所有单词
     * 
     * @param listId 词库 ID
     * @return 单词列表
     */
    fun getAllWordsInList(listId: Int): Flow<List<Word>> {
        return wordDao.getAllWords()
    }

    /**
     * 记录学习反馈
     * 
     * @param wordId 单词 ID
     * @param listId 词库 ID
     * @param quality 用户反馈评分 (0-5)
     */
    suspend fun recordFeedback(wordId: Int, listId: Int, quality: Int) {
        require(quality in 0..5) { "Quality must be between 0 and 5" }
        learningManager.recordLearningFeedback(wordId, listId, quality)
    }

    /**
     * 获取单词的学习进度
     * 
     * @param wordId 单词 ID
     * @param listId 词库 ID
     * @return 学习进度信息
     */
    suspend fun getWordProgress(wordId: Int, listId: Int): WordProgress? {
        val word = wordDao.getWordById(wordId) ?: return null
        val record = learningManager.getLearningRecord(wordId, listId)
        
        return if (record != null) {
            WordProgress(
                wordId = wordId,
                word = word,
                quality = record.quality,
                interval = record.interval,
                easeFactor = record.easeFactor,
                nextReviewDate = record.nextReviewDate,
                reviewCount = record.reviewCount,
                isReviewDue = record.nextReviewDate <= System.currentTimeMillis()
            )
        } else {
            WordProgress(
                wordId = wordId,
                word = word,
                quality = 0,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis(),
                reviewCount = 0,
                isReviewDue = true
            )
        }
    }

    /**
     * 获取学习进度列表
     * 
     * @param listId 词库 ID
     * @return 学习进度列表
     */
    suspend fun getWordProgressList(listId: Int): List<WordProgress> {
        val words = wordDao.getAllWords()
        val progressList = mutableListOf<WordProgress>()
        
        words.collect { wordList ->
            for (word in wordList) {
                val progress = getWordProgress(word.id, listId)
                if (progress != null) {
                    progressList.add(progress)
                }
            }
        }
        
        return progressList
    }

    /**
     * 检查是否有待复习的单词
     * 
     * @param listId 词库 ID
     * @return 是否有待复习的单词
     */
    suspend fun hasReviewDueWords(listId: Int): Boolean {
        return getReviewDueWords(listId).isNotEmpty()
    }

    /**
     * 获取学习进度百分比
     * 
     * @param listId 词库 ID
     * @return 学习进度百分比 (0-100)
     */
    suspend fun getLearningProgress(listId: Int): Int {
        val allWords = mutableListOf<Word>()
        wordDao.getAllWords().collect { words ->
            allWords.addAll(words)
        }
        
        if (allWords.isEmpty()) return 0
        
        var learnedCount = 0
        for (word in allWords) {
            val record = learningManager.getLearningRecord(word.id, listId)
            if (record != null && record.quality >= 3) {
                learnedCount++
            }
        }
        
        return (learnedCount * 100) / allWords.size
    }
}

/**
 * 学习统计信息
 */
data class LearningStatistics(
    val todayLearningCount: Int,    // 今日学习数
    val todayReviewCount: Int,      // 今日复习数
    val consecutiveDays: Int        // 连续学习天数
)

/**
 * 单词学习进度
 */
data class WordProgress(
    val wordId: Int,                // 单词 ID
    val word: Word,                 // 单词信息
    val quality: Int,               // 当前质量评分
    val interval: Int,              // 复习间隔（天数）
    val easeFactor: Double,         // 易度因子
    val nextReviewDate: Long,       // 下一次复习日期
    val reviewCount: Int,           // 复习次数
    val isReviewDue: Boolean        // 是否需要复习
)
