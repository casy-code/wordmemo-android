package com.wordmemo.di

import android.content.Context
import com.wordmemo.data.db.AppDatabase
import com.wordmemo.domain.usecase.LearningManager
import com.wordmemo.domain.usecase.LearningUseCase

/**
 * 应用级依赖容器
 * 提供 DAO、UseCase 等单例
 */
class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val wordDao = database.wordDao()
    val wordListDao = database.wordListDao()
    val learningRecordDao = database.learningRecordDao()

    private val learningManager = LearningManager(wordDao, learningRecordDao)

    val learningUseCase = LearningUseCase(
        learningManager = learningManager,
        wordDao = wordDao,
        wordListDao = wordListDao,
        learningRecordDao = learningRecordDao
    )
}
