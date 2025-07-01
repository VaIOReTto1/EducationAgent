package com.example.education.core.sync

import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 同步管理器
 * 负责管理所有数据同步任务
 */
@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val SYNC_WORK_NAME = "education_sync_work"
        private const val PREFETCH_WORK_NAME = "prefetch_chapters_work"
        private const val PERIODIC_SYNC_INTERVAL_HOURS = 1L
    }

    /**
     * 启动定期同步
     */
    fun startPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            PERIODIC_SYNC_INTERVAL_HOURS,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    /**
     * 立即执行同步
     */
    fun syncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncRequest)
    }

    /**
     * 启动章节预取任务
     * @param courseId 课程ID
     * @param currentChapterIndex 当前章节索引
     */
    fun startChapterPrefetch(courseId: String, currentChapterIndex: Int) {
        val inputData = Data.Builder()
            .putString("course_id", courseId)
            .putInt("current_chapter_index", currentChapterIndex)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val prefetchRequest = OneTimeWorkRequestBuilder<PrefetchNextChapterWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "${PREFETCH_WORK_NAME}_${courseId}",
            ExistingWorkPolicy.REPLACE,
            prefetchRequest
        )
    }

    /**
     * 停止所有同步任务
     */
    fun stopAllSync() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
        workManager.cancelAllWorkByTag(PREFETCH_WORK_NAME)
    }

    /**
     * 获取同步状态
     */
    fun getSyncStatus() = workManager.getWorkInfosForUniqueWorkLiveData(SYNC_WORK_NAME)
}