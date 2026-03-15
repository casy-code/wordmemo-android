package com.wordmemo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_lists")
data class WordList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val type: String = "custom", // preset or custom
    val createdAt: Long = System.currentTimeMillis()
)
