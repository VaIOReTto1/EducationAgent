package com.example.education.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.education.core.database.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE is_synced = 0")
    suspend fun getUnsyncedUsers(): List<UserEntity>

    @Query("UPDATE users SET is_synced = 1 WHERE id = :id")
    suspend fun markUserAsSynced(id: String)
}

/**
 * 课程数据访问对象
 */
@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: String): CourseEntity?

    @Query("SELECT * FROM courses WHERE teacher_id = :teacherId ORDER BY created_at DESC")
    fun getCoursesByTeacher(teacherId: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE is_published = 1 ORDER BY created_at DESC")
    fun getPublishedCourses(): PagingSource<Int, CourseEntity>

    @Query("SELECT * FROM courses WHERE subject = :subject AND is_published = 1")
    fun getCoursesBySubject(subject: String): Flow<List<CourseEntity>>

    @Query("""
        SELECT * FROM courses 
        WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') 
        AND is_published = 1
        ORDER BY created_at DESC
    """)
    fun searchCourses(query: String): PagingSource<Int, CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("SELECT * FROM courses WHERE is_synced = 0")
    suspend fun getUnsyncedCourses(): List<CourseEntity>

    @Query("UPDATE courses SET is_synced = 1 WHERE id = :id")
    suspend fun markCourseAsSynced(id: String)
}

/**
 * 章节数据访问对象
 */
@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: String): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE course_id = :courseId ORDER BY order_index ASC")
    fun getChaptersByCourse(courseId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE course_id = :courseId AND is_published = 1 ORDER BY order_index ASC")
    fun getPublishedChaptersByCourse(courseId: String): Flow<List<ChapterEntity>>

    @Query("SELECT COUNT(*) FROM chapters WHERE course_id = :courseId AND is_published = 1")
    suspend fun getPublishedChapterCount(courseId: String): Int

    @Query("""
        SELECT * FROM chapters 
        WHERE course_id = :courseId 
        AND order_index > :currentIndex 
        AND is_published = 1 
        ORDER BY order_index ASC 
        LIMIT :limit
    """)
    suspend fun getNextChapters(courseId: String, currentIndex: Int, limit: Int = 3): List<ChapterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)

    @Query("SELECT * FROM chapters WHERE is_synced = 0")
    suspend fun getUnsyncedChapters(): List<ChapterEntity>

    @Query("UPDATE chapters SET is_synced = 1 WHERE id = :id")
    suspend fun markChapterAsSynced(id: String)
}

/**
 * 对话数据访问对象
 */
@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE user_id = :userId ORDER BY updated_at DESC")
    fun getConversationsByUser(userId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE user_id = :userId AND agent_type = :agentType ORDER BY updated_at DESC")
    fun getConversationsByUserAndAgent(userId: String, agentType: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE user_id = :userId AND is_active = 1 ORDER BY updated_at DESC LIMIT 1")
    suspend fun getActiveConversation(userId: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET is_active = 0 WHERE user_id = :userId")
    suspend fun deactivateAllConversations(userId: String)

    @Query("SELECT * FROM conversations WHERE is_synced = 0")
    suspend fun getUnsyncedConversations(): List<ConversationEntity>

    @Query("UPDATE conversations SET is_synced = 1 WHERE id = :id")
    suspend fun markConversationAsSynced(id: String)
}

/**
 * 消息数据访问对象
 */
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    fun getMessagesByConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    fun getMessagesByConversationPaged(conversationId: String): PagingSource<Int, MessageEntity>

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestMessage(conversationId: String): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE conversation_id = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)

    @Query("SELECT * FROM messages WHERE is_synced = 0")
    suspend fun getUnsyncedMessages(): List<MessageEntity>

    @Query("UPDATE messages SET is_synced = 1 WHERE id = :id")
    suspend fun markMessageAsSynced(id: String)
}

/**
 * 评估数据访问对象
 */
