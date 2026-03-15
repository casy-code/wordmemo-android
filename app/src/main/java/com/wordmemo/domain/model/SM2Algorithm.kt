package com.wordmemo.domain.model

/**
 * SM-2 (SuperMemo 2) 间隔重复算法实现
 * 
 * 该算法根据用户的学习反馈自动调整复习间隔和易度因子。
 * 
 * 参数说明：
 * - quality: 用户反馈评分 (0-5)
 *   0: 完全忘记
 *   1: 非常困难
 *   2: 困难
 *   3: 勉强记得
 *   4: 记得不错
 *   5: 完美记得
 * - interval: 复习间隔（天数）
 * - easeFactor: 易度因子（初始值 2.5）
 */
data class SM2Result(
    val nextInterval: Int,      // 下一次复习间隔（天数）
    val nextEaseFactor: Double, // 下一个易度因子
    val nextReviewDate: Long    // 下一次复习日期（毫秒时间戳）
)

object SM2Algorithm {
    private const val MIN_EASE_FACTOR = 1.3
    private const val INITIAL_EASE_FACTOR = 2.5
    private const val QUALITY_THRESHOLD = 3 // 质量阈值，低于此值视为失败

    /**
     * 计算下一次复习的间隔和易度因子
     * 
     * @param quality 用户反馈评分 (0-5)
     * @param currentInterval 当前复习间隔（天数）
     * @param currentEaseFactor 当前易度因子
     * @param currentReviewDate 当前复习日期（毫秒时间戳）
     * @return SM2Result 包含下一次复习的间隔、易度因子和日期
     */
    fun calculateNextReview(
        quality: Int,
        currentInterval: Int,
        currentEaseFactor: Double,
        currentReviewDate: Long = System.currentTimeMillis()
    ): SM2Result {
        // 验证输入
        require(quality in 0..5) { "Quality must be between 0 and 5" }
        require(currentInterval > 0) { "Current interval must be positive" }
        require(currentEaseFactor >= MIN_EASE_FACTOR) { "Ease factor must be at least $MIN_EASE_FACTOR" }

        val nextInterval: Int
        val nextEaseFactor: Double

        if (quality < QUALITY_THRESHOLD) {
            // 学习失败：重置间隔为 1 天，易度因子不变
            nextInterval = 1
            nextEaseFactor = currentEaseFactor
        } else {
            // 学习成功：计算新的间隔和易度因子
            nextInterval = when (currentInterval) {
                1 -> 3
                else -> (currentInterval * currentEaseFactor).toInt()
            }

            // 计算新的易度因子
            // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
            val qualityDiff = 5 - quality
            val easeAdjustment = 0.1 - qualityDiff * (0.08 + qualityDiff * 0.02)
            nextEaseFactor = (currentEaseFactor + easeAdjustment).coerceAtLeast(MIN_EASE_FACTOR)
        }

        // 计算下一次复习日期
        val nextReviewDate = currentReviewDate + (nextInterval * 24 * 60 * 60 * 1000L)

        return SM2Result(
            nextInterval = nextInterval,
            nextEaseFactor = nextEaseFactor,
            nextReviewDate = nextReviewDate
        )
    }

    /**
     * 初始化新单词的学习参数
     */
    fun initializeNewWord(): Pair<Int, Double> {
        return Pair(1, INITIAL_EASE_FACTOR)
    }

    /**
     * 获取初始易度因子
     */
    fun getInitialEaseFactor(): Double = INITIAL_EASE_FACTOR

    /**
     * 获取最小易度因子
     */
    fun getMinEaseFactor(): Double = MIN_EASE_FACTOR
}
