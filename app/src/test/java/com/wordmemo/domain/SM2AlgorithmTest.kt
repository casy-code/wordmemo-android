package com.wordmemo.domain.model

import org.junit.Assert.*
import org.junit.Test

class SM2AlgorithmTest {

    @Test
    fun testInitializeNewWord() {
        val (interval, easeFactor) = SM2Algorithm.initializeNewWord()
        assertEquals(1, interval)
        assertEquals(2.5, easeFactor, 0.01)
    }

    @Test
    fun testCalculateNextReview_QualityLessThan3() {
        // 学习失败：质量 < 3
        val result = SM2Algorithm.calculateNextReview(
            quality = 2,
            currentInterval = 5,
            currentEaseFactor = 2.5,
            currentReviewDate = 1000000L
        )
        
        assertEquals(1, result.nextInterval)
        assertEquals(2.5, result.nextEaseFactor, 0.01)
        assertEquals(1000000L + 24 * 60 * 60 * 1000L, result.nextReviewDate)
    }

    @Test
    fun testCalculateNextReview_QualityEquals3() {
        // 学习成功：质量 = 3（勉强记得），间隔从 1 变为 3
        val result = SM2Algorithm.calculateNextReview(
            quality = 3,
            currentInterval = 1,
            currentEaseFactor = 2.5,
            currentReviewDate = 1000000L
        )
        
        assertEquals(3, result.nextInterval)
        // 质量 3 时易度因子会略微下降（EF' = EF + (0.1 - (5-3)*(0.08+(5-3)*0.02)) = 2.5 - 0.14）
        assertTrue(result.nextEaseFactor in 2.3..2.5)
        assertEquals(1000000L + 3 * 24 * 60 * 60 * 1000L, result.nextReviewDate)
    }

    @Test
    fun testCalculateNextReview_QualityEquals4() {
        // 学习成功：质量 = 4（记得不错），易度因子调整约为 0
        val result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 3,
            currentEaseFactor = 2.5,
            currentReviewDate = 1000000L
        )
        
        assertEquals((3 * 2.5).toInt(), result.nextInterval)
        assertTrue(result.nextEaseFactor >= 2.5)
    }

    @Test
    fun testCalculateNextReview_QualityEquals5() {
        // 学习成功：质量 = 5（完美记得）
        val result = SM2Algorithm.calculateNextReview(
            quality = 5,
            currentInterval = 7,
            currentEaseFactor = 2.5,
            currentReviewDate = 1000000L
        )
        
        assertEquals((7 * 2.5).toInt(), result.nextInterval)
        assertTrue(result.nextEaseFactor > 2.5)
    }

    @Test
    fun testCalculateNextReview_FirstReview() {
        // 第一次复习：间隔从 1 变为 3
        val result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 1,
            currentEaseFactor = 2.5,
            currentReviewDate = 1000000L
        )
        
        assertEquals(3, result.nextInterval)
    }

    @Test
    fun testCalculateNextReview_EaseFactorMinimum() {
        // 易度因子不能低于最小值
        val result = SM2Algorithm.calculateNextReview(
            quality = 0,
            currentInterval = 1,
            currentEaseFactor = 1.3,
            currentReviewDate = 1000000L
        )
        
        assertEquals(1.3, result.nextEaseFactor, 0.01)
    }

    @Test
    fun testCalculateNextReview_InvalidQuality() {
        // 质量值超出范围应抛出异常
        assertThrows(IllegalArgumentException::class.java) {
            SM2Algorithm.calculateNextReview(
                quality = 6,
                currentInterval = 1,
                currentEaseFactor = 2.5
            )
        }
    }

    @Test
    fun testCalculateNextReview_InvalidInterval() {
        // 间隔值无效应抛出异常
        assertThrows(IllegalArgumentException::class.java) {
            SM2Algorithm.calculateNextReview(
                quality = 3,
                currentInterval = 0,
                currentEaseFactor = 2.5
            )
        }
    }

    @Test
    fun testCalculateNextReview_InvalidEaseFactor() {
        // 易度因子无效应抛出异常
        assertThrows(IllegalArgumentException::class.java) {
            SM2Algorithm.calculateNextReview(
                quality = 3,
                currentInterval = 1,
                currentEaseFactor = 1.0
            )
        }
    }

    @Test
    fun testGetInitialEaseFactor() {
        assertEquals(2.5, SM2Algorithm.getInitialEaseFactor(), 0.01)
    }

    @Test
    fun testGetMinEaseFactor() {
        assertEquals(1.3, SM2Algorithm.getMinEaseFactor(), 0.01)
    }

    @Test
    fun testCalculateNextReview_SequentialReviews() {
        // 测试连续复习的场景
        var result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 1,
            currentEaseFactor = 2.5,
            currentReviewDate = 1000000L
        )
        
        assertEquals(3, result.nextInterval)
        
        // 第二次复习：间隔 3 -> 7
        result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = result.nextInterval,
            currentEaseFactor = result.nextEaseFactor,
            currentReviewDate = result.nextReviewDate
        )
        
        assertTrue(result.nextInterval > 3)
        assertTrue(result.nextEaseFactor >= 2.5)
    }

    @Test
    fun testCalculateNextReview_RecoveryAfterFailure() {
        // 测试失败后恢复的场景
        var result = SM2Algorithm.calculateNextReview(
            quality = 4,
            currentInterval = 10,
            currentEaseFactor = 2.5,
            currentReviewDate = 1000000L
        )
        
        val interval1 = result.nextInterval
        val easeFactor1 = result.nextEaseFactor
        
        // 失败
        result = SM2Algorithm.calculateNextReview(
            quality = 1,
            currentInterval = interval1,
            currentEaseFactor = easeFactor1,
            currentReviewDate = result.nextReviewDate
        )
        
        assertEquals(1, result.nextInterval)
        assertEquals(easeFactor1, result.nextEaseFactor, 0.01)
    }
}
