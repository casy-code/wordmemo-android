package com.wordmemo

import android.app.Application
import android.util.Log
import com.wordmemo.di.AppContainer
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 应用入口，持有全局依赖容器
 * 初始化失败时 appContainer 为 null，Fragment 会显示错误提示而非崩溃
 */
class WordMemoApplication : Application() {

    var appContainer: AppContainer? = null
        private set

    override fun onCreate() {
        super.onCreate()
        installCrashHandler()
        try {
            appContainer = AppContainer(this)
        } catch (e: Exception) {
            Log.e(TAG, "AppContainer 初始化失败", e)
            writeCrashLog(e)
            // 不抛出，让应用继续运行，Fragment 会显示错误提示
        }
    }

    private fun installCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "未捕获异常: ${thread.name}", throwable)
            writeCrashLog(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrashLog(throwable: Throwable) {
        try {
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
            val logFile = File(filesDir, "crash_$timestamp.txt")
            logFile.writeText("${throwable.message}\n\n${sw}")
            Log.i(TAG, "崩溃日志已保存: ${logFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "写入崩溃日志失败", e)
        }
    }

    companion object {
        private const val TAG = "WordMemo"
    }
}
