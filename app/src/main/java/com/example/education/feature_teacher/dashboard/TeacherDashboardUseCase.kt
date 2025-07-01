package com.example.education.feature_teacher.dashboard

import com.example.education.core.database.dao.*
import com.example.education.core.database.entity.*
import com.example.education.core.network.service.DifyApiService
import com.example.education.core.common.RoleManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 教师仪表盘用例类
 * 处理教师端仪表盘相关的业务逻辑
 */
@Singleton
class TeacherDashboardUseCase @Inject constructor(
    private val courseDao: CourseDao,
    private val chapterDao: ChapterDao,
    private val userDao: UserDao,
    private val learningProgressDao: LearningProgressDao,
    private val assessmentDao: AssessmentDao,
    private val assessmentResultDao: AssessmentResultDao,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val difyApiService: DifyApiService,
    private val roleManager: RoleManager
) {
    
    /**
     * 获取教师的课程列表
     */
    suspend fun getTeacherCourses(): Flow<List<CourseEntity>> {
        val teacherId = roleManager.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return courseDao.getCoursesByTeacher(teacherId)
    }
    
    /**
     * 获取课程统计信息
     */
    suspend fun getCourseStatistics(courseId: String): Flow<CourseStatistics> {
        return combine(
            courseDao.getCourseById(courseId),
            chapterDao.getChaptersByCourse(courseId),
            learningProgressDao.getProgressByCourse(courseId),
            assessmentDao.getAssessmentsByCourse(courseId)
        ) { course, chapters, progressList, assessments ->
            val totalStudents = progressList.map { it.userId }.distinct().size
            val completedChapters = progressList.count { it.isCompleted }
            val averageProgress = if (progressList.isNotEmpty()) {
                progressList.map { it.progressPercentage }.average().toFloat()
            } else 0f
            
            CourseStatistics(
                course = course,
                totalChapters = chapters.size,
                totalStudents = totalStudents,
                completedChapters = completedChapters,
                averageProgress = averageProgress,
                totalAssessments = assessments.size
            )
        }
    }
    
    /**
     * 获取学生学习进度概览
     */
    suspend fun getStudentProgressOverview(courseId: String): Flow<List<StudentProgress>> {
        return combine(
            userDao.getStudentsByCourse(courseId),
            learningProgressDao.getProgressByCourse(courseId)
        ) { students, progressList ->
            students.map { student ->
                val studentProgress = progressList.filter { it.userId == student.id }
                val averageProgress = if (studentProgress.isNotEmpty()) {
                    studentProgress.map { it.progressPercentage }.average().toFloat()
                } else 0f
                val completedChapters = studentProgress.count { it.isCompleted }
                val totalTimeSpent = studentProgress.sumOf { it.timeSpentMinutes }
                
                StudentProgress(
                    student = student,
                    averageProgress = averageProgress,
                    completedChapters = completedChapters,
                    totalTimeSpent = totalTimeSpent,
                    lastActiveTime = studentProgress.maxOfOrNull { it.lastAccessTime }
                )
            }
        }
    }
    
    /**
     * 获取待批改的评估
     */
    suspend fun getPendingAssessments(): Flow<List<PendingAssessment>> {
        val teacherId = roleManager.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return combine(
            assessmentDao.getAssessmentsByTeacher(teacherId),
            assessmentResultDao.getPendingResults()
        ) { assessments, pendingResults ->
            pendingResults.mapNotNull { result ->
                val assessment = assessments.find { it.id == result.assessmentId }
                assessment?.let {
                    PendingAssessment(
                        assessmentResult = result,
                        assessment = it,
                        student = userDao.getUserById(result.userId)
                    )
                }
            }
        }
    }
    
    /**
     * 获取最近的学生提问
     */
    suspend fun getRecentStudentQuestions(limit: Int = 10): Flow<List<StudentQuestion>> {
        val teacherId = roleManager.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return messageDao.getRecentQuestionsByTeacher(teacherId, limit).map { messages ->
            messages.map { message ->
                val conversation = conversationDao.getConversationById(message.conversationId)
                val student = userDao.getUserById(message.userId)
                StudentQuestion(
                    message = message,
                    conversation = conversation,
                    student = student
                )
            }
        }
    }
    
    /**
     * 创建新课程
     */
    suspend fun createCourse(
        title: String,
        description: String,
        category: String,
        difficulty: String
    ): Result<CourseEntity> {
        return try {
            val teacherId = roleManager.getCurrentUserId() 
                ?: return Result.failure(Exception("教师ID不能为空"))
            
            val course = CourseEntity(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                description = description,
                teacherId = teacherId,
                category = category,
                difficulty = difficulty,
                isPublished = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            courseDao.insertCourse(course)
            Result.success(course)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 发布课程
     */
    suspend fun publishCourse(courseId: String): Result<Unit> {
        return try {
            courseDao.updateCoursePublishStatus(courseId, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取课程分析数据
     */
    suspend fun getCourseAnalytics(courseId: String, days: Int = 30): Flow<CourseAnalytics> {
        val startTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        
        return combine(
            learningProgressDao.getProgressByCourseAndTimeRange(courseId, startTime),
            assessmentResultDao.getResultsByCourseAndTimeRange(courseId, startTime),
            messageDao.getMessagesByCourseAndTimeRange(courseId, startTime)
        ) { progressList, assessmentResults, messages ->
            CourseAnalytics(
                courseId = courseId,
                totalViews = progressList.size,
                uniqueStudents = progressList.map { it.userId }.distinct().size,
                averageTimeSpent = if (progressList.isNotEmpty()) {
                    progressList.map { it.timeSpentMinutes }.average().toFloat()
                } else 0f,
                completionRate = if (progressList.isNotEmpty()) {
                    progressList.count { it.isCompleted }.toFloat() / progressList.size
                } else 0f,
                averageScore = if (assessmentResults.isNotEmpty()) {
                    assessmentResults.map { it.score }.average().toFloat()
                } else 0f,
                totalQuestions = messages.size,
                engagementScore = calculateEngagementScore(progressList, assessmentResults, messages)
            )
        }
    }
    
    /**
     * 计算参与度分数
     */
    private fun calculateEngagementScore(
        progressList: List<LearningProgressEntity>,
        assessmentResults: List<AssessmentResultEntity>,
        messages: List<MessageEntity>
    ): Float {
        val progressScore = if (progressList.isNotEmpty()) {
            progressList.map { it.progressPercentage }.average().toFloat()
        } else 0f
        
        val assessmentScore = if (assessmentResults.isNotEmpty()) {
            assessmentResults.map { it.score }.average().toFloat()
        } else 0f
        
        val interactionScore = minOf(messages.size.toFloat() / 10f, 1f) * 100f
        
        return (progressScore * 0.4f + assessmentScore * 0.4f + interactionScore * 0.2f)
    }
}

/**
 * 课程统计信息数据类
 */
data class CourseStatistics(
    val course: CourseEntity?,
    val totalChapters: Int,
    val totalStudents: Int,
    val completedChapters: Int,
    val averageProgress: Float,
    val totalAssessments: Int
)

/**
 * 学生进度数据类
 */
data class StudentProgress(
    val student: UserEntity,
    val averageProgress: Float,
    val completedChapters: Int,
    val totalTimeSpent: Long,
    val lastActiveTime: Long?
)

/**
 * 待批改评估数据类
 */
data class PendingAssessment(
    val assessmentResult: AssessmentResultEntity,
    val assessment: AssessmentEntity,
    val student: UserEntity?
)

/**
 * 学生提问数据类
 */
data class StudentQuestion(
    val message: MessageEntity,
    val conversation: ConversationEntity?,
    val student: UserEntity?
)

/**
 * 课程分析数据类
 */
data class CourseAnalytics(
    val courseId: String,
    val totalViews: Int,
    val uniqueStudents: Int,
    val averageTimeSpent: Float,
    val completionRate: Float,
    val averageScore: Float,
    val totalQuestions: Int,
    val engagementScore: Float
)