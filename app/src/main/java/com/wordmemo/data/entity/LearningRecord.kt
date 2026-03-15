package com.wordmemo.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "learning_records",
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WordList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("wordId"),
        Index("listId")
    ]
)
data class LearningRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val wordId: Int,
    val listId: Int,
    val quality: Int = 0, // 0-5: 忘记(0), 模糊(1-2), 认识(3-5)
    val interval: Int = 1, // 天数
    val easeFactor: Double = 2.5, // SM-2 算法的易度因子
    val nextReviewDate: Long = System.currentTimeMillis(),
    val reviewedAt: Long = System.currentTimeMillis()
)
