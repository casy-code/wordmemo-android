package com.wordmemo.data.entity

import org.junit.Test
import org.junit.Assert.*

class WordTest {

    @Test
    fun testWordCreation() {
        val word = Word(
            id = 1,
            content = "hello",
            translation = "你好",
            phonetic = "/həˈloʊ/",
            example = "Hello, world!",
            difficulty = 1
        )
        
        assertEquals(1, word.id)
        assertEquals("hello", word.content)
        assertEquals("你好", word.translation)
        assertEquals("/həˈloʊ/", word.phonetic)
        assertEquals("Hello, world!", word.example)
        assertEquals(1, word.difficulty)
    }

    @Test
    fun testWordDefaultValues() {
        val word = Word(
            content = "test",
            translation = "测试"
        )
        
        assertEquals(0, word.id)
        assertEquals("test", word.content)
        assertEquals("测试", word.translation)
        assertEquals("", word.phonetic)
        assertEquals("", word.example)
        assertEquals(1, word.difficulty)
        assertTrue(word.createdAt > 0)
    }

    @Test
    fun testWordCopy() {
        val word = Word(
            id = 1,
            content = "hello",
            translation = "你好",
            difficulty = 1
        )
        
        val copied = word.copy(difficulty = 3)
        
        assertEquals(1, copied.id)
        assertEquals("hello", copied.content)
        assertEquals("你好", copied.translation)
        assertEquals(3, copied.difficulty)
    }

    @Test
    fun testWordEquality() {
        val word1 = Word(
            id = 1,
            content = "hello",
            translation = "你好",
            difficulty = 1
        )
        
        val word2 = Word(
            id = 1,
            content = "hello",
            translation = "你好",
            difficulty = 1
        )
        
        assertEquals(word1, word2)
    }

    @Test
    fun testWordDifficultyRange() {
        for (difficulty in 1..5) {
            val word = Word(
                content = "test",
                translation = "测试",
                difficulty = difficulty
            )
            assertEquals(difficulty, word.difficulty)
        }
    }
}

class WordListTest {

    @Test
    fun testWordListCreation() {
        val wordList = WordList(
            id = 1,
            name = "CET-4",
            description = "College English Test Level 4",
            wordCount = 100,
            type = "preset"
        )
        
        assertEquals(1, wordList.id)
        assertEquals("CET-4", wordList.name)
        assertEquals("College English Test Level 4", wordList.description)
        assertEquals(100, wordList.wordCount)
        assertEquals("preset", wordList.type)
    }

    @Test
    fun testWordListDefaultValues() {
        val wordList = WordList(
            name = "Custom",
            description = "My custom list"
        )
        
        assertEquals(0, wordList.id)
        assertEquals("Custom", wordList.name)
        assertEquals("My custom list", wordList.description)
        assertEquals(0, wordList.wordCount)
        assertEquals("custom", wordList.type)
        assertTrue(wordList.createdAt > 0)
    }

    @Test
    fun testWordListCopy() {
        val wordList = WordList(
            id = 1,
            name = "CET-4",
            description = "四级",
            wordCount = 100
        )
        
        val copied = wordList.copy(wordCount = 150)
        
        assertEquals(1, copied.id)
        assertEquals("CET-4", copied.name)
        assertEquals(150, copied.wordCount)
    }
}

class LearningRecordTest {

    @Test
    fun testLearningRecordCreation() {
        val now = System.currentTimeMillis()
        val record = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 4,
            interval = 3,
            easeFactor = 2.6,
            nextReviewDate = now + 3 * 24 * 60 * 60 * 1000,
            reviewedAt = now
        )
        
        assertEquals(1, record.id)
        assertEquals(1, record.wordId)
        assertEquals(1, record.listId)
        assertEquals(4, record.quality)
        assertEquals(3, record.interval)
        assertEquals(2.6, record.easeFactor, 0.01)
    }

    @Test
    fun testLearningRecordDefaultValues() {
        val record = LearningRecord(
            wordId = 1,
            listId = 1,
            quality = 3,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis()
        )
        
        assertEquals(0, record.id)
        assertEquals(1, record.wordId)
        assertEquals(1, record.listId)
        assertEquals(3, record.quality)
        assertEquals(1, record.interval)
        assertEquals(2.5, record.easeFactor, 0.01)
    }

    @Test
    fun testLearningRecordCopy() {
        val record = LearningRecord(
            id = 1,
            wordId = 1,
            listId = 1,
            quality = 3,
            interval = 1,
            easeFactor = 2.5,
            nextReviewDate = System.currentTimeMillis()
        )
        
        val copied = record.copy(quality = 5, interval = 7, easeFactor = 2.8)
        
        assertEquals(1, copied.id)
        assertEquals(5, copied.quality)
        assertEquals(7, copied.interval)
        assertEquals(2.8, copied.easeFactor, 0.01)
    }

    @Test
    fun testLearningRecordQualityRange() {
        for (quality in 0..5) {
            val record = LearningRecord(
                wordId = 1,
                listId = 1,
                quality = quality,
                interval = 1,
                easeFactor = 2.5,
                nextReviewDate = System.currentTimeMillis()
            )
            assertEquals(quality, record.quality)
        }
    }
}
