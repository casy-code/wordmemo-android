package com.wordmemo.domain.model

import org.junit.Test
import org.junit.Assert.*

class SM2AlgorithmExtendedTest {

    @Test
    fun testMultipleReviewSequence() {
        // 模拟一个单词的多次复习过程
        var interval = 1
        var easeFactor = 2.5
        var reviewDate = System.currentTimeMillis()

        // 第一次复习：质量 4（记得不错）
        var result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = interval,
            currentEaseFactor = easeFactor,
            currentReviewDate = reviewDate
        )
        interval = result.nextInterval
        easeFactor = result.nextEaseFactor
        reviewDate = result.nextReviewDate
        assertEquals(3, interval)

        // 第二次复习：质量 4（记得不错）
        result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = interval,
            currentEaseFactor = easeFactor,
            currentReviewDate = reviewDate
        )
        interval = result.nextInterval
        easeFactor = result.nextEaseFactor
        reviewDate = result.nextReviewDate
        assertTrue(interval > 3)

        // 第三次复习：质量 3（勉强记得）
        result = SM2Algorithm.calculateNextReview(
            quality = 3,
            currentInterval = interval,
            currentEaseFactor = easeFactor,
            currentReviewDate = reviewDate
        )
        interval = result.nextInterval
        easeFactor = result.nextEaseFactor
        reviewDate = result.nextReviewDate
        assertTrue(interval > 0)
    }

    @Test
    fun testEaseFactorProgression() {
        // 质量 5 增加易度因子，质量 4 保持或略增
        var easeFactor = 2.5

        var result = SM2Algorithm.calculateNextReview(
            quality = 5,
            currentInterval = 1,
            currentEaseFactor = easeFactor,
            currentReviewDate = System.currentTimeMillis()
        )
        assertTrue(result.nextEaseFactor > easeFactor)

        easeFactor = result.nextEaseFactor

        result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 3,
            currentEaseFactor = easeFactor,
            currentReviewDate = System.currentTimeMillis()
        )
        assertTrue(result.nextEaseFactor >= 2.5)
    }

    @Test
    fun testIntervalCalculation() {
        // 测试间隔计算的正确性
        val result1 = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 1,
            currentEaseFactor = 2.5,
            currentReviewDate = System.currentTimeMillis()
        )
        assertEquals(3, result1.nextInterval)

        val result2 = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 3,
            currentEaseFactor = 2.5,
            currentReviewDate = System.currentTimeMillis()
        )
        assertEquals((3 * 2.5).toInt(), result2.nextInterval)

        val result3 = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 7,
            currentEaseFactor = 2.5,
            currentReviewDate = System.currentTimeMillis()
        )
        assertEquals((7 * 2.5).toInt(), result3.nextInterval)
    }

    @Test
    fun testReviewDateCalculation() {
        val baseDate = 1000000L
        val result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 3,
            currentEaseFactor = 2.5,
            currentReviewDate = baseDate
        )
        // nextInterval = 3*2.5 = 7
        val expectedDate = baseDate + 7 * 24 * 60 * 60 * 1000L
        assertEquals(expectedDate, result.nextReviewDate)
    }

    @Test
    fun testFailureRecovery() {
        // 测试失败后的恢复
        var result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 10,
            currentEaseFactor = 2.5,
            currentReviewDate = System.currentTimeMillis()
        )

        val interval1 = result.nextInterval
        val easeFactor1 = result.nextEaseFactor

        // 失败
        result = SM2Algorithm.calculateNextReview(
            quality = 0,
            currentInterval = interval1,
            currentEaseFactor = easeFactor1,
            currentReviewDate = result.nextReviewDate
        )

        // 间隔应该重置为 1
        assertEquals(1, result.nextInterval)
        // 易度因子应该保持不变
        assertEquals(easeFactor1, result.nextEaseFactor, 0.01)
    }

    @Test
    fun testBoundaryConditions() {
        // 测试边界条件
        val result1 = SM2Algorithm.calculateNextReview(
            quality = 0,
            currentInterval = 1,
            currentEaseFactor = 1.3,
            currentReviewDate = System.currentTimeMillis()
        )
        assertEquals(1.3, result1.nextEaseFactor, 0.01)

        val result2 = SM2Algorithm.calculateNextReview(
            quality = 5,
            currentInterval = 1,
            currentEaseFactor = 2.5,
            currentReviewDate = System.currentTimeMillis()
        )
        assertTrue(result2.nextEaseFactor > 2.5)
    }

    @Test
    fun testQualityImpact() {
        // 测试不同质量对易度因子的影响：质量 0-2 失败不变，3 略降，4 不变，5 略增
        val baseEaseFactor = 2.5
        val baseInterval = 5

        val results = mutableMapOf<Int, Double>()
        for (quality in 0..5) {
            val result = SM2Algorithm.calculateNextReview(
                quality = quality,
                currentInterval = baseInterval,
                currentEaseFactor = baseEaseFactor,
                currentReviewDate = System.currentTimeMillis()
            )
            results[quality] = result.nextEaseFactor
        }

        // 质量 5 > 质量 4 >= 质量 3（成功时易度因子变化）
        assertTrue(results[5]!! >= results[4]!!)
        assertTrue(results[4]!! >= results[3]!!)
        // 质量 0-2 失败时易度因子保持不变
        assertEquals(results[0], results[1])
        assertEquals(results[1], results[2])
    }
}
