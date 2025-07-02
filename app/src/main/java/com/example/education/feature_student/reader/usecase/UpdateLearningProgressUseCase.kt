package com.example.education.feature_student.reader.usecase

import android.util.Log
import com.example.education.core.database.entities.LearningProgressEntity
import com.example.education.core.repository.CourseRepository
import com.example.education.core.user.RoleManager
import com.example.education.agent.AgentRepository
import com.example.education.agent.StudentContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 更新学习进度用例
 * 
 * 处理学生学习进度的更新，包括时间跟踪、完成度计算和AI学习分析
 */
@Singleton
class UpdateLearningProgressUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val roleManager: RoleManager,
    private val agentRepository: AgentRepository
) {
    
    companion object {
        private const val TAG = "UpdateLearningProgressUseCase"
        private const val MIN_STUDY_TIME_FOR_PROGRESS = 30000L // 30秒最小学习时间
        private const val COMPLETION_THRESHOLD = 0.95f // 95%完成度阈值
    }
    
    /**
     * 更新学习进度
     * 
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @param progressPercent 学习进度百分比
     * @param timeSpent 学习时间（毫秒）
     * @param isCompleted 是否完成
     * @return 更新结果
     */
    suspend fun updateProgress(
        courseId: String,
        chapterId: String,
        progressPercent: Float,
        timeSpent: Long,
        isCompleted: Boolean = false
    ): Result<LearningProgressEntity> {
        return try {
            Log.d(TAG, "更新学习进度: 课程=$courseId, 章节=$chapterId, 进度=$progressPercent%, 时间=${timeSpent}ms")
            
            // 获取当前用户ID
            val userId = roleManager.getCurrentUserId()
                ?: return Result.failure(Exception("用户未登录"))
            
            // 验证参数
            if (progressPercent < 0f || progressPercent > 100f) {
                return Result.failure(Exception("进度百分比必须在0-100之间"))
            }
            
            // 计算是否自动完成
            val autoCompleted = isCompleted || progressPercent >= COMPLETION_THRESHOLD * 100
            
            // 创建或更新学习进度实体
            val currentProgress = courseRepository.getLearningProgress(userId, chapterId)
            val updatedProgress = if (currentProgress != null) {
                // 更新现有进度
                val totalTimeSpent = currentProgress.timeSpent + timeSpent
                val newProgress = maxOf(currentProgress.progressPercent, progressPercent)
                
                currentProgress.copy(
                    progressPercent = newProgress,
                    timeSpent = totalTimeSpent,
                    isCompleted = autoCompleted,
                    lastAccessAt = System.currentTimeMillis()
                )
            } else {
                // 创建新的进度记录
                LearningProgressEntity(
                    userId = userId,
                    courseId = courseId,
                    chapterId = chapterId,
                    progressPercent = progressPercent,
                    timeSpent = timeSpent,
                    isCompleted = autoCompleted,
                    lastAccessAt = System.currentTimeMillis()
                )
            }
            
            // 保存学习进度
            courseRepository.updateLearningProgress(updatedProgress)
            
            // 如果学习时间足够，触发AI学习分析
            if (timeSpent >= MIN_STUDY_TIME_FOR_PROGRESS) {
                analyzeStudyBehavior(userId, courseId, chapterId, updatedProgress)
            }
            
            Log.d(TAG, "学习进度更新成功: $updatedProgress")
            Result.success(updatedProgress)
            
        } catch (e: Exception) {
            Log.e(TAG, "更新学习进度失败", e)
            Result.failure(e)
        }
    }
    
    /**
     * 批量更新学习进度
     */
    suspend fun batchUpdateProgress(
        progressList: List<LearningProgressEntity>
    ): Result<List<LearningProgressEntity>> {
        return try {
            Log.d(TAG, "批量更新学习进度: ${progressList.size}条记录")
            
            val updatedList = mutableListOf<LearningProgressEntity>()
            
            progressList.forEach { progress ->
                val result = updateProgress(
                    courseId = progress.courseId,
                    chapterId = progress.chapterId,
                    progressPercent = progress.progressPercent,
                    timeSpent = progress.timeSpent,
                    isCompleted = progress.isCompleted
                )
                
                result.getOrNull()?.let { updatedProgress ->
                    updatedList.add(updatedProgress)
                }
            }
            
            Result.success(updatedList)
            
        } catch (e: Exception) {
            Log.e(TAG, "批量更新学习进度失败", e)
            Result.failure(e)
        }
    }
    
    /**
     * 标记章节为已完成
     */
    suspend fun markChapterCompleted(
        courseId: String,
        chapterId: String
    ): Result<LearningProgressEntity> {
        return updateProgress(
            courseId = courseId,
            chapterId = chapterId,
            progressPercent = 100f,
            timeSpent = 0L,
            isCompleted = true
        )
    }
    
    /**
     * 计算学习效率指数
     * 
     * @param totalTimeSpent 总学习时间（毫秒）
     * @param completedChapters 完成章节数
     * @param averageProgress 平均进度
     * @return 学习效率指数（0.0-1.0）
     */
    fun calculateStudyEfficiency(
        totalTimeSpent: Long,
        completedChapters: Int,
        averageProgress: Float
    ): Float {
        if (totalTimeSpent <= 0 || completedChapters <= 0) return 0f
        
        // 基础效率：完成章节数 / 学习小时数
        val studyHours = totalTimeSpent / (1000 * 60 * 60).toFloat()
        val baseEfficiency = completedChapters / maxOf(studyHours, 1f)
        
        // 进度权重：平均进度影响效率
        val progressWeight = averageProgress / 100f
        
        // 综合效率指数
        val efficiency = (baseEfficiency * 0.7f + progressWeight * 0.3f)
        
        // 归一化到0.0-1.0范围
        return minOf(efficiency / 5f, 1f) // 假设5章节/小时为满分效率
    }
    
    /**
     * 获取学习建议
     */
    suspend fun getStudyRecommendation(
        userId: String,
        courseId: String
    ): Result<String> {
        return try {
            // 获取学习统计数据
            val totalTime = courseRepository.getTotalStudyTime(userId)
            val completedCount = courseRepository.getCompletedChapterCount(userId)
            val averageProgress = courseRepository.getAverageProgress(userId)
            
            // 计算学习效率
            val efficiency = calculateStudyEfficiency(totalTime, completedCount, averageProgress)
            
            // 生成学习建议
            val recommendation = when {
                efficiency >= 0.8f -> "学习效率很高！保持当前的学习节奏。"
                efficiency >= 0.6f -> "学习进展良好，可以适当增加学习时间。"
                efficiency >= 0.4f -> "建议制定更规律的学习计划，提高学习专注度。"
                else -> "学习进度较慢，建议寻求老师或同学的帮助。"
            }
            
            Result.success(recommendation)
            
        } catch (e: Exception) {
            Log.e(TAG, "获取学习建议失败", e)
            Result.failure(e)
        }
    }
    
    /**
     * AI学习行为分析
     */
    private suspend fun analyzeStudyBehavior(
        userId: String,
        courseId: String,
        chapterId: String,
        progress: LearningProgressEntity
    ) {
        try {
            Log.d(TAG, "开始AI学习行为分析")
            
            // 调用AI辅导智能体进行学习分析
            val analysisQuery = "分析学生的学习行为：" +
                    "当前章节进度${progress.progressPercent}%，" +
                    "学习时长${progress.timeSpent / 1000 / 60}分钟。" +
                    "请提供个性化的学习建议。"
            
            agentRepository.startTutoring(
                userId = userId,
                question = analysisQuery,
                conversationId = "${userId}_learning_analysis",
                studentLevel = "beginner"
            ).collect { response ->
                Log.d(TAG, "AI学习分析结果: $response")
                // 这里可以保存分析结果或推送给学生
            }
            
        } catch (e: Exception) {
            Log.w(TAG, "AI学习行为分析失败，但不影响进度更新", e)
        }
    }
    
    /**
     * 生成学习进度ID
     */
    private fun generateProgressId(userId: String, chapterId: String): String {
        return "${userId}_${chapterId}_${System.currentTimeMillis()}"
    }
} 