@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments WHERE id = :id")
    suspend fun getAssessmentById(id: String): AssessmentEntity?

    @Query("SELECT * FROM assessments WHERE course_id = :courseId ORDER BY created_at DESC")
    fun getAssessmentsByCourse(courseId: String): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE chapter_id = :chapterId ORDER BY created_at DESC")
    fun getAssessmentsByChapter(chapterId: String): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE course_id = :courseId AND is_published = 1")
    fun getPublishedAssessmentsByCourse(courseId: String): Flow<List<AssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity)

    @Update
    suspend fun updateAssessment(assessment: AssessmentEntity)

    @Delete
    suspend fun deleteAssessment(assessment: AssessmentEntity)

    @Query("SELECT * FROM assessments WHERE is_synced = 0")
    suspend fun getUnsyncedAssessments(): List<AssessmentEntity>

    @Query("UPDATE assessments SET is_synced = 1 WHERE id = :id")
    suspend fun markAssessmentAsSynced(id: String)
}

/**
 * 学习进度数据访问对象
 */
@Dao
interface LearningProgressDao {
    @Query("SELECT * FROM learning_progress WHERE user_id = :userId AND course_id = :courseId")
    fun getProgressByCourse(userId: String, courseId: String): Flow<List<LearningProgressEntity>>

    @Query("SELECT * FROM learning_progress WHERE user_id = :userId AND course_id = :courseId AND chapter_id = :chapterId")
    suspend fun getProgressByChapter(userId: String, courseId: String, chapterId: String): LearningProgressEntity?

    @Query("""
        SELECT AVG(progress_percentage) FROM learning_progress 
        WHERE user_id = :userId AND course_id = :courseId
    """)
    suspend fun getCourseProgressPercentage(userId: String, courseId: String): Float?

    @Query("""
        SELECT COUNT(*) FROM learning_progress 
        WHERE user_id = :userId AND course_id = :courseId AND is_completed = 1
    """)
    suspend fun getCompletedChapterCount(userId: String, courseId: String): Int

    @Query("""
        SELECT SUM(time_spent_minutes) FROM learning_progress 
        WHERE user_id = :userId AND course_id = :courseId
    """)
    suspend fun getTotalTimeSpent(userId: String, courseId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgressEntity)

    @Update
    suspend fun updateProgress(progress: LearningProgressEntity)

    @Delete
    suspend fun deleteProgress(progress: LearningProgressEntity)

    @Query("SELECT * FROM learning_progress WHERE is_synced = 0")
    suspend fun getUnsyncedProgress(): List<LearningProgressEntity>

    @Query("UPDATE learning_progress SET is_synced = 1 WHERE id = :id")
    suspend fun markProgressAsSynced(id: String)
}

/**
 * 评估结果数据访问对象
 */
@Dao
interface AssessmentResultDao {
    @Query("SELECT * FROM assessment_results WHERE user_id = :userId AND assessment_id = :assessmentId")
    suspend fun getResultByUserAndAssessment(userId: String, assessmentId: String): AssessmentResultEntity?

    @Query("SELECT * FROM assessment_results WHERE user_id = :userId ORDER BY submitted_at DESC")
    fun getResultsByUser(userId: String): Flow<List<AssessmentResultEntity>>

    @Query("SELECT * FROM assessment_results WHERE assessment_id = :assessmentId ORDER BY submitted_at DESC")
    fun getResultsByAssessment(assessmentId: String): Flow<List<AssessmentResultEntity>>

    @Query("""
        SELECT AVG(CAST(score_points AS FLOAT) / total_points * 100) 
        FROM assessment_results 
        WHERE user_id = :userId
    """)
    suspend fun getAverageScorePercentage(userId: String): Float?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: AssessmentResultEntity)

    @Update
    suspend fun updateResult(result: AssessmentResultEntity)

    @Delete
    suspend fun deleteResult(result: AssessmentResultEntity)

    @Query("SELECT * FROM assessment_results WHERE is_synced = 0")
    suspend fun getUnsyncedResults(): List<AssessmentResultEntity>

    @Query("UPDATE assessment_results SET is_synced = 1 WHERE id = :id")
    suspend fun markResultAsSynced(id: String)
}