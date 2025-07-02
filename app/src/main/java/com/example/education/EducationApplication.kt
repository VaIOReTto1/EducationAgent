package com.example.education

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp

/**
 * 智能教学平台应用主类
 * 
 * 配置全局应用状态和依赖注入
 */
@HiltAndroidApp
class EducationApplication : Application(), Configuration.Provider {
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "智能教学平台应用启动")
        
        // 初始化WorkManager
        initWorkManager()
        
        // 初始化应用配置
        initAppConfiguration()
        
        Log.d(TAG, "应用初始化完成")
    }
    
    /**
     * 提供WorkManager配置
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO)
            .build()
    
    /**
     * 初始化WorkManager
     */
    private fun initWorkManager() {
        try {
            // WorkManager已通过workManagerConfiguration自动初始化
            Log.d(TAG, "WorkManager 初始化成功")
        } catch (e: Exception) {
            Log.e(TAG, "WorkManager 初始化失败", e)
        }
    }
    
    /**
     * 初始化应用配置
     */
    private fun initAppConfiguration() {
        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e(TAG, "未捕获异常在线程 ${thread.name}", exception)
            
            // 在生产环境中可以添加崩溃报告
            if (!BuildConfig.DEBUG) {
                // 发送崩溃报告到分析平台
                // Firebase Crashlytics.recordException(exception)
            }
        }
        
        Log.d(TAG, "应用配置初始化完成")
    }
    
    companion object {
        private const val TAG = "EducationApp"
        
        /**
         * 应用版本信息
         */
        const val VERSION_NAME = BuildConfig.VERSION_NAME
        const val VERSION_CODE = BuildConfig.VERSION_CODE
        
        /**
         * API配置信息
         */
        const val DIFY_API_KEY = BuildConfig.DIFY_API_KEY
        const val DIFY_BASE_URL = BuildConfig.DIFY_BASE_URL
    }
} 