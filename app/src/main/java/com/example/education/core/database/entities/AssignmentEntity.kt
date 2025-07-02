package com.example.education.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 作业实体类
 * 
 * 存储教师创建的作业信息
 */
@Entity(
    tableName = "assignments",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["teacher_id"]),
        Index(value = ["course_id"]),
        Index(value = ["chapter_id"]),
        Index(value = ["due_date"]),
        Index(value = ["created_at"])
    ]
)
data class AssignmentEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "teacher_id")
    val teacherId: String,
    
    @ColumnInfo(name = "course_id")
    val courseId: String,
    
    @ColumnInfo(name = "chapter_id")
    val chapterId: String? = null,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "assignment_type")
    val assignmentType: String, // essay, quiz, project, reading
    
    @ColumnInfo(name = "max_score")
    val maxScore: Int = 100,
    
    @ColumnInfo(name = "due_date")
    val dueDate: LocalDateTime,
    
    @ColumnInfo(name = "instructions")
    val instructions: String? = null,
    
    @ColumnInfo(name = "resources")
    val resources: String? = null, // JSON格式的资源链接
    
    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = false,
    
    @ColumnInfo(name = "allow_late_submission")
    val allowLateSubmission: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) 