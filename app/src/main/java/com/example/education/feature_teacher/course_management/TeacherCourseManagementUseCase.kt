package com.example.education.feature_teacher.course_management

import com.example.education.core.database.dao.*
import com.example.education.core.database.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 教师课程管理业务逻辑
 * 处理课程创建、编辑、发布、学生管理等功能
 */
@Singleton
class TeacherCourseManagementUseCase @Inject constructor(
    private val courseDao: CourseDao,
    private val chapterDao: ChapterDao,
    private val assessmentDao: AssessmentDao,
    private val learningProgressDao: LearningProgressDao,
    private val enrollmentDao: EnrollmentDao,
    private val userDao: UserDao
) {
    
    /**
     * 获取教师的所有课程
     */
    fun getTeacherCourses(teacherId: String): Flow<List<TeacherCourseDetail>> {
        return courseDao.getCoursesByTeacher(teacherId).map { courses ->
            courses.map { course ->
                val chapters = chapterDao.getChaptersByCourseSync(course.id)
                val assessments = assessmentDao.getAssessmentsByCourseSync(course.id)
                val enrollments = enrollmentDao.getEnrollmentsByCourseSync(course.id)
                val students = enrollments.mapNotNull { enrollment ->
                    userDao.getUserByIdSync(enrollment.studentId)
                }
                
                // 计算课程统计
                val totalStudents = students.size
                val activeStudents = enrollments.count { enrollment ->
                    val lastAccess = learningProgressDao.getLastAccessTimeByCourseSync(course.id, enrollment.studentId)
                    lastAccess != null && (System.currentTimeMillis() - lastAccess) < 7 * 24 * 60 * 60 * 1000 // 7天内活跃
                }
                
                val completedStudents = enrollments.count { enrollment ->
                    val progress = learningProgressDao.getCourseProgressSync(course.id, enrollment.studentId)
                    progress >= 100f
                }
                
                val averageProgress = if (totalStudents > 0) {
                    enrollments.map { enrollment ->
                        learningProgressDao.getCourseProgressSync(course.id, enrollment.studentId)
                    }.average().toFloat()
                } else 0f
                
                val totalLearningTime = enrollments.sumOf { enrollment ->
                    learningProgressDao.getTotalLearningTimeSync(course.id, enrollment.studentId)
                }
                
                val averageLearningTime = if (totalStudents > 0) {
                    totalLearningTime / totalStudents
                } else 0L
                
                val pendingAssessments = assessments.count { assessment ->
                    // TODO: 检查是否有待批改的提交
                    false
                }
                
                TeacherCourseDetail(
                    course = course,
                    chapters = chapters,
                    assessments = assessments,
                    students = students,
                    totalStudents = totalStudents,
                    activeStudents = activeStudents,
                    completedStudents = completedStudents,
                    averageProgress = averageProgress,
                    totalLearningTime = totalLearningTime,
                    averageLearningTime = averageLearningTime,
                    pendingAssessments = pendingAssessments,
                    lastUpdated = course.updatedAt
                )
            }
        }
    }
    
    /**
     * 获取单个课程详情
     */
    suspend fun getCourseDetail(courseId: String): Result<TeacherCourseDetail> {
        return try {
            val course = courseDao.getCourseByIdSync(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            val chapters = chapterDao.getChaptersByCourseSync(courseId)
            val assessments = assessmentDao.getAssessmentsByCourseSync(courseId)
            val enrollments = enrollmentDao.getEnrollmentsByCourseSync(courseId)
            val students = enrollments.mapNotNull { enrollment ->
                userDao.getUserByIdSync(enrollment.studentId)
            }
            
            // 计算统计数据
            val totalStudents = students.size
            val activeStudents = enrollments.count { enrollment ->
                val lastAccess = learningProgressDao.getLastAccessTimeByCourseSync(courseId, enrollment.studentId)
                lastAccess != null && (System.currentTimeMillis() - lastAccess) < 7 * 24 * 60 * 60 * 1000
            }
            
            val completedStudents = enrollments.count { enrollment ->
                val progress = learningProgressDao.getCourseProgressSync(courseId, enrollment.studentId)
                progress >= 100f
            }
            
            val averageProgress = if (totalStudents > 0) {
                enrollments.map { enrollment ->
                    learningProgressDao.getCourseProgressSync(courseId, enrollment.studentId)
                }.average().toFloat()
            } else 0f
            
            val totalLearningTime = enrollments.sumOf { enrollment ->
                learningProgressDao.getTotalLearningTimeSync(courseId, enrollment.studentId)
            }
            
            val averageLearningTime = if (totalStudents > 0) {
                totalLearningTime / totalStudents
            } else 0L
            
            val pendingAssessments = assessments.count { assessment ->
                // TODO: 检查是否有待批改的提交
                false
            }
            
            val courseDetail = TeacherCourseDetail(
                course = course,
                chapters = chapters,
                assessments = assessments,
                students = students,
                totalStudents = totalStudents,
                activeStudents = activeStudents,
                completedStudents = completedStudents,
                averageProgress = averageProgress,
                totalLearningTime = totalLearningTime,
                averageLearningTime = averageLearningTime,
                pendingAssessments = pendingAssessments,
                lastUpdated = course.updatedAt
            )
            
            Result.success(courseDetail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 创建新课程
     */
    suspend fun createCourse(
        title: String,
        description: String,
        category: String,
        difficulty: String,
        teacherId: String
    ): Result<String> {
        return try {
            // 验证输入
            if (title.isBlank()) {
                return Result.failure(Exception("课程标题不能为空"))
            }
            if (description.isBlank()) {
                return Result.failure(Exception("课程描述不能为空"))
            }
            if (category.isBlank()) {
                return Result.failure(Exception("课程类别不能为空"))
            }
            if (difficulty.isBlank()) {
                return Result.failure(Exception("课程难度不能为空"))
            }
            
            val courseId = generateCourseId()
            val now = System.currentTimeMillis()
            
            val course = CourseEntity(
                id = courseId,
                title = title,
                description = description,
                category = category,
                difficulty = difficulty,
                teacherId = teacherId,
                isPublished = false,
                createdAt = now,
                updatedAt = now
            )
            
            courseDao.insertCourse(course)
            
            Result.success(courseId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新课程信息
     */
    suspend fun updateCourse(
        courseId: String,
        title: String,
        description: String,
        category: String,
        difficulty: String
    ): Result<Unit> {
        return try {
            val course = courseDao.getCourseByIdSync(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            // 验证输入
            if (title.isBlank()) {
                return Result.failure(Exception("课程标题不能为空"))
            }
            if (description.isBlank()) {
                return Result.failure(Exception("课程描述不能为空"))
            }
            
            val updatedCourse = course.copy(
                title = title,
                description = description,
                category = category,
                difficulty = difficulty,
                updatedAt = System.currentTimeMillis()
            )
            
            courseDao.updateCourse(updatedCourse)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 发布/取消发布课程
     */
    suspend fun toggleCoursePublication(courseId: String): Result<Boolean> {
        return try {
            val course = courseDao.getCourseByIdSync(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            // 检查是否可以发布
            if (!course.isPublished) {
                val chapters = chapterDao.getChaptersByCourseSync(courseId)
                if (chapters.isEmpty()) {
                    return Result.failure(Exception("课程至少需要包含一个章节才能发布"))
                }
            }
            
            val updatedCourse = course.copy(
                isPublished = !course.isPublished,
                updatedAt = System.currentTimeMillis()
            )
            
            courseDao.updateCourse(updatedCourse)
            
            Result.success(updatedCourse.isPublished)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除课程
     */
    suspend fun deleteCourse(courseId: String): Result<Unit> {
        return try {
            val course = courseDao.getCourseByIdSync(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            // 检查是否有学生注册
            val enrollments = enrollmentDao.getEnrollmentsByCourseSync(courseId)
            if (enrollments.isNotEmpty()) {
                return Result.failure(Exception("无法删除已有学生注册的课程"))
            }
            
            // 删除相关数据
            chapterDao.deleteChaptersByCourse(courseId)
            assessmentDao.deleteAssessmentsByCourse(courseId)
            learningProgressDao.deleteProgressByCourse(courseId)
            courseDao.deleteCourse(course)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 添加章节
     */
    suspend fun addChapter(
        courseId: String,
        title: String,
        content: String,
        orderIndex: Int
    ): Result<String> {
        return try {
            // 验证课程存在
            val course = courseDao.getCourseByIdSync(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            // 验证输入
            if (title.isBlank()) {
                return Result.failure(Exception("章节标题不能为空"))
            }
            if (content.isBlank()) {
                return Result.failure(Exception("章节内容不能为空"))
            }
            
            val chapterId = generateChapterId()
            val now = System.currentTimeMillis()
            
            val chapter = ChapterEntity(
                id = chapterId,
                courseId = courseId,
                title = title,
                content = content,
                orderIndex = orderIndex,
                createdAt = now,
                updatedAt = now
            )
            
            chapterDao.insertChapter(chapter)
            
            // 更新课程修改时间
            courseDao.updateCourse(course.copy(updatedAt = now))
            
            Result.success(chapterId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新章节
     */
    suspend fun updateChapter(
        chapterId: String,
        title: String,
        content: String,
        orderIndex: Int
    ): Result<Unit> {
        return try {
            val chapter = chapterDao.getChapterByIdSync(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            // 验证输入
            if (title.isBlank()) {
                return Result.failure(Exception("章节标题不能为空"))
            }
            if (content.isBlank()) {
                return Result.failure(Exception("章节内容不能为空"))
            }
            
            val now = System.currentTimeMillis()
            val updatedChapter = chapter.copy(
                title = title,
                content = content,
                orderIndex = orderIndex,
                updatedAt = now
            )
            
            chapterDao.updateChapter(updatedChapter)
            
            // 更新课程修改时间
            val course = courseDao.getCourseByIdSync(chapter.courseId)
            course?.let {
                courseDao.updateCourse(it.copy(updatedAt = now))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除章节
     */
    suspend fun deleteChapter(chapterId: String): Result<Unit> {
        return try {
            val chapter = chapterDao.getChapterByIdSync(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            // 删除相关学习进度
            learningProgressDao.deleteProgressByChapter(chapterId)
            
            // 删除章节
            chapterDao.deleteChapter(chapter)
            
            // 更新课程修改时间
            val course = courseDao.getCourseByIdSync(chapter.courseId)
            course?.let {
                courseDao.updateCourse(it.copy(updatedAt = System.currentTimeMillis()))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取课程学生列表
     */
    fun getCourseStudents(courseId: String): Flow<List<StudentProgress>> {
        return combine(
            enrollmentDao.getEnrollmentsByCourse(courseId),
            learningProgressDao.getProgressByCourse(courseId)
        ) { enrollments, progressList ->
            enrollments.mapNotNull { enrollment ->
                val student = userDao.getUserByIdSync(enrollment.studentId)
                if (student != null) {
                    val studentProgress = progressList.filter { it.studentId == enrollment.studentId }
                    val totalChapters = chapterDao.getChaptersByCourseSync(courseId).size
                    val completedChapters = studentProgress.count { it.isCompleted }
                    val averageProgress = if (totalChapters > 0) {
                        (completedChapters.toFloat() / totalChapters.toFloat()) * 100f
                    } else 0f
                    val totalTimeSpent = studentProgress.sumOf { it.timeSpent }
                    val lastAccessTime = studentProgress.maxOfOrNull { it.lastAccessTime }
                    
                    StudentProgress(
                        student = student,
                        enrollment = enrollment,
                        totalChapters = totalChapters,
                        completedChapters = completedChapters,
                        averageProgress = averageProgress,
                        totalTimeSpent = totalTimeSpent,
                        lastAccessTime = lastAccessTime
                    )
                } else null
            }
        }
    }
    
    /**
     * 移除学生
     */
    suspend fun removeStudent(courseId: String, studentId: String): Result<Unit> {
        return try {
            // 删除注册记录
            enrollmentDao.deleteEnrollment(courseId, studentId)
            
            // 删除学习进度
            learningProgressDao.deleteProgressByCourseAndStudent(courseId, studentId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 生成课程ID
     */
    private fun generateCourseId(): String {
        return "course_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    /**
     * 生成章节ID
     */
    private fun generateChapterId(): String {
        return "chapter_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

/**
 * 教师课程详情数据类
 */
data class TeacherCourseDetail(
    val course: CourseEntity,
    val chapters: List<ChapterEntity>,
    val assessments: List<AssessmentEntity>,
    val students: List<UserEntity>,
    val totalStudents: Int,
    val activeStudents: Int,
    val completedStudents: Int,
    val averageProgress: Float,
    val totalLearningTime: Long,
    val averageLearningTime: Long,
    val pendingAssessments: Int,
    val lastUpdated: Long
) {
    /**
     * 获取完成率
     */
    fun getCompletionRate(): Float {
        return if (totalStudents > 0) {
            (completedStudents.toFloat() / totalStudents.toFloat()) * 100f
        } else 0f
    }
    
    /**
     * 获取活跃率
     */
    fun getActiveRate(): Float {
        return if (totalStudents > 0) {
            (activeStudents.toFloat() / totalStudents.toFloat()) * 100f
        } else 0f
    }
    
    /**
     * 检查是否可以发布
     */
    fun canPublish(): Boolean {
        return chapters.isNotEmpty()
    }
    
    /**
     * 检查是否可以删除
     */
    fun canDelete(): Boolean {
        return totalStudents == 0
    }
}

/**
 * 学生进度数据类
 */
data class StudentProgress(
    val student: UserEntity,
    val enrollment: EnrollmentEntity,
    val totalChapters: Int,
    val completedChapters: Int,
    val averageProgress: Float,
    val totalTimeSpent: Long,
    val lastAccessTime: Long?
) {
    /**
     * 检查是否完成课程
     */
    fun isCompleted(): Boolean {
        return averageProgress >= 100f
    }
    
    /**
     * 检查是否活跃
     */
    fun isActive(): Boolean {
        return lastAccessTime != null && 
               (System.currentTimeMillis() - lastAccessTime) < 7 * 24 * 60 * 60 * 1000
    }
}