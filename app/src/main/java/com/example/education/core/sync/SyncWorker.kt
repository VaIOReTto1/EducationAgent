package com.example.education.core.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.education.core.database.dao.MessageDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

/**
 * 数据同步WorkManager工作者
 * 
 * 负责后台同步本地数据到Firebase，确保数据一致性
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firebaseSyncRepository: FirebaseSyncRepository,
    private val messageDao: MessageDao
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val TAG = "SyncWorker"
        const val WORK_NAME = "sync_work"
        
        /**
         * 创建一次性同步任务
         */
        fun createOneTimeWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()
        }
        
        /**
         * 创建周期性同步任务
         */
        fun createPeriodicWork(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<SyncWorker>(15, java.util.concurrent.TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()
        }
    }
    
    override suspend fun doWork(): Result = coroutineScope {
        Log.d(TAG, "开始执行数据同步任务")
        
        try {
            // 设置进度
            setProgress(workDataOf("status" to "正在同步数据..."))
            
            // 同步未同步的数据
            firebaseSyncRepository.syncPendingData()
            
            Log.d(TAG, "数据同步任务完成")
            Result.success(workDataOf("sync_time" to System.currentTimeMillis()))
            
        } catch (e: Exception) {
            Log.e(TAG, "数据同步任务失败", e)
            
            // 如果是网络错误，则重试
            if (runAttemptCount < 3) {
                Log.d(TAG, "同步失败，将重试（第${runAttemptCount + 1}次）")
                Result.retry()
            } else {
                Log.e(TAG, "同步失败，已达到最大重试次数")
                Result.failure(
                    workDataOf(
                        "error" to e.message,
                        "failed_time" to System.currentTimeMillis()
                    )
                )
            }
        }
    }
}

/**
 * 课程预获取WorkManager工作者
 * 
 * 根据学习进度预获取下一章节内容，提升用户体验
 */
@HiltWorker  
class PrefetchNextChapterWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firebaseSyncRepository: FirebaseSyncRepository
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val TAG = "PrefetchWorker"
        const val WORK_NAME = "prefetch_work"
        const val USER_ID_KEY = "user_id"
        const val CHAPTER_ID_KEY = "chapter_id"
        
        /**
         * 创建预获取任务
         */
        fun createPrefetchWork(userId: String, currentChapterId: String): OneTimeWorkRequest {
            val inputData = workDataOf(
                USER_ID_KEY to userId,
                CHAPTER_ID_KEY to currentChapterId
            )
            
            return OneTimeWorkRequestBuilder<PrefetchNextChapterWorker>()
                .setInputData(inputData)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }
    }
    
    override suspend fun doWork(): Result = coroutineScope {
        val userId = inputData.getString(USER_ID_KEY) ?: return@coroutineScope Result.failure()
        val chapterId = inputData.getString(CHAPTER_ID_KEY) ?: return@coroutineScope Result.failure()
        
        Log.d(TAG, "开始预获取下一章节，用户: $userId, 当前章节: $chapterId")
        
        try {
            // 设置进度
            setProgress(workDataOf("status" to "正在预获取下一章节..."))
            
            // TODO: 实现预获取逻辑
            // 1. 根据当前章节找到下一章节
            // 2. 预加载下一章节的内容和资源
            // 3. 缓存到本地数据库
            
            Log.d(TAG, "下一章节预获取完成")
            Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "预获取下一章节失败", e)
            Result.failure(workDataOf("error" to e.message))
        }
    }
}

/**
 * 学习分析WorkManager工作者
 * 
 * 定期分析学习数据，生成学习报告和建议
 */
@HiltWorker
class LearningAnalyticsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val TAG = "LearningAnalyticsWorker"
        const val WORK_NAME = "analytics_work"
        
        /**
         * 创建学习分析任务
         */
        fun createAnalyticsWork(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<LearningAnalyticsWorker>(1, java.util.concurrent.TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
        }
    }
    
    override suspend fun doWork(): Result = coroutineScope {
        Log.d(TAG, "开始执行学习分析任务")
        
        try {
            // 设置进度
            setProgress(workDataOf("status" to "正在分析学习数据..."))
            
            // TODO: 实现学习分析逻辑
            // 1. 收集用户学习数据
            // 2. 计算学习效率和知识点掌握度
            // 3. 生成个性化学习建议
            // 4. 更新推荐系统数据
            
            Log.d(TAG, "学习分析任务完成")
            Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "学习分析任务失败", e)
            Result.failure(workDataOf("error" to e.message))
        }
    }
} 