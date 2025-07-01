package com.example.education

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import dagger.hilt.android.HiltAndroidApp
import leakcanary.LeakCanary
import javax.inject.Inject

/**
 * 教育应用的Application类
 * 负责初始化Hilt、Flipper、LeakCanary等全局组件
 */
@HiltAndroidApp
class EducationApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        
        // 初始化调试工具
        initializeDebugTools()
    }

    /**
     * WorkManager配置
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * 初始化调试工具（仅在Debug构建中启用）
     */
    private fun initializeDebugTools() {
        if (BuildConfig.DEBUG) {
            // 初始化LeakCanary
            if (BuildConfig.ENABLE_LEAKCANARY) {
                LeakCanary.config = LeakCanary.config.copy(
                    dumpHeap = true,
                    retainedVisibleThreshold = 3
                )
            }

            // 初始化Flipper
            if (BuildConfig.ENABLE_FLIPPER && FlipperUtils.shouldEnableFlipper(this)) {
                val client = AndroidFlipperClient.getInstance(this)
                client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
                client.addPlugin(DatabasesFlipperPlugin(this))
                client.addPlugin(NetworkFlipperPlugin())
                client.start()
            }
        }
    }
}