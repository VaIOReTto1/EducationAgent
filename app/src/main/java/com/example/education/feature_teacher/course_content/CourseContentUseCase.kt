package com.example.education.feature_teacher.course_content

import com.example.education.core.database.entity.*
import com.example.education.core.database.dao.CourseDao
import com.example.education.core.database.dao.ChapterDao
import com.example.education.core.database.dao.AssessmentDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 课程内容管理业务逻辑
 */
@Singleton
class CourseContentUseCase @Inject constructor(
    private val courseDao: CourseDao,
    private val chapterDao: ChapterDao,
    private val assessmentDao: AssessmentDao
) {
    
    /**
     * 获取课程内容详情
     */
    suspend fun getCourseContent(courseId: String): Result<CourseContentDetail> {
        return try {
            val course = courseDao.getCourseById(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            val chapters = chapterDao.getChaptersByCourseId(courseId)
            val assessments = assessmentDao.getAssessmentsByCourseId(courseId)
            
            // 计算课程统计信息
            val totalChapters = chapters.size
            val totalAssessments = assessments.size
            val totalWords = chapters.sumOf { it.content.length }
            val estimatedDuration = calculateEstimatedDuration(chapters)
            
            val courseContentDetail = CourseContentDetail(
                course = course,
                chapters = chapters,
                assessments = assessments,
                totalChapters = totalChapters,
                totalAssessments = totalAssessments,
                totalWords = totalWords,
                estimatedDuration = estimatedDuration,
                lastModified = maxOf(
                    course.updatedAt,
                    chapters.maxOfOrNull { it.updatedAt } ?: course.updatedAt,
                    assessments.maxOfOrNull { it.updatedAt } ?: course.updatedAt
                )
            )
            
            Result.success(courseContentDetail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取课程内容流
     */
    fun getCourseContentFlow(courseId: String): Flow<CourseContentDetail?> {
        return combine(
            courseRepository.getCourseFlow(courseId),
            chapterRepository.getChaptersFlow(courseId),
            assessmentRepository.getAssessmentsFlow(courseId)
        ) { course, chapters, assessments ->
            if (course != null) {
                val totalChapters = chapters.size
                val totalAssessments = assessments.size
                val totalWords = chapters.sumOf { it.content.length }
                val estimatedDuration = calculateEstimatedDuration(chapters)
                
                CourseContentDetail(
                    course = course,
                    chapters = chapters,
                    assessments = assessments,
                    totalChapters = totalChapters,
                    totalAssessments = totalAssessments,
                    totalWords = totalWords,
                    estimatedDuration = estimatedDuration,
                    lastModified = maxOf(
                        course.updatedAt,
                        chapters.maxOfOrNull { it.updatedAt } ?: course.updatedAt,
                        assessments.maxOfOrNull { it.updatedAt } ?: course.updatedAt
                    )
                )
            } else {
                null
            }
        }
    }
    
    /**
     * 创建章节
     */
    suspend fun createChapter(
        courseId: String,
        title: String,
        content: String,
        order: Int,
        description: String = ""
    ): Result<Chapter> {
        return try {
            val chapter = Chapter(
                id = generateChapterId(),
                courseId = courseId,
                title = title,
                content = content,
                description = description,
                order = order,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            chapterRepository.insertChapter(chapter)
            Result.success(chapter)
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
        description: String = ""
    ): Result<Chapter> {
        return try {
            val existingChapter = chapterRepository.getChapterById(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            val updatedChapter = existingChapter.copy(
                title = title,
                content = content,
                description = description,
                updatedAt = System.currentTimeMillis()
            )
            
            chapterRepository.updateChapter(updatedChapter)
            Result.success(updatedChapter)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除章节
     */
    suspend fun deleteChapter(chapterId: String): Result<Unit> {
        return try {
            // 检查章节是否存在
            val chapter = chapterRepository.getChapterById(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            // 删除相关的评估
            assessmentRepository.deleteAssessmentsByChapterId(chapterId)
            
            // 删除章节
            chapterRepository.deleteChapter(chapterId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 重新排序章节
     */
    suspend fun reorderChapters(
        courseId: String,
        chapterOrders: List<Pair<String, Int>>
    ): Result<Unit> {
        return try {
            chapterOrders.forEach { (chapterId, newOrder) ->
                val chapter = chapterRepository.getChapterById(chapterId)
                    ?: return Result.failure(Exception("章节 $chapterId 不存在"))
                
                val updatedChapter = chapter.copy(
                    order = newOrder,
                    updatedAt = System.currentTimeMillis()
                )
                
                chapterRepository.updateChapter(updatedChapter)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 创建评估
     */
    suspend fun createAssessment(
        courseId: String,
        chapterId: String?,
        title: String,
        description: String,
        type: AssessmentType,
        questions: List<AssessmentQuestion>,
        timeLimit: Int? = null,
        passingScore: Int = 60
    ): Result<Assessment> {
        return try {
            val assessment = Assessment(
                id = generateAssessmentId(),
                courseId = courseId,
                chapterId = chapterId,
                title = title,
                description = description,
                type = type,
                questions = questions,
                timeLimit = timeLimit,
                passingScore = passingScore,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            assessmentRepository.insertAssessment(assessment)
            Result.success(assessment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 更新评估
     */
    suspend fun updateAssessment(
        assessmentId: String,
        title: String,
        description: String,
        questions: List<AssessmentQuestion>,
        timeLimit: Int? = null,
        passingScore: Int = 60
    ): Result<Assessment> {
        return try {
            val existingAssessment = assessmentRepository.getAssessmentById(assessmentId)
                ?: return Result.failure(Exception("评估不存在"))
            
            val updatedAssessment = existingAssessment.copy(
                title = title,
                description = description,
                questions = questions,
                timeLimit = timeLimit,
                passingScore = passingScore,
                updatedAt = System.currentTimeMillis()
            )
            
            assessmentRepository.updateAssessment(updatedAssessment)
            Result.success(updatedAssessment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除评估
     */
    suspend fun deleteAssessment(assessmentId: String): Result<Unit> {
        return try {
            // 检查评估是否存在
            val assessment = assessmentRepository.getAssessmentById(assessmentId)
                ?: return Result.failure(Exception("评估不存在"))
            
            // 删除评估
            assessmentRepository.deleteAssessment(assessmentId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 复制章节
     */
    suspend fun duplicateChapter(
        chapterId: String,
        newTitle: String? = null
    ): Result<Chapter> {
        return try {
            val originalChapter = chapterRepository.getChapterById(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            // 获取课程中最大的order值
            val chapters = chapterRepository.getChaptersByCourseId(originalChapter.courseId)
            val maxOrder = chapters.maxOfOrNull { it.order } ?: 0
            
            val duplicatedChapter = originalChapter.copy(
                id = generateChapterId(),
                title = newTitle ?: "${originalChapter.title} (副本)",
                order = maxOrder + 1,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            chapterRepository.insertChapter(duplicatedChapter)
            
            // 复制相关的评估
            val assessments = assessmentRepository.getAssessmentsByChapterId(chapterId)
            assessments.forEach { assessment ->
                val duplicatedAssessment = assessment.copy(
                    id = generateAssessmentId(),
                    chapterId = duplicatedChapter.id,
                    title = "${assessment.title} (副本)",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                assessmentRepository.insertAssessment(duplicatedAssessment)
            }
            
            Result.success(duplicatedChapter)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 导入课程内容
     */
    suspend fun importCourseContent(
        courseId: String,
        contentData: CourseContentImportData
    ): Result<Unit> {
        return try {
            // 导入章节
            contentData.chapters.forEachIndexed { index, chapterData ->
                val chapter = Chapter(
                    id = generateChapterId(),
                    courseId = courseId,
                    title = chapterData.title,
                    content = chapterData.content,
                    description = chapterData.description,
                    order = index + 1,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                chapterRepository.insertChapter(chapter)
                
                // 导入章节相关的评估
                chapterData.assessments.forEach { assessmentData ->
                    val assessment = Assessment(
                        id = generateAssessmentId(),
                        courseId = courseId,
                        chapterId = chapter.id,
                        title = assessmentData.title,
                        description = assessmentData.description,
                        type = assessmentData.type,
                        questions = assessmentData.questions,
                        timeLimit = assessmentData.timeLimit,
                        passingScore = assessmentData.passingScore,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    assessmentRepository.insertAssessment(assessment)
                }
            }
            
            // 导入课程级别的评估
            contentData.courseAssessments.forEach { assessmentData ->
                val assessment = Assessment(
                    id = generateAssessmentId(),
                    courseId = courseId,
                    chapterId = null,
                    title = assessmentData.title,
                    description = assessmentData.description,
                    type = assessmentData.type,
                    questions = assessmentData.questions,
                    timeLimit = assessmentData.timeLimit,
                    passingScore = assessmentData.passingScore,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                assessmentRepository.insertAssessment(assessment)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 导出课程内容
     */
    suspend fun exportCourseContent(courseId: String): Result<CourseContentExportData> {
        return try {
            val course = courseRepository.getCourseById(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            val chapters = chapterRepository.getChaptersByCourseId(courseId)
            val assessments = assessmentRepository.getAssessmentsByCourseId(courseId)
            
            val chapterExportData = chapters.map { chapter ->
                val chapterAssessments = assessments.filter { it.chapterId == chapter.id }
                ChapterExportData(
                    title = chapter.title,
                    content = chapter.content,
                    description = chapter.description,
                    order = chapter.order,
                    assessments = chapterAssessments.map { assessment ->
                        AssessmentExportData(
                            title = assessment.title,
                            description = assessment.description,
                            type = assessment.type,
                            questions = assessment.questions,
                            timeLimit = assessment.timeLimit,
                            passingScore = assessment.passingScore
                        )
                    }
                )
            }
            
            val courseAssessments = assessments.filter { it.chapterId == null }
            val courseAssessmentExportData = courseAssessments.map { assessment ->
                AssessmentExportData(
                    title = assessment.title,
                    description = assessment.description,
                    type = assessment.type,
                    questions = assessment.questions,
                    timeLimit = assessment.timeLimit,
                    passingScore = assessment.passingScore
                )
            }
            
            val exportData = CourseContentExportData(
                courseTitle = course.title,
                courseDescription = course.description,
                chapters = chapterExportData,
                courseAssessments = courseAssessmentExportData,
                exportedAt = System.currentTimeMillis()
            )
            
            Result.success(exportData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取内容统计信息
     */
    suspend fun getContentStatistics(courseId: String): Result<ContentStatistics> {
        return try {
            val chapters = chapterRepository.getChaptersByCourseId(courseId)
            val assessments = assessmentRepository.getAssessmentsByCourseId(courseId)
            
            val totalWords = chapters.sumOf { it.content.length }
            val averageWordsPerChapter = if (chapters.isNotEmpty()) totalWords / chapters.size else 0
            val totalQuestions = assessments.sumOf { it.questions.size }
            val averageQuestionsPerAssessment = if (assessments.isNotEmpty()) totalQuestions / assessments.size else 0
            
            val chapterAssessments = assessments.filter { it.chapterId != null }
            val courseAssessments = assessments.filter { it.chapterId == null }
            
            val estimatedDuration = calculateEstimatedDuration(chapters)
            
            val statistics = ContentStatistics(
                totalChapters = chapters.size,
                totalAssessments = assessments.size,
                chapterAssessments = chapterAssessments.size,
                courseAssessments = courseAssessments.size,
                totalWords = totalWords,
                averageWordsPerChapter = averageWordsPerChapter,
                totalQuestions = totalQuestions,
                averageQuestionsPerAssessment = averageQuestionsPerAssessment,
                estimatedDuration = estimatedDuration,
                lastModified = maxOf(
                    chapters.maxOfOrNull { it.updatedAt } ?: 0L,
                    assessments.maxOfOrNull { it.updatedAt } ?: 0L
                )
            )
            
            Result.success(statistics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 计算预估学习时长（分钟）
     */
    private fun calculateEstimatedDuration(chapters: List<Chapter>): Int {
        // 假设平均阅读速度为每分钟200字
        val wordsPerMinute = 200
        val totalWords = chapters.sumOf { it.content.length }
        return (totalWords / wordsPerMinute).coerceAtLeast(1)
    }
    
    /**
     * 生成章节ID
     */
    private fun generateChapterId(): String {
        return "chapter_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    /**
     * 生成评估ID
     */
    private fun generateAssessmentId(): String {
        return "assessment_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

/**
 * 课程内容详情
 */
data class CourseContentDetail(
    val course: Course,
    val chapters: List<Chapter>,
    val assessments: List<Assessment>,
    val totalChapters: Int,
    val totalAssessments: Int,
    val totalWords: Int,
    val estimatedDuration: Int, // 分钟
    val lastModified: Long
)

/**
 * 内容统计信息
 */
data class ContentStatistics(
    val totalChapters: Int,
    val totalAssessments: Int,
    val chapterAssessments: Int,
    val courseAssessments: Int,
    val totalWords: Int,
    val averageWordsPerChapter: Int,
    val totalQuestions: Int,
    val averageQuestionsPerAssessment: Int,
    val estimatedDuration: Int, // 分钟
    val lastModified: Long
)

/**
 * 课程内容导入数据
 */
data class CourseContentImportData(
    val chapters: List<ChapterImportData>,
    val courseAssessments: List<AssessmentImportData>
)

/**
 * 章节导入数据
 */
data class ChapterImportData(
    val title: String,
    val content: String,
    val description: String,
    val assessments: List<AssessmentImportData>
)

/**
 * 评估导入数据
 */
data class AssessmentImportData(
    val title: String,
    val description: String,
    val type: AssessmentType,
    val questions: List<AssessmentQuestion>,
    val timeLimit: Int?,
    val passingScore: Int
)

/**
 * 课程内容导出数据
 */
data class CourseContentExportData(
    val courseTitle: String,
    val courseDescription: String,
    val chapters: List<ChapterExportData>,
    val courseAssessments: List<AssessmentExportData>,
    val exportedAt: Long
)

/**
 * 章节导出数据
 */
data class ChapterExportData(
    val title: String,
    val content: String,
    val description: String,
    val order: Int,
    val assessments: List<AssessmentExportData>
)

/**
 * 评估导出数据
 */
data class AssessmentExportData(
    val title: String,
    val description: String,
    val type: AssessmentType,
    val questions: List<AssessmentQuestion>,
    val timeLimit: Int?,
    val passingScore: Int
)