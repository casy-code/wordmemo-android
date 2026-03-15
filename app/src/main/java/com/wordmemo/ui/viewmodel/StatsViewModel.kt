package com.wordmemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordmemo.domain.usecase.LearningStatistics
import com.wordmemo.domain.usecase.LearningUseCase
import kotlinx.coroutines.launch

/**
 * 统计页面 ViewModel
 * 
 * 管理统计页面的 UI 状态和业务逻辑
 */
class StatsViewModel(
    private val learningUseCase: LearningUseCase
) : ViewModel() {

    private val _todayLearningCount = MutableLiveData(0)
    val todayLearningCount: LiveData<Int> = _todayLearningCount

    private val _todayReviewCount = MutableLiveData(0)
    val todayReviewCount: LiveData<Int> = _todayReviewCount

    private val _consecutiveDays = MutableLiveData(0)
    val consecutiveDays: LiveData<Int> = _consecutiveDays

    private val _learningProgress = MutableLiveData(0)
    val learningProgress: LiveData<Int> = _learningProgress

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private var currentListId: Int = 0

    /**
     * 初始化统计页面
     * 
     * @param listId 词库 ID
     */
    fun initializeStats(listId: Int) {
        currentListId = listId
        loadStatistics()
    }

    /**
     * 加载统计数据
     */
    private fun loadStatistics() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                learningUseCase.getTodayStatistics(currentListId).collect { stats ->
                    _todayLearningCount.value = stats.todayLearningCount
                    _todayReviewCount.value = stats.todayReviewCount
                    _consecutiveDays.value = stats.consecutiveDays
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载统计失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 加载学习进度
     */
    fun loadLearningProgress() {
        viewModelScope.launch {
            try {
                val progress = learningUseCase.getLearningProgress(currentListId)
                _learningProgress.value = progress
            } catch (e: Exception) {
                _errorMessage.value = "加载进度失败: ${e.message}"
            }
        }
    }

    /**
     * 刷新统计数据
     */
    fun refreshStatistics() {
        loadStatistics()
        loadLearningProgress()
    }

    /**
     * 获取统计摘要
     */
    fun getStatisticsSummary(): String {
        val learning = _todayLearningCount.value ?: 0
        val review = _todayReviewCount.value ?: 0
        val consecutive = _consecutiveDays.value ?: 0
        
        return "今日学习: $learning | 复习: $review | 连续: $consecutive 天"
    }

    /**
     * 获取学习状态描述
     */
    fun getLearningStatusDescription(): String {
        val learning = _todayLearningCount.value ?: 0
        val review = _todayReviewCount.value ?: 0
        
        return when {
            learning == 0 && review == 0 -> "今天还没有学习，加油！💪"
            learning > 0 && review == 0 -> "已学习 $learning 个单词，继续加油！📚"
            learning == 0 && review > 0 -> "已复习 $review 个单词，保持节奏！🔄"
            else -> "已学习 $learning 个，复习 $review 个，很棒！🎉"
        }
    }

    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = ""
    }
}
