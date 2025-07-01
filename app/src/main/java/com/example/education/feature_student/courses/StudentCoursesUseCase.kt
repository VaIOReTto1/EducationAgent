package com.example.education.feature_student.courses

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
 * 学生课程用例类
 * 处理学生端课程相关的业务逻辑
 */
@Singleton
class StudentCoursesUseCase @Inject constructor(
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
     * 获取学生可访问的课程列表
     */
    suspend fun getAvailableCourses(): Flow<List<CourseWithProgress>> {
        val studentId = roleManager.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        
        return combine(
            courseDao.getPublishedCourses(),
            learningProgressDao.getProgressByUser(studentId)
        ) { courses, progressList ->
            courses.map { course ->
                val courseProgress = progressList.filter { it.courseId == course.id }
                val totalChapters = chapterDao.getChapterCountByCourse(course.id)
                val completedChapters = courseProgress.count { it.isCompleted }
                val averageProgress = if (courseProgress.isNotEmpty()) {
                    courseProgress.map { it.progressPercentage }.average().toFloat()
                } else 0f
                val totalTimeSpent = courseProgress.sumOf { it.timeSpentMinutes }
                val lastAccessTime = courseProgress.maxOfOrNull { it.lastAccessTime }
                val isEnrolled = courseProgress.isNotEmpty()
                
                CourseWithProgress(
                    course = course,
                    totalChapters = totalChapters,
                    completedChapters = completedChapters,
                    averageProgress = averageProgress,
                    totalTimeSpent = totalTimeSpent,
                    lastAccessTime = lastAccessTime,
                    isEnrolled = isEnrolled
                )
            }
        }
    }
    
    /**
     * 根据类别筛选课程
     */
    suspend fun getCoursesByCategory(category: String): Flow<List<CourseWithProgress>> {
        return getAvailableCourses().map { courses ->
            if (category == "全部") {
                courses
            } else {
                courses.filter { it.course.category == category }
            }
        }
    }
    
    /**
     * 根据难度筛选课程
     */
    suspend fun getCoursesByDifficulty(difficulty: String): Flow<List<CourseWithProgress>> {
        return getAvailableCourses().map { courses ->
            if (difficulty == "全部") {
                courses
            } else {
                courses.filter { it.course.difficulty == difficulty }
            }
        }
    }
    
    /**
     * 搜索课程
     */
    suspend fun searchCourses(query: String): Flow<List<CourseWithProgress>> {
        return getAvailableCourses().map { courses ->
            if (query.isBlank()) {
                courses
            } else {
                courses.filter { courseWithProgress ->
                    courseWithProgress.course.title.contains(query, ignoreCase = true) ||
                    courseWithProgress.course.description.contains(query, ignoreCase = true) ||
                    courseWithProgress.course.category.contains(query, ignoreCase = true)
                }
            }
        }
    }
    
    /**
     * 获取推荐课程
     */
    suspend fun getRecommendedCourses(): Flow<List<CourseWithProgress>> {
        val studentId = roleManager.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        
        return combine(
            getAvailableCourses(),
            learningProgressDao.getProgressByUser(studentId)
        ) { allCourses, userProgress ->
            // 基于学习历史推荐课程
            val enrolledCategories = userProgress.map { progress ->
                allCourses.find { it.course.id == progress.courseId }?.course?.category
            }.filterNotNull().distinct()
            
            val completedCourses = userProgress.filter { it.isCompleted }.map { it.courseId }
            
            // 推荐同类别的未完成课程
            allCourses.filter { courseWithProgress ->
                !courseWithProgress.isEnrolled && 
                enrolledCategories.contains(courseWithProgress.course.category) &&
                !completedCourses.contains(courseWithProgress.course.id)
            }.take(5)
        }
    }
    
    /**
     * 获取继续学习的课程
     */
    suspend fun getContinueLearningCourses(): Flow<List<CourseWithProgress>> {
        return getAvailableCourses().map { courses ->
            courses.filter { courseWithProgress ->
                courseWithProgress.isEnrolled && 
                !courseWithProgress.isCompleted() &&
                courseWithProgress.averageProgress > 0f
            }.sortedByDescending { it.lastAccessTime }
        }
    }
    
    /**
     * 注册课程
     */
    suspend fun enrollCourse(courseId: String): Result<Unit> {
        return try {
            val studentId = roleManager.getCurrentUserId() 
                ?: return Result.failure(Exception("学生ID不能为空"))
            
            // 检查课程是否存在且已发布
            val course = courseDao.getCourseById(courseId).first()
            if (course == null || !course.isPublished) {
                return Result.failure(Exception("课程不存在或未发布"))
            }
            
            // 检查是否已经注册
            val existingProgress = learningProgressDao.getProgressByCourseAndUser(courseId, studentId)
            if (existingProgress.isNotEmpty()) {
                return Result.failure(Exception("已经注册过该课程"))
            }
            
            // 获取课程的第一个章节
            val firstChapter = chapterDao.getFirstChapterByCourse(courseId)
            if (firstChapter != null) {
                // 创建第一个章节的学习进度记录
                val progress = LearningProgressEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = studentId,
                    courseId = courseId,
                    chapterId = firstChapter.id,
                    progressPercentage = 0f,
                    timeSpentMinutes = 0L,
                    isCompleted = false,
                    lastAccessTime = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                learningProgressDao.insertProgress(progress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取课程详情
     */
    suspend fun getCourseDetail(courseId: String): Flow<CourseDetail?> {
        val studentId = roleManager.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(null)
        
        return combine(
            courseDao.getCourseById(courseId),
            chapterDao.getChaptersByCourse(courseId),
            learningProgressDao.getProgressByCourseAndUser(courseId, studentId),
            assessmentDao.getAssessmentsByCourse(courseId)
        ) { course, chapters, progressList, assessments ->
            course?.let {
                val teacher = userDao.getUserById(course.teacherId)
                val completedChapters = progressList.count { it.isCompleted }
                val averageProgress = if (progressList.isNotEmpty()) {
                    progressList.map { it.progressPercentage }.average().toFloat()
                } else 0f
                val totalTimeSpent = progressList.sumOf { it.timeSpentMinutes }
                val lastAccessTime = progressList.maxOfOrNull { it.lastAccessTime }
                val isEnrolled = progressList.isNotEmpty()
                val nextChapter = findNextChapter(chapters, progressList)
                
                CourseDetail(
                    course = course,
                    teacher = teacher,
                    chapters = chapters,
                    assessments = assessments,
                    totalChapters = chapters.size,
                    completedChapters = completedChapters,
                    averageProgress = averageProgress,
                    totalTimeSpent = totalTimeSpent,
                    lastAccessTime = lastAccessTime,
                    isEnrolled = isEnrolled,
                    nextChapter = nextChapter
                )
            }
        }
    }
    
    /**
     * 获取学习统计
     */
    suspend fun getLearningStatistics(): Flow<LearningStatistics> {
        val studentId = roleManager.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(
            LearningStatistics(0, 0, 0f, 0L, 0, 0f)
        )
        
        return combine(
            learningProgressDao.getProgressByUser(studentId),
            assessmentResultDao.getResultsByUser(studentId)
        ) { progressList, assessmentResults ->
            val enrolledCourses = progressList.map { it.courseId }.distinct().size
            val completedCourses = progressList.groupBy { it.courseId }
                .count { (_, courseProgress) -> courseProgress.all { it.isCompleted } }
            val averageProgress = if (progressList.isNotEmpty()) {
                progressList.map { it.progressPercentage }.average().toFloat()
            } else 0f
            val totalTimeSpent = progressList.sumOf { it.timeSpentMinutes }
            val totalAssessments = assessmentResults.size
            val averageScore = if (assessmentResults.isNotEmpty()) {
                assessmentResults.map { it.score }.average().toFloat()
            } else 0f
            
            LearningStatistics(
                enrolledCourses = enrolledCourses,
                completedCourses = completedCourses,
                averageProgress = averageProgress,
                totalTimeSpent = totalTimeSpent,
                totalAssessments = totalAssessments,
                averageScore = averageScore
            )
        }
    }
    
    /**
     * 查找下一个要学习的章节
     */
    private fun findNextChapter(
        chapters: List<ChapterEntity>,
        progressList: List<LearningProgressEntity>
    ): ChapterEntity? {
        val sortedChapters = chapters.sortedBy { it.orderIndex }
        val completedChapterIds = progressList.filter { it.isCompleted }.map { it.chapterId }
        
        return sortedChapters.find { chapter ->
            !completedChapterIds.contains(chapter.id)
        }
    }
}

/**
 * 带进度的课程数据类
 */
data class CourseWithProgress(
    val course: CourseEntity,
    val totalChapters: Int,
    val completedChapters: Int,
    val averageProgress: Float,
    val totalTimeSpent: Long,
    val lastAccessTime: Long?,
    val isEnrolled: Boolean
) {
    fun isCompleted(): Boolean = completedChapters >= totalChapters && totalChapters > 0
    fun getCompletionRate(): Float = if (totalChapters > 0) {
        (completedChapters.toFloat() / totalChapters.toFloat()) * 100f
    } else 0f
}

/**
 * 课程详情数据类
 */
data class CourseDetail(
    val course: CourseEntity,
    val teacher: UserEntity?,
    val chapters: List<ChapterEntity>,
    val assessments: List<AssessmentEntity>,
    val totalChapters: Int,
    val completedChapters: Int,
    val averageProgress: Float,
    val totalTimeSpent: Long,
    val lastAccessTime: Long?,
    val isEnrolled: Boolean,
    val nextChapter: ChapterEntity?
)

/**
 * 学习统计数据类
 */
data class LearningStatistics(
    val enrolledCourses: Int,
    val completedCourses: Int,
    val averageProgress: Float,
    val totalTimeSpent: Long,
    val totalAssessments: Int,
    val averageScore: Float
)