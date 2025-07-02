package com.example.education.core.repository

import androidx.paging.PagingData
import com.example.education.core.database.entities.CourseEntity
import com.example.education.core.database.entities.ChapterEntity
import com.example.education.core.database.entities.LearningProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * 课程数据仓库接口
 * 
 * 定义课程、章节和学习进度相关的数据操作方法
 */
interface CourseRepository {
    
    // === 课程相关方法 ===
    
    /**
     * 获取所有课程列表
     */
    fun getAllCourses(): Flow<List<CourseEntity>>
    
    /**
     * 根据教师ID获取课程列表
     */
    fun getCoursesByTeacher(teacherId: String): Flow<List<CourseEntity>>
    
    /**
     * 根据课程ID获取课程详情
     */
    suspend fun getCourseById(courseId: String): CourseEntity?
    
    /**
     * 获取分页课程数据
     */
    fun getCoursesWithPaging(): Flow<PagingData<CourseEntity>>
    
    /**
     * 插入新课程
     */
    suspend fun insertCourse(course: CourseEntity)
    
    /**
     * 更新课程信息
     */
    suspend fun updateCourse(course: CourseEntity)
    
    /**
     * 删除课程
     */
    suspend fun deleteCourse(courseId: String)
    
    // === 章节相关方法 ===
    
    /**
     * 根据课程ID获取章节列表
     */
    fun getChaptersByCourse(courseId: String): Flow<List<ChapterEntity>>
    
    /**
     * 根据章节ID获取章节详情
     */
    suspend fun getChapterById(chapterId: String): ChapterEntity?
    
    /**
     * 插入新章节
     */
    suspend fun insertChapter(chapter: ChapterEntity)
    
    /**
     * 更新章节信息
     */
    suspend fun updateChapter(chapter: ChapterEntity)
    
    // === 学习进度相关方法 ===
    
    /**
     * 获取特定用户和章节的学习进度
     */
    suspend fun getLearningProgress(userId: String, chapterId: String): LearningProgressEntity?
    
    /**
     * 获取用户的所有学习进度
     */
    fun getLearningProgressByUser(userId: String): Flow<List<LearningProgressEntity>>
    
    /**
     * 获取课程的学习进度统计
     */
    fun getLearningProgressByCourse(courseId: String): Flow<List<LearningProgressEntity>>
    
    /**
     * 更新学习进度
     */
    suspend fun updateLearningProgress(progress: LearningProgressEntity)
    
    /**
     * 插入新的学习进度记录
     */
    suspend fun insertLearningProgress(progress: LearningProgressEntity)
    
    /**
     * 删除学习进度记录
     */
    suspend fun deleteLearningProgress(userId: String, chapterId: String)
    
    // === 统计相关方法 ===
    
    /**
     * 获取用户总学习时间
     */
    suspend fun getTotalStudyTime(userId: String): Long
    
    /**
     * 获取用户完成的章节数量
     */
    suspend fun getCompletedChapterCount(userId: String): Int
    
    /**
     * 获取用户平均学习进度
     */
    suspend fun getAverageProgress(userId: String): Float
    
    /**
     * 获取课程的学生数量
     */
    suspend fun getStudentCountByCourse(courseId: String): Int
    
    /**
     * 获取课程的平均学习进度
     */
    suspend fun getAverageProgressByCourse(courseId: String): Float
    
    /**
     * 获取课程的完成率
     */
    suspend fun getCompletionRateByCourse(courseId: String): Float
    
    // === 搜索相关方法 ===
    
    /**
     * 搜索课程
     */
    fun searchCourses(query: String): Flow<List<CourseEntity>>
    
    /**
     * 搜索学习进度
     */
    fun searchLearningProgress(userId: String, query: String): Flow<List<LearningProgressEntity>>
    
    // === 时间范围查询 ===
    
    /**
     * 获取时间范围内的学习进度
     */
    fun getLearningProgressInTimeRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): Flow<List<LearningProgressEntity>>
    
    /**
     * 获取时间范围内的学习时间
     */
    suspend fun getStudyTimeInRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): Long
    
    /**
     * 获取最近的学习进度
     */
    fun getRecentLearningProgress(userId: String, limit: Int = 10): Flow<List<LearningProgressEntity>>
    
    // === 数据同步 ===
    
    /**
     * 从Firebase同步课程数据
     */
    suspend fun syncCoursesFromFirebase(): Flow<Result<List<CourseEntity>>>
    
    /**
     * 从Firebase同步学习进度数据
     */
    suspend fun syncLearningProgressFromFirebase(userId: String): Flow<Result<List<LearningProgressEntity>>>
} 