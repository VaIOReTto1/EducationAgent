package com.example.education.core.database.dao

import androidx.room.*
import androidx.paging.PagingSource
import com.example.education.core.database.entities.LearningProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * 学习进度数据访问对象
 * 
 * 提供学习进度相关的数据库操作方法
 */
@Dao
interface LearningProgressDao {
    
    /**
     * 插入学习进度记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgressEntity)
    
    /**
     * 批量插入学习进度记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressList(progressList: List<LearningProgressEntity>)
    
    /**
     * 更新学习进度记录
     */
    @Update
    suspend fun updateProgress(progress: LearningProgressEntity)
    
    /**
     * 通过参数更新学习进度
     */
    @Query("""
        UPDATE learning_progress 
        SET progressPercent = :progressPercent, 
            timeSpent = :timeSpent, 
            isCompleted = :isCompleted, 
            lastAccessAt = :timestamp 
        WHERE userId = :userId AND chapterId = :chapterId
    """)
    suspend fun updateProgressByParams(
        userId: String,
        chapterId: String,
        progressPercent: Float,
        timeSpent: Long,
        isCompleted: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * 删除学习进度记录
     */
    @Delete
    suspend fun deleteProgress(progress: LearningProgressEntity)
    
    /**
     * 根据用户ID和章节ID删除学习进度
     */
    @Query("DELETE FROM learning_progress WHERE userId = :userId AND chapterId = :chapterId")
    suspend fun deleteProgressByIds(userId: String, chapterId: String)
    
    /**
     * 获取特定用户和章节的学习进度
     */
    @Query("SELECT * FROM learning_progress WHERE userId = :userId AND chapterId = :chapterId")
    suspend fun getProgress(userId: String, chapterId: String): LearningProgressEntity?
    
    /**
     * 获取用户的所有学习进度 - Flow版本
     */
    @Query("SELECT * FROM learning_progress WHERE userId = :userId ORDER BY lastAccessAt DESC")
    fun getProgressByUser(userId: String): Flow<List<LearningProgressEntity>>
    
    /**
     * 获取课程的学习进度
     */
    @Query("SELECT * FROM learning_progress WHERE courseId = :courseId ORDER BY lastAccessAt DESC")
    fun getProgressByCourse(courseId: String): Flow<List<LearningProgressEntity>>
    
    /**
     * 分页获取学习进度
     */
    @Query("SELECT * FROM learning_progress WHERE userId = :userId ORDER BY lastAccessAt DESC")
    fun getProgressPaged(userId: String): PagingSource<Int, LearningProgressEntity>
    
    /**
     * 获取课程的学生数量
     */
    @Query("SELECT COUNT(DISTINCT userId) FROM learning_progress WHERE courseId = :courseId")
    suspend fun getStudentCountByCourse(courseId: String): Int
    
    /**
     * 获取课程的平均学习进度
     */
    @Query("SELECT AVG(progressPercent) FROM learning_progress WHERE courseId = :courseId")
    suspend fun getAverageProgressByCourse(courseId: String): Float
    
    /**
     * 获取课程的完成率
     */
    @Query("""
        SELECT (COUNT(CASE WHEN isCompleted = 1 THEN 1 END) * 100.0 / COUNT(*)) 
        FROM learning_progress 
        WHERE courseId = :courseId
    """)
    suspend fun getCompletionRateByCourse(courseId: String): Float
    
    /**
     * 获取用户总学习时间
     */
    @Query("SELECT SUM(timeSpent) FROM learning_progress WHERE userId = :userId")
    suspend fun getTotalStudyTime(userId: String): Long
    
    /**
     * 获取用户完成的章节数量
     */
    @Query("SELECT COUNT(*) FROM learning_progress WHERE userId = :userId AND isCompleted = 1")
    suspend fun getCompletedChapterCount(userId: String): Int
    
    /**
     * 获取用户平均学习进度
     */
    @Query("SELECT AVG(progressPercent) FROM learning_progress WHERE userId = :userId")
    suspend fun getAverageProgress(userId: String): Float
    
    /**
     * 获取时间范围内的学习进度
     */
    @Query("""
        SELECT * FROM learning_progress 
        WHERE userId = :userId 
        AND lastAccessAt BETWEEN :startTime AND :endTime 
        ORDER BY lastAccessAt DESC
    """)
    fun getProgressInTimeRange(userId: String, startTime: Long, endTime: Long): Flow<List<LearningProgressEntity>>
    
    /**
     * 获取时间范围内的学习时间
     */
    @Query("""
        SELECT SUM(timeSpent) FROM learning_progress 
        WHERE userId = :userId 
        AND lastAccessAt BETWEEN :startTime AND :endTime
    """)
    suspend fun getStudyTimeInRange(userId: String, startTime: Long, endTime: Long): Long
    
    /**
     * 获取最近的学习进度
     */
    @Query("""
        SELECT * FROM learning_progress 
        WHERE userId = :userId 
        ORDER BY lastAccessAt DESC 
        LIMIT :limit
    """)
    fun getRecentProgress(userId: String, limit: Int = 10): Flow<List<LearningProgressEntity>>
    
    /**
     * 获取已完成章节的ID列表
     */
    @Query("SELECT chapterId FROM learning_progress WHERE userId = :userId AND isCompleted = 1")
    suspend fun getCompletedChapterIds(userId: String): List<String>
    
    /**
     * 搜索学习进度（根据课程标题）
     */
    @Query("""
        SELECT lp.* FROM learning_progress lp 
        INNER JOIN courses c ON lp.courseId = c.id 
        WHERE lp.userId = :userId 
        AND c.title LIKE '%' || :query || '%'
        ORDER BY lp.lastAccessAt DESC
    """)
    fun searchProgressByQuery(userId: String, query: String): Flow<List<LearningProgressEntity>>
} 