package com.wordmemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordmemo.data.entity.Word
import com.wordmemo.domain.usecase.LearningUseCase
import kotlinx.coroutines.launch

/**
 * 复习页面 ViewModel
 *
 * 加载待复习单词（nextReviewDate <= 当前时间），闪卡式逐个复习，记录反馈后更新 SM-2 计划
 */
class ReviewViewModel(
    private val learningUseCase: LearningUseCase
) : ViewModel() {

    private val _currentWord = MutableLiveData<Word?>()
    val currentWord: LiveData<Word?> = _currentWord

    private val _isCardFlipped = MutableLiveData(false)
    val isCardFlipped: LiveData<Boolean> = _isCardFlipped

    private val _feedbackMessage = MutableLiveData<String>()
    val feedbackMessage: LiveData<String> = _feedbackMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentWordIndex = MutableLiveData(0)
    val currentWordIndex: LiveData<Int> = _currentWordIndex

    private val _totalWords = MutableLiveData(0)
    val totalWords: LiveData<Int> = _totalWords

    private val _isReviewComplete = MutableLiveData(false)
    val isReviewComplete: LiveData<Boolean> = _isReviewComplete

    private var currentListId: Int = 0
    private var wordList: List<Word> = emptyList()
    private var currentIndex: Int = 0

    /**
     * 初始化复习页面，加载待复习单词
     */
    fun initializeReview(listId: Int) {
        currentListId = listId
        _isLoading.value = true
        loadReviewWords()
    }

    private fun loadReviewWords() {
        viewModelScope.launch {
            try {
                wordList = learningUseCase.getReviewDueWords(currentListId)
                _totalWords.value = wordList.size

                if (wordList.isNotEmpty()) {
                    currentIndex = 0
                    _currentWordIndex.value = 0
                    _isReviewComplete.value = false
                    loadCurrentWord()
                } else {
                    _feedbackMessage.value = "暂无待复习单词"
                    _isReviewComplete.value = true
                    _currentWord.value = null
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _feedbackMessage.value = "加载复习单词失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun loadCurrentWord() {
        if (currentIndex < wordList.size) {
            val word = wordList[currentIndex]
            _currentWord.value = word
            _isCardFlipped.value = false
            _currentWordIndex.value = currentIndex
        }
    }

    fun flipCard() {
        _isCardFlipped.value = !(_isCardFlipped.value ?: false)
    }

    fun recordFeedback(quality: Int) {
        val word = _currentWord.value ?: return

        viewModelScope.launch {
            try {
                learningUseCase.recordFeedback(word.id, currentListId, quality)

                val feedbackText = when (quality) {
                    0, 1, 2 -> "困难 ❌ - 下次复习: 1 天"
                    3 -> "一般 😐 - 下次复习: 3 天"
                    4, 5 -> "简单 😊 - 下次复习: 更长时间"
                    else -> "反馈已记录"
                }
                _feedbackMessage.value = feedbackText

                kotlinx.coroutines.delay(800)
                moveToNextWord()
            } catch (e: Exception) {
                _feedbackMessage.value = "记录反馈失败: ${e.message}"
            }
        }
    }

    fun moveToNextWord() {
        currentIndex++
        if (currentIndex < wordList.size) {
            loadCurrentWord()
        } else {
            _feedbackMessage.value = "今日复习已完成！🎊"
            _isReviewComplete.value = true
            _currentWord.value = null
        }
    }

    fun clearFeedbackMessage() {
        _feedbackMessage.value = ""
    }

    fun getCurrentWordIndex(): Int = currentIndex

    fun getTotalWords(): Int = wordList.size

    fun refreshReview() {
        _isReviewComplete.value = false
        loadReviewWords()
    }

    @androidx.annotation.VisibleForTesting
    fun setCurrentWordForTesting(word: Word?) {
        _currentWord.value = word
    }

    @androidx.annotation.VisibleForTesting
    fun setCurrentListIdForTesting(listId: Int) {
        currentListId = listId
    }
}
