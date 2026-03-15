package com.wordmemo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wordmemo.data.db.dao.LearningRecordDao
import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.LearningRecord
import com.wordmemo.data.entity.Word
import com.wordmemo.data.entity.WordList
import com.wordmemo.data.entity.WordListItem

/**
 * Room 数据库类
 * 定义数据库版本、Entity 和 DAO
 * 支持数据库迁移和初始化回调
 */
@Database(
    entities = [Word::class, WordList::class, WordListItem::class, LearningRecord::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun wordListDao(): WordListDao
    abstract fun learningRecordDao(): LearningRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        private const val DATABASE_NAME = "wordmemo_database"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * 清除数据库实例（用于测试）
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }

    /**
     * 数据库回调类
     * 处理数据库创建和打开时的初始化逻辑
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // 数据库首次创建时的初始化逻辑
            // 可以在这里插入预设数据
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // 数据库打开时的初始化逻辑
            // 例如启用外键约束
            db.execSQL("PRAGMA foreign_keys=ON")
        }
    }
}
