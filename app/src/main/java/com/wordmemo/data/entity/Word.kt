package com.wordmemo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,
    val translation: String,
    val phonetic: String = "",
    val example: String = "",
    val difficulty: Int = 1, // 1-5
    val createdAt: Long = System.currentTimeMillis()
)
