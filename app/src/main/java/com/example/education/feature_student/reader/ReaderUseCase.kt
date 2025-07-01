package com.example.education.feature_student.reader

import com.example.education.agent.StudentAgent
import com.example.education.agent.TutoringAgent
import com.example.education.core.common.RoleManager
import com.example.education.core.database.dao.ChapterDao
import com.example.education.core.database.dao.LearningProgressDao
import com.example.education.core.database.entity.ChapterEntity
import com.example.education.core.database.entity.LearningProgressEntity
import com.example.education.core.network.model.StreamChatMessageResponse
import com.example.education.core.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 阅读器用例类
 * 处理章节阅读相关的业务逻辑
 */
@Singleton
class ReaderUseCase @Inject constructor(
    private val chapterDao: ChapterDao,
    private val learningProgressDao: LearningProgressDao,
    private val studentAgent: StudentAgent,
    private val tutoringAgent: TutoringAgent,
    private val roleManager: RoleManager,
    private val syncManager: SyncManager
) {

    /**
     * 获取章节详情
     * @param chapterId 章节ID
     * @return 章节实体
     */
    suspend fun getChapterById(chapterId: String): ChapterEntity? {
        return chapterDao.getChapterById(chapterId)
    }

    /**
     * 获取课程的所有已发布章节
     * @param courseId 课程ID
     * @return 章节列表流
     */
    fun getPublishedChaptersByCourse(courseId: String): Flow<List<ChapterEntity>> {
        return chapterDao.getPublishedChaptersByCourse(courseId)
    }

    /**
     * 更新学习进度
     * @param chapterId 章节ID
     * @param courseId 课程ID
     * @param progressPercentage 进度百分比（0.0-1.0）
     * @param timeSpentMinutes 花费时间（分钟）
     */
    suspend fun updateLearningProgress(
        chapterId: String,
        courseId: String,
        progressPercentage: Float,
        timeSpentMinutes: Int
    ) {
        val userId = roleManager.currentUserId.first() ?: return
        
        // 获取现有进度或创建新的进度记录
        val existingProgress = learningProgressDao.getProgressByChapter(userId, courseId, chapterId)
        
        val progressEntity = if (existingProgress != null) {
            existingProgress.copy(
                progressPercentage = progressPercentage,
                timeSpentMinutes = existingProgress.timeSpentMinutes + timeSpentMinutes,
                isCompleted = progressPercentage >= 1.0f,
                lastAccessedAt = System.currentTimeMillis(),
                completedAt = if (progressPercentage >= 1.0f) System.currentTimeMillis() else existingProgress.completedAt,
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        } else {
            LearningProgressEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                courseId = courseId,
                chapterId = chapterId,
                progressPercentage = progressPercentage,
                timeSpentMinutes = timeSpentMinutes,
                isCompleted = progressPercentage >= 1.0f,
                lastAccessedAt = System.currentTimeMillis(),
                completedAt = if (progressPercentage >= 1.0f) System.currentTimeMillis() else null,
                isSynced = false
            )
        }
        
        learningProgressDao.insertProgress(progressEntity)
        
        // 如果章节完成，启动下一章节的预取
        if (progressPercentage >= 1.0f) {
            val chapter = chapterDao.getChapterById(chapterId)
            chapter?.let {
                syncManager.startChapterPrefetch(courseId, it.orderIndex)
            }
        }
    }

    /**
     * 获取学习进度
     * @param chapterId 章节ID
     * @param courseId 课程ID
     * @return 学习进度实体
     */
    suspend fun getLearningProgress(chapterId: String, courseId: String): LearningProgressEntity? {
        val userId = roleManager.currentUserId.first() ?: return null
        return learningProgressDao.getProgressByChapter(userId, courseId, chapterId)
    }

    /**
     * 获取课程总体进度
     * @param courseId 课程ID
     * @return 进度百分比
     */
    suspend fun getCourseProgress(courseId: String): Float {
        val userId = roleManager.currentUserId.first() ?: return 0f
        return learningProgressDao.getCourseProgressPercentage(userId, courseId) ?: 0f
    }

    /**
     * 请求AI辅导
     * @param question 学生问题
     * @param chapterContent 当前章节内容（用于上下文）
     * @param conversationId 会话ID（可选）
     * @return AI回复流
     */
    suspend fun requestTutoring(
        question: String,
        chapterContent: String? = null,
        conversationId: String? = null
    ): Flow<StreamChatMessageResponse> {
        val userId = roleManager.currentUserId.first() ?: throw IllegalStateException("用户未登录")
        
        // 构建包含上下文的问题
        val contextualQuestion = if (chapterContent != null) {
            "基于以下章节内容：\n$chapterContent\n\n学生问题：$question"
        } else {
            question
        }
        
        return tutoringAgent.answerQuestion(
            question = contextualQuestion,
            userId = userId,
            conversationId = conversationId
        )
    }

    /**
     * 请求学习建议
     * @param currentChapter 当前章节
     * @param progress 学习进度
     * @return AI建议流
     */
    suspend fun requestLearningAdvice(
        currentChapter: ChapterEntity,
        progress: LearningProgressEntity?
    ): Flow<StreamChatMessageResponse> {
        val userId = roleManager.currentUserId.first() ?: throw IllegalStateException("用户未登录")
        
        val progressText = if (progress != null) {
            "当前进度：${(progress.progressPercentage * 100).toInt()}%，" +
            "已学习时间：${progress.timeSpentMinutes}分钟"
        } else {
            "尚未开始学习"
        }
        
        return studentAgent.getLearningAdvice(
            currentTopic = currentChapter.title,
            progress = progressText,
            userId = userId
        )
    }

    /**
     * 请求练习题
     * @param chapterTitle 章节标题
     * @return 练习题流
     */
    suspend fun requestPracticeQuestions(chapterTitle: String): Flow<StreamChatMessageResponse> {
        val userId = roleManager.currentUserId.first() ?: throw IllegalStateException("用户未登录")
        
        return studentAgent.requestPractice(
            topic = chapterTitle,
            userId = userId
        )
    }

    /**
     * 标记章节为已完成
     * @param chapterId 章节ID
     * @param courseId 课程ID
     */
    suspend fun markChapterAsCompleted(chapterId: String, courseId: String) {
        updateLearningProgress(
            chapterId = chapterId,
            courseId = courseId,
            progressPercentage = 1.0f,
            timeSpentMinutes = 0 // 不增加额外时间
        )
    }

    /**
     * 获取下一个章节
     * @param courseId 课程ID
     * @param currentOrderIndex 当前章节顺序
     * @return 下一个章节
     */
    suspend fun getNextChapter(courseId: String, currentOrderIndex: Int): ChapterEntity? {
        val nextChapters = chapterDao.getNextChapters(courseId, currentOrderIndex, 1)
        return nextChapters.firstOrNull()
    }

    /**
     * 获取上一个章节
     * @param courseId 课程ID
     * @param currentOrderIndex 当前章节顺序
     * @return 上一个章节
     */
    suspend fun getPreviousChapter(courseId: String, currentOrderIndex: Int): ChapterEntity? {
        // 这里需要实现获取前一个章节的逻辑
        // 由于DAO中没有直接的方法，我们可以获取所有章节然后筛选
        val allChapters = chapterDao.getPublishedChaptersByCourse(courseId).first()
        return allChapters.filter { it.orderIndex < currentOrderIndex }
            .maxByOrNull { it.orderIndex }
    }
}