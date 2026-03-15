package com.wordmemo.di

import android.content.Context
import com.wordmemo.data.db.AppDatabase
import com.wordmemo.data.preset.DatabaseSeeder
import com.wordmemo.domain.usecase.LearningManager
import com.wordmemo.domain.usecase.LearningUseCase
import kotlinx.coroutines.runBlocking

/**
 * 应用级依赖容器
 * 提供 DAO、UseCase 等单例
 * 首次启动时自动插入 5 分类共 500 个预设单词
 */
class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val wordDao = database.wordDao()
    val wordListDao = database.wordListDao()
    val wordListItemDao = database.wordListItemDao()
    val learningRecordDao = database.learningRecordDao()

    init {
        runBlocking {
            DatabaseSeeder.seedIfNeeded(wordListDao, wordDao, wordListItemDao)
        }
    }

    private val learningManager = LearningManager(wordDao, learningRecordDao)

    val learningUseCase = LearningUseCase(
        learningManager = learningManager,
        wordDao = wordDao,
        wordListDao = wordListDao,
        learningRecordDao = learningRecordDao
    )
}
