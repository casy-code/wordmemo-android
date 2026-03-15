package com.wordmemo.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "word_list_items",
    primaryKeys = ["wordId", "listId"],
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
data class WordListItem(
    val wordId: Int,
    val listId: Int,
    val addedAt: Long = System.currentTimeMillis()
)
