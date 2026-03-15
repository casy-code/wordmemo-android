package com.wordmemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordmemo.data.entity.Word
import com.wordmemo.domain.usecase.LearningStatistics
import com.wordmemo.domain.usecase.LearningUseCase
import com.wordmemo.domain.usecase.WordProgress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 学习页面 ViewModel
 * 
 * 完全集成学习流程与 UI，管理学习页面的所有状态和业务逻辑
 */
class LearnViewModel(
    private val learningUseCase: LearningUseCase
) : ViewModel() {

    // UI 状态
    private val _currentWord = MutableLiveData<Word?>()
    val currentWord: LiveData<Word?> = _currentWord

    private val _wordProgress = MutableLiveData<WordProgress?>()
    val wordProgress: LiveData<WordProgress?> = _wordProgress

    private val _isCardFlipped = MutableLiveData(false)
    val isCardFlipped: LiveData<Boolean> = _isCardFlipped

    private val _feedbackMessage = MutableLiveData<String>()
    val feedbackMessage: LiveData<String> = _feedbackMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _learningStatistics = MutableLiveData<LearningStatistics>()
    val learningStatistics: LiveData<LearningStatistics> = _learningStatistics

    private val _currentWordIndex = MutableLiveData(0)
    val currentWordIndex: LiveData<Int> = _currentWordIndex

    private val _totalWords = MutableLiveData(0)
    val totalWords: LiveData<Int> = _totalWords

    private val _isLearningComplete = MutableLiveData(false)
    val isLearningComplete: LiveData<Boolean> = _isLearningComplete

    // 内部状态
    private var currentListId: Int = 0
    private var wordList: List<Word> = emptyList()
    private var currentIndex: Int = 0

    /**
     * 初始化学习页面
     * 
     * @param listId 词库 ID
     */
    fun initializeLearning(listId: Int) {
        currentListId = listId
        _isLoading.value = true
        loadWords()
        loadStatistics()
    }

    /**
     * 加载今日待学习单词（排除今日已学习的，切换 tab 后不重置进度）
     */
    private fun loadWords() {
        viewModelScope.launch {
            try {
                val words = learningUseCase.getWordsToLearnToday(currentListId)
                wordList = words
                _totalWords.value = words.size

                if (words.isNotEmpty()) {
                    currentIndex = 0
                    _currentWordIndex.value = 0
                    loadCurrentWord()
                } else {
                    val hasWords = learningUseCase.getAllWordsInList(currentListId).map { it.isNotEmpty() }.first()
                    _feedbackMessage.value = if (hasWords) "今日待学习单词已完成" else "词库中没有单词"
                    _isLearningComplete.value = true
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _feedbackMessage.value = "加载单词失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 加载学习统计
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                learningUseCase.getTodayStatistics(currentListId).collect { stats ->
                    _learningStatistics.value = stats
                }
            } catch (e: Exception) {
                _feedbackMessage.value = "加载统计失败: ${e.message}"
            }
        }
    }

    /**
     * 加载当前单词
     */
    private fun loadCurrentWord() {
        if (currentIndex < wordList.size) {
            val word = wordList[currentIndex]
            _currentWord.value = word
            _isCardFlipped.value = false
            _currentWordIndex.value = currentIndex
            
            // 加载单词的学习进度
            viewModelScope.launch {
                try {
                    val progress = learningUseCase.getWordProgress(word.id, currentListId)
                    _wordProgress.value = progress
                } catch (e: Exception) {
                    _feedbackMessage.value = "加载进度失败: ${e.message}"
                }
            }
        }
    }

    /**
     * 翻转卡片
     */
    fun flipCard() {
        _isCardFlipped.value = !(_isCardFlipped.value ?: false)
    }

    /**
     * 记录学习反馈
     * 
     * @param quality 用户反馈评分 (0-5)
     */
    fun recordFeedback(quality: Int) {
        val word = _currentWord.value ?: return
        
        viewModelScope.launch {
            try {
                learningUseCase.recordFeedback(word.id, currentListId, quality)
                
                // 显示反馈消息（3 档：困难 0、一般 3、简单 5）
                val feedbackText = when (quality) {
                    0 -> "困难 ❌ - 下次复习: 1 天"
                    1, 2 -> "困难 😕 - 下次复习: 1 天"
                    3 -> "一般 😐 - 下次复习: 3 天"
                    4, 5 -> "简单 😊 - 下次复习: 更长时间"
                    else -> "反馈已记录"
                }
                _feedbackMessage.value = feedbackText
                
                // 重新加载统计
                loadStatistics()
                
                // 延迟后加载下一个单词
                kotlinx.coroutines.delay(800)
                moveToNextWord()
            } catch (e: Exception) {
                _feedbackMessage.value = "记录反馈失败: ${e.message}"
            }
        }
    }

    /**
     * 移动到下一个单词
     */
    fun moveToNextWord() {
        currentIndex++
        if (currentIndex < wordList.size) {
            loadCurrentWord()
        } else {
            _feedbackMessage.value = "今天的学习已完成！🎊"
            _isLearningComplete.value = true
            _currentWord.value = null
        }
    }

    /**
     * 移动到上一个单词
     */
    fun moveToPreviousWord() {
        if (currentIndex > 0) {
            currentIndex--
            loadCurrentWord()
        }
    }

    /**
     * 跳过当前单词
     */
    fun skipWord() {
        _feedbackMessage.value = "已跳过"
        moveToNextWord()
    }

    /**
     * 重新开始学习
     */
    fun restartLearning() {
        currentIndex = 0
        _isLearningComplete.value = false
        loadWords()
    }

    /**
     * 清除反馈消息
     */
    fun clearFeedbackMessage() {
        _feedbackMessage.value = ""
    }

    /**
     * 获取学习进度百分比
     */
    fun getLearningProgressPercentage(): Int {
        return if (wordList.isEmpty()) 0 else ((currentIndex + 1) * 100) / wordList.size
    }

    /**
     * 获取当前单词索引（用于 UI 和测试）
     */
    fun getCurrentWordIndex(): Int = currentIndex

    /**
     * 获取总单词数（用于 UI 和测试）
     */
    fun getTotalWords(): Int = wordList.size

    /**
     * 仅用于测试：设置当前单词
     */
    @androidx.annotation.VisibleForTesting
    fun setCurrentWordForTesting(word: Word?) {
        _currentWord.value = word
    }

    /**
     * 仅用于测试：设置当前词库 ID
     */
    @androidx.annotation.VisibleForTesting
    fun setCurrentListIdForTesting(listId: Int) {
        currentListId = listId
    }
}
