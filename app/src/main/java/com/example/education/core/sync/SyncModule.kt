package com.example.education.core.sync

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 同步模块 - 提供数据同步相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    /**
     * 提供WorkManager实例
     */
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    /**
     * 提供同步管理器
     */
    @Provides
    @Singleton
    fun provideSyncManager(
        workManager: WorkManager
    ): SyncManager {
        return SyncManager(workManager)
    }
}