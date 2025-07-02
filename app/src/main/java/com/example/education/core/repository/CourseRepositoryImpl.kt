package com.example.education.core.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.education.core.database.dao.CourseDao
import com.example.education.core.database.dao.ChapterDao
import com.example.education.core.database.dao.LearningProgressDao
import com.example.education.core.database.entities.CourseEntity
import com.example.education.core.database.entities.ChapterEntity
import com.example.education.core.database.entities.LearningProgressEntity
import com.example.education.core.sync.FirebaseSyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 课程仓库实现类
 * 
 * 实现课程数据的本地缓存和远程同步逻辑
 */
@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao,
    private val chapterDao: ChapterDao,
    private val learningProgressDao: LearningProgressDao,
    private val firebaseSyncRepository: FirebaseSyncRepository
) : CourseRepository {
    
    companion object {
        private const val TAG = "CourseRepositoryImpl"
        private const val PAGE_SIZE = 20
    }
    
    // === 课程相关方法 ===
    
    override fun getAllCourses(): Flow<List<CourseEntity>> {
        Log.d(TAG, "获取所有课程列表")
        return courseDao.getAllCourses()
            .catch { e ->
                Log.e(TAG, "获取课程列表失败", e)
                emit(emptyList())
            }
    }
    
    override fun getCoursesByTeacher(teacherId: String): Flow<List<CourseEntity>> {
        Log.d(TAG, "获取教师课程: $teacherId")
        return courseDao.getCoursesByTeacher(teacherId)
            .catch { e ->
                Log.e(TAG, "获取教师课程失败", e)
                emit(emptyList())
            }
    }
    
    override suspend fun getCourseById(courseId: String): CourseEntity? {
        return try {
            Log.d(TAG, "获取课程详情: $courseId")
            courseDao.getCourseById(courseId)
        } catch (e: Exception) {
            Log.e(TAG, "获取课程详情失败: $courseId", e)
            null
        }
    }
    
    override fun getCoursesWithPaging(): Flow<PagingData<CourseEntity>> {
        Log.d(TAG, "获取分页课程列表")
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { courseDao.getCoursesPaged() }
        ).flow
    }
    
    override suspend fun insertCourse(course: CourseEntity) {
        try {
            Log.d(TAG, "插入课程: ${course.title}")
            courseDao.insertCourse(course)
            
            // 同步到Firebase
            firebaseSyncRepository.syncCourseToFirebase(course)
        } catch (e: Exception) {
            Log.e(TAG, "插入课程失败", e)
            throw e
        }
    }
    
    override suspend fun updateCourse(course: CourseEntity) {
        try {
            Log.d(TAG, "更新课程: ${course.title}")
            courseDao.updateCourse(course)
            
            // 同步到Firebase
            firebaseSyncRepository.syncCourseToFirebase(course)
        } catch (e: Exception) {
            Log.e(TAG, "更新课程失败", e)
            throw e
        }
    }
    
    override suspend fun deleteCourse(courseId: String) {
        try {
            Log.d(TAG, "删除课程: $courseId")
            courseDao.deleteCourseById(courseId)
            
            // 从Firebase删除
            firebaseSyncRepository.deleteCourseFromFirebase(courseId)
        } catch (e: Exception) {
            Log.e(TAG, "删除课程失败", e)
            throw e
        }
    }
    
    // === 章节相关方法 ===
    
    override fun getChaptersByCourse(courseId: String): Flow<List<ChapterEntity>> {
        Log.d(TAG, "获取课程章节: $courseId")
        return chapterDao.getChaptersByCourse(courseId)
            .catch { e ->
                Log.e(TAG, "获取课程章节失败", e)
                emit(emptyList())
            }
    }
    
    override suspend fun getChapterById(chapterId: String): ChapterEntity? {
        return try {
            Log.d(TAG, "获取章节详情: $chapterId")
            chapterDao.getChapterById(chapterId)
        } catch (e: Exception) {
            Log.e(TAG, "获取章节详情失败: $chapterId", e)
            null
        }
    }
    
    override suspend fun insertChapter(chapter: ChapterEntity) {
        try {
            Log.d(TAG, "插入章节: ${chapter.title}")
            chapterDao.insertChapter(chapter)
            
            // 同步到Firebase
            firebaseSyncRepository.syncChapterToFirebase(chapter)
        } catch (e: Exception) {
            Log.e(TAG, "插入章节失败", e)
            throw e
        }
    }
    
    override suspend fun updateChapter(chapter: ChapterEntity) {
        try {
            Log.d(TAG, "更新章节: ${chapter.title}")
            chapterDao.updateChapter(chapter)
            
            // 同步到Firebase
            firebaseSyncRepository.syncChapterToFirebase(chapter)
        } catch (e: Exception) {
            Log.e(TAG, "更新章节失败", e)
            throw e
        }
    }
    
    // === 学习进度相关方法 ===
    
    override suspend fun getLearningProgress(userId: String, chapterId: String): LearningProgressEntity? {
        return try {
            Log.d(TAG, "获取学习进度: 用户=$userId, 章节=$chapterId")
            learningProgressDao.getProgress(userId, chapterId)
        } catch (e: Exception) {
            Log.e(TAG, "获取学习进度失败", e)
            null
        }
    }
    
    override fun getLearningProgressByUser(userId: String): Flow<List<LearningProgressEntity>> {
        Log.d(TAG, "获取用户学习进度: $userId")
        return learningProgressDao.getProgressByUser(userId)
            .catch { e ->
                Log.e(TAG, "获取用户学习进度失败", e)
                emit(emptyList())
            }
    }
    
    override fun getLearningProgressByCourse(courseId: String): Flow<List<LearningProgressEntity>> {
        Log.d(TAG, "获取课程学习进度: $courseId")
        return learningProgressDao.getProgressByCourse(courseId)
            .catch { e ->
                Log.e(TAG, "获取课程学习进度失败", e)
                emit(emptyList())
            }
    }
    
    override suspend fun updateLearningProgress(progress: LearningProgressEntity) {
        try {
            Log.d(TAG, "更新学习进度: 用户=${progress.userId}, 章节=${progress.chapterId}")
            learningProgressDao.updateProgress(progress)
            
            // 同步到Firebase
            firebaseSyncRepository.syncLearningProgressToFirebase(progress)
        } catch (e: Exception) {
            Log.e(TAG, "更新学习进度失败", e)
            throw e
        }
    }
    
    override suspend fun insertLearningProgress(progress: LearningProgressEntity) {
        try {
            Log.d(TAG, "插入学习进度: 用户=${progress.userId}, 章节=${progress.chapterId}")
            learningProgressDao.insertProgress(progress)
            
            // 同步到Firebase
            firebaseSyncRepository.syncLearningProgressToFirebase(progress)
        } catch (e: Exception) {
            Log.e(TAG, "插入学习进度失败", e)
            throw e
        }
    }
    
    override suspend fun deleteLearningProgress(userId: String, chapterId: String) {
        try {
            Log.d(TAG, "删除学习进度: 用户=$userId, 章节=$chapterId")
            learningProgressDao.deleteProgressByIds(userId, chapterId)
            
            // 从Firebase删除
            firebaseSyncRepository.deleteLearningProgressFromFirebase(userId, chapterId)
        } catch (e: Exception) {
            Log.e(TAG, "删除学习进度失败", e)
            throw e
        }
    }
    
    // === 统计相关方法 ===
    
    override suspend fun getTotalStudyTime(userId: String): Long {
        return try {
            Log.d(TAG, "获取总学习时间: $userId")
            learningProgressDao.getTotalStudyTime(userId) ?: 0L
        } catch (e: Exception) {
            Log.e(TAG, "获取总学习时间失败", e)
            0L
        }
    }
    
    override suspend fun getCompletedChapterCount(userId: String): Int {
        return try {
            Log.d(TAG, "获取完成章节数: $userId")
            learningProgressDao.getCompletedChapterCount(userId)
        } catch (e: Exception) {
            Log.e(TAG, "获取完成章节数失败", e)
            0
        }
    }
    
    override suspend fun getAverageProgress(userId: String): Float {
        return try {
            Log.d(TAG, "获取平均进度: $userId")
            learningProgressDao.getAverageProgress(userId) ?: 0f
        } catch (e: Exception) {
            Log.e(TAG, "获取平均进度失败", e)
            0f
        }
    }
    
    override suspend fun getStudentCountByCourse(courseId: String): Int {
        return try {
            Log.d(TAG, "获取课程学生数: $courseId")
            learningProgressDao.getStudentCountByCourse(courseId)
        } catch (e: Exception) {
            Log.e(TAG, "获取课程学生数失败", e)
            0
        }
    }
    
    override suspend fun getAverageProgressByCourse(courseId: String): Float {
        return try {
            Log.d(TAG, "获取课程平均进度: $courseId")
            learningProgressDao.getAverageProgressByCourse(courseId) ?: 0f
        } catch (e: Exception) {
            Log.e(TAG, "获取课程平均进度失败", e)
            0f
        }
    }
    
    override suspend fun getCompletionRateByCourse(courseId: String): Float {
        return try {
            Log.d(TAG, "获取课程完成率: $courseId")
            learningProgressDao.getCompletionRateByCourse(courseId) ?: 0f
        } catch (e: Exception) {
            Log.e(TAG, "获取课程完成率失败", e)
            0f
        }
    }
    
    // === 搜索相关方法 ===
    
    override fun searchCourses(query: String): Flow<List<CourseEntity>> {
        Log.d(TAG, "搜索课程: $query")
        return courseDao.searchCoursesByTitle(query)
            .catch { e ->
                Log.e(TAG, "搜索课程失败", e)
                emit(emptyList())
            }
    }
    
    override fun searchLearningProgress(userId: String, query: String): Flow<List<LearningProgressEntity>> {
        Log.d(TAG, "搜索学习进度: 用户=$userId, 查询=$query")
        return learningProgressDao.searchProgressByQuery(userId, query)
            .catch { e ->
                Log.e(TAG, "搜索学习进度失败", e)
                emit(emptyList())
            }
    }
    
    // === 时间范围查询 ===
    
    override fun getLearningProgressInTimeRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): Flow<List<LearningProgressEntity>> {
        Log.d(TAG, "获取时间范围学习进度: 用户=$userId, 开始=$startTime, 结束=$endTime")
        return learningProgressDao.getProgressInTimeRange(userId, startTime, endTime)
            .catch { e ->
                Log.e(TAG, "获取时间范围学习进度失败", e)
                emit(emptyList())
            }
    }
    
    override suspend fun getStudyTimeInRange(
        userId: String,
        startTime: Long,
        endTime: Long
    ): Long {
        return try {
            Log.d(TAG, "获取时间范围学习时长: 用户=$userId")
            learningProgressDao.getStudyTimeInRange(userId, startTime, endTime) ?: 0L
        } catch (e: Exception) {
            Log.e(TAG, "获取时间范围学习时长失败", e)
            0L
        }
    }
    
    override fun getRecentLearningProgress(userId: String, limit: Int): Flow<List<LearningProgressEntity>> {
        Log.d(TAG, "获取最近学习进度: 用户=$userId, 限制=$limit")
        return learningProgressDao.getRecentProgress(userId, limit)
            .catch { e ->
                Log.e(TAG, "获取最近学习进度失败", e)
                emit(emptyList())
            }
    }
    
    // === 数据同步 ===
    
    override suspend fun syncCoursesFromFirebase(): Flow<Result<List<CourseEntity>>> = flow {
        try {
            Log.d(TAG, "从Firebase同步课程数据")
            emit(Result.success(emptyList())) // 临时实现
        } catch (e: Exception) {
            Log.e(TAG, "同步课程数据失败", e)
            emit(Result.failure(e))
        }
    }
    
    override suspend fun syncLearningProgressFromFirebase(userId: String): Flow<Result<List<LearningProgressEntity>>> = flow {
        try {
            Log.d(TAG, "从Firebase同步学习进度: $userId")
            emit(Result.success(emptyList())) // 临时实现
        } catch (e: Exception) {
            Log.e(TAG, "同步学习进度失败", e)
            emit(Result.failure(e))
        }
    }
} 