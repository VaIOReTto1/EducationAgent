package com.example.education.feature_student.chapter_reader

import com.example.education.core.database.dao.ChapterDao
import com.example.education.core.database.dao.LearningProgressDao
import com.example.education.core.database.dao.CourseDao
import com.example.education.core.database.dao.AssessmentDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 章节阅读模块的业务逻辑
 */
@Singleton
class ChapterReaderUseCase @Inject constructor(
    private val chapterDao: ChapterDao,
    private val learningProgressDao: LearningProgressDao,
    private val courseDao: CourseDao,
    private val assessmentDao: AssessmentDao
) {
    
    /**
     * 获取章节详情及相关信息
     */
    suspend fun getChapterDetail(
        chapterId: String,
        studentId: String
    ): Result<ChapterDetail> {
        return try {
            val chapter = chapterDao.getChapterById(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            val course = courseDao.getCourseById(chapter.courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            // 获取学习进度
            val progress = learningProgressDao.getProgress(studentId, chapter.courseId, chapterId)
            
            // 获取课程所有章节
            val allChapters = chapterDao.getChaptersByCourseId(chapter.courseId)
            
            // 获取当前章节的评估
            val assessments = assessmentDao.getAssessmentsByChapterId(chapterId)
            
            // 查找上一章节和下一章节
            val currentIndex = allChapters.indexOfFirst { it.id == chapterId }
            val previousChapter = if (currentIndex > 0) allChapters[currentIndex - 1] else null
            val nextChapter = if (currentIndex < allChapters.size - 1) allChapters[currentIndex + 1] else null
            
            // 计算阅读时间估算
            val estimatedReadingTime = estimateReadingTime(chapter.content)
            
            val chapterDetail = ChapterDetail(
                chapter = chapter,
                course = course,
                progress = progress,
                assessments = assessments,
                previousChapter = previousChapter,
                nextChapter = nextChapter,
                currentChapterIndex = currentIndex + 1,
                totalChapters = allChapters.size,
                estimatedReadingTime = estimatedReadingTime,
                isCompleted = progress?.isCompleted == true,
                readingProgress = progress?.readingProgress ?: 0f,
                timeSpent = progress?.timeSpent ?: 0L,
                lastAccessTime = progress?.lastAccessTime
            )
            
            Result.success(chapterDetail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取章节详情流
     */
    fun getChapterDetailFlow(
        chapterId: String,
        studentId: String
    ): Flow<ChapterDetail?> {
        return combine(
            chapterDao.getChapterByIdFlow(chapterId),
            learningProgressDao.getProgressFlow(studentId, "", chapterId)
        ) { chapter, progress ->
            if (chapter == null) return@combine null
            
            try {
                val course = courseDao.getCourseById(chapter.courseId) ?: return@combine null
                val allChapters = chapterDao.getChaptersByCourseId(chapter.courseId)
                val assessments = assessmentDao.getAssessmentsByChapterId(chapterId)
                
                val currentIndex = allChapters.indexOfFirst { it.id == chapterId }
                val previousChapter = if (currentIndex > 0) allChapters[currentIndex - 1] else null
                val nextChapter = if (currentIndex < allChapters.size - 1) allChapters[currentIndex + 1] else null
                
                ChapterDetail(
                    chapter = chapter,
                    course = course,
                    progress = progress,
                    assessments = assessments,
                    previousChapter = previousChapter,
                    nextChapter = nextChapter,
                    currentChapterIndex = currentIndex + 1,
                    totalChapters = allChapters.size,
                    estimatedReadingTime = estimateReadingTime(chapter.content),
                    isCompleted = progress?.isCompleted == true,
                    readingProgress = progress?.readingProgress ?: 0f,
                    timeSpent = progress?.timeSpent ?: 0L,
                    lastAccessTime = progress?.lastAccessTime
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * 更新阅读进度
     */
    suspend fun updateReadingProgress(
        studentId: String,
        courseId: String,
        chapterId: String,
        progress: Float,
        timeSpent: Long = 0L
    ): Result<Unit> {
        return try {
            val existingProgress = learningProgressDao.getProgress(studentId, courseId, chapterId)
            
            if (existingProgress != null) {
                // 更新现有进度
                val updatedProgress = existingProgress.copy(
                    readingProgress = maxOf(existingProgress.readingProgress, progress),
                    timeSpent = existingProgress.timeSpent + timeSpent,
                    lastAccessTime = System.currentTimeMillis(),
                    isCompleted = progress >= 1.0f
                )
                learningProgressDao.updateProgress(updatedProgress)
            } else {
                // 创建新的进度记录
                val newProgress = LearningProgress(
                    id = "", // 将由数据库生成
                    studentId = studentId,
                    courseId = courseId,
                    chapterId = chapterId,
                    readingProgress = progress,
                    timeSpent = timeSpent,
                    isCompleted = progress >= 1.0f,
                    lastAccessTime = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                )
                learningProgressDao.insertProgress(newProgress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 标记章节为已完成
     */
    suspend fun markChapterCompleted(
        studentId: String,
        courseId: String,
        chapterId: String,
        totalTimeSpent: Long
    ): Result<Unit> {
        return try {
            val existingProgress = learningProgressDao.getProgress(studentId, courseId, chapterId)
            
            if (existingProgress != null) {
                val updatedProgress = existingProgress.copy(
                    readingProgress = 1.0f,
                    timeSpent = totalTimeSpent,
                    isCompleted = true,
                    lastAccessTime = System.currentTimeMillis()
                )
                learningProgressDao.updateProgress(updatedProgress)
            } else {
                val newProgress = LearningProgress(
                    id = "",
                    studentId = studentId,
                    courseId = courseId,
                    chapterId = chapterId,
                    readingProgress = 1.0f,
                    timeSpent = totalTimeSpent,
                    isCompleted = true,
                    lastAccessTime = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                )
                learningProgressDao.insertProgress(newProgress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取章节笔记
     */
    suspend fun getChapterNotes(
        studentId: String,
        chapterId: String
    ): Result<List<ChapterNote>> {
        return try {
            // 这里应该从笔记数据库获取，暂时返回空列表
            // val notes = noteDao.getNotesByChapter(studentId, chapterId)
            val notes = emptyList<ChapterNote>()
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 添加章节笔记
     */
    suspend fun addChapterNote(
        studentId: String,
        chapterId: String,
        content: String,
        position: Int? = null
    ): Result<ChapterNote> {
        return try {
            val note = ChapterNote(
                id = generateNoteId(),
                studentId = studentId,
                chapterId = chapterId,
                content = content,
                position = position,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            // 这里应该保存到笔记数据库
            // noteDao.insertNote(note)
            
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取学习统计
     */
    suspend fun getLearningStatistics(
        studentId: String,
        courseId: String
    ): Result<LearningStatistics> {
        return try {
            val allChapters = chapterDao.getChaptersByCourseId(courseId)
            val progressList = learningProgressDao.getProgressByCourse(studentId, courseId)
            
            val totalChapters = allChapters.size
            val completedChapters = progressList.count { it.isCompleted }
            val totalTimeSpent = progressList.sumOf { it.timeSpent }
            val averageProgress = if (progressList.isNotEmpty()) {
                progressList.map { it.readingProgress }.average().toFloat()
            } else 0f
            
            val lastAccessTime = progressList.maxOfOrNull { it.lastAccessTime }
            val currentChapter = findCurrentChapter(allChapters, progressList)
            
            val statistics = LearningStatistics(
                totalChapters = totalChapters,
                completedChapters = completedChapters,
                totalTimeSpent = totalTimeSpent,
                averageProgress = averageProgress,
                lastAccessTime = lastAccessTime,
                currentChapter = currentChapter,
                estimatedTimeToComplete = estimateTimeToComplete(allChapters, progressList)
            )
            
            Result.success(statistics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 搜索章节内容
     */
    suspend fun searchInChapter(
        chapterId: String,
        query: String
    ): Result<List<SearchResult>> {
        return try {
            val chapter = chapterDao.getChapterById(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            val results = searchInContent(chapter.content, query)
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取章节目录
     */
    suspend fun getChapterOutline(chapterId: String): Result<List<OutlineItem>> {
        return try {
            val chapter = chapterDao.getChapterById(chapterId)
                ?: return Result.failure(Exception("章节不存在"))
            
            val outline = extractOutline(chapter.content)
            Result.success(outline)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 辅助函数
    
    /**
     * 估算阅读时间（分钟）
     */
    private fun estimateReadingTime(content: String): Int {
        val wordsPerMinute = 200 // 平均阅读速度
        val wordCount = content.split("\\s+".toRegex()).size
        return maxOf(1, wordCount / wordsPerMinute)
    }
    
    /**
     * 查找当前应该学习的章节
     */
    private fun findCurrentChapter(
        chapters: List<Chapter>,
        progressList: List<LearningProgress>
    ): Chapter? {
        // 找到第一个未完成的章节
        for (chapter in chapters) {
            val progress = progressList.find { it.chapterId == chapter.id }
            if (progress?.isCompleted != true) {
                return chapter
            }
        }
        return null // 所有章节都已完成
    }
    
    /**
     * 估算完成剩余章节所需时间
     */
    private fun estimateTimeToComplete(
        chapters: List<Chapter>,
        progressList: List<LearningProgress>
    ): Long {
        var totalEstimatedTime = 0L
        
        for (chapter in chapters) {
            val progress = progressList.find { it.chapterId == chapter.id }
            if (progress?.isCompleted != true) {
                val estimatedTime = estimateReadingTime(chapter.content) * 60 * 1000L // 转换为毫秒
                val remainingProgress = 1.0f - (progress?.readingProgress ?: 0f)
                totalEstimatedTime += (estimatedTime * remainingProgress).toLong()
            }
        }
        
        return totalEstimatedTime
    }
    
    /**
     * 在内容中搜索关键词
     */
    private fun searchInContent(content: String, query: String): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val lines = content.split("\n")
        
        lines.forEachIndexed { lineIndex, line ->
            val index = line.indexOf(query, ignoreCase = true)
            if (index != -1) {
                val start = maxOf(0, index - 20)
                val end = minOf(line.length, index + query.length + 20)
                val snippet = line.substring(start, end)
                
                results.add(
                    SearchResult(
                        lineNumber = lineIndex + 1,
                        snippet = snippet,
                        highlightStart = index - start,
                        highlightEnd = index - start + query.length
                    )
                )
            }
        }
        
        return results
    }
    
    /**
     * 提取章节大纲
     */
    private fun extractOutline(content: String): List<OutlineItem> {
        val outline = mutableListOf<OutlineItem>()
        val lines = content.split("\n")
        
        lines.forEachIndexed { index, line ->
            val trimmedLine = line.trim()
            when {
                trimmedLine.startsWith("# ") -> {
                    outline.add(
                        OutlineItem(
                            level = 1,
                            title = trimmedLine.substring(2),
                            lineNumber = index + 1
                        )
                    )
                }
                trimmedLine.startsWith("## ") -> {
                    outline.add(
                        OutlineItem(
                            level = 2,
                            title = trimmedLine.substring(3),
                            lineNumber = index + 1
                        )
                    )
                }
                trimmedLine.startsWith("### ") -> {
                    outline.add(
                        OutlineItem(
                            level = 3,
                            title = trimmedLine.substring(4),
                            lineNumber = index + 1
                        )
                    )
                }
            }
        }
        
        return outline
    }
    
    /**
     * 生成笔记ID
     */
    private fun generateNoteId(): String {
        return "note_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

/**
 * 章节详情数据类
 */
data class ChapterDetail(
    val chapter: Chapter,
    val course: Course,
    val progress: LearningProgress?,
    val assessments: List<Assessment>,
    val previousChapter: Chapter?,
    val nextChapter: Chapter?,
    val currentChapterIndex: Int,
    val totalChapters: Int,
    val estimatedReadingTime: Int, // 分钟
    val isCompleted: Boolean,
    val readingProgress: Float, // 0.0 - 1.0
    val timeSpent: Long, // 毫秒
    val lastAccessTime: Long?
)

/**
 * 章节笔记数据类
 */
data class ChapterNote(
    val id: String,
    val studentId: String,
    val chapterId: String,
    val content: String,
    val position: Int?, // 在章节中的位置
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * 学习统计数据类
 */
data class LearningStatistics(
    val totalChapters: Int,
    val completedChapters: Int,
    val totalTimeSpent: Long, // 毫秒
    val averageProgress: Float, // 0.0 - 1.0
    val lastAccessTime: Long?,
    val currentChapter: Chapter?,
    val estimatedTimeToComplete: Long // 毫秒
)

/**
 * 搜索结果数据类
 */
data class SearchResult(
    val lineNumber: Int,
    val snippet: String,
    val highlightStart: Int,
    val highlightEnd: Int
)

/**
 * 大纲项数据类
 */
data class OutlineItem(
    val level: Int, // 1, 2, 3 对应 #, ##, ###
    val title: String,
    val lineNumber: Int
)