package com.wordmemo

import android.app.Application
import com.wordmemo.di.AppContainer

/**
 * 应用入口，持有全局依赖容器
 */
class WordMemoApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
