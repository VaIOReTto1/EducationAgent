package com.example.education.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo

/**
 * 用户实体
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val role: String, // "teacher" 或 "student"
    val avatar: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

/**
 * 课程实体
 */
@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["teacher_id"])]
)
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    @ColumnInfo(name = "teacher_id")
    val teacherId: String,
    val subject: String,
    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String, // "beginner", "intermediate", "advanced"
    @ColumnInfo(name = "estimated_hours")
    val estimatedHours: Int,
    @ColumnInfo(name = "cover_image")
    val coverImage: String? = null,
    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

/**
 * 章节实体
 */
@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["course_id"])]
)
data class ChapterEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    val title: String,
    val content: String,
    @ColumnInfo(name = "order_index")
    val orderIndex: Int,
    @ColumnInfo(name = "estimated_minutes")
    val estimatedMinutes: Int,
    @ColumnInfo(name = "video_url")
    val videoUrl: String? = null,
    @ColumnInfo(name = "audio_url")
    val audioUrl: String? = null,
    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

/**
 * 对话实体
 */
@Entity(
    tableName = "conversations",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    val title: String,
    @ColumnInfo(name = "agent_type")
    val agentType: String, // "tutoring", "assessment", "kb", "student", "teacher"
    @ColumnInfo(name = "context_data")
    val contextData: String? = null, // JSON格式的上下文数据
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

/**
 * 消息实体
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["conversation_id"])]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "conversation_id")
    val conversationId: String,
    val content: String,
    @ColumnInfo(name = "sender_type")
    val senderType: String, // "user" 或 "agent"
    @ColumnInfo(name = "message_type")
    val messageType: String, // "text", "image", "file", "audio"
    @ColumnInfo(name = "metadata")
    val metadata: String? = null, // JSON格式的元数据
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

/**
 * 评估实体
 */
@Entity(
    tableName = "assessments",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["course_id"]), Index(value = ["chapter_id"])]
)
data class AssessmentEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "chapter_id")
    val chapterId: String? = null,
    val title: String,
    val description: String,
    @ColumnInfo(name = "question_data")
    val questionData: String, // JSON格式的题目数据
    @ColumnInfo(name = "total_points")
    val totalPoints: Int,
    @ColumnInfo(name = "time_limit_minutes")
    val timeLimitMinutes: Int? = null,
    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

/**
 * 学习进度实体
 */
@Entity(
    tableName = "learning_progress",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["course_id"]),
        Index(value = ["chapter_id"]),
        Index(value = ["user_id", "course_id", "chapter_id"], unique = true)
    ]
)
data class LearningProgressEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "chapter_id")
    val chapterId: String,
    @ColumnInfo(name = "progress_percentage")
    val progressPercentage: Float, // 0.0 - 1.0
    @ColumnInfo(name = "time_spent_minutes")
    val timeSpentMinutes: Int,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "last_accessed_at")
    val lastAccessedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)

/**
 * 评估结果实体
 */
@Entity(
    tableName = "assessment_results",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AssessmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["assessment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["assessment_id"]),
        Index(value = ["user_id", "assessment_id"])
    ]
)
data class AssessmentResultEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "assessment_id")
    val assessmentId: String,
    @ColumnInfo(name = "answer_data")
    val answerData: String, // JSON格式的答案数据
    @ColumnInfo(name = "score_points")
    val scorePoints: Int,
    @ColumnInfo(name = "total_points")
    val totalPoints: Int,
    @ColumnInfo(name = "time_spent_minutes")
    val timeSpentMinutes: Int,
    @ColumnInfo(name = "feedback")
    val feedback: String? = null,
    @ColumnInfo(name = "submitted_at")
    val submittedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "graded_at")
    val gradedAt: Long? = null,
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)