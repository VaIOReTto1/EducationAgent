package com.example.education.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 作业提交实体类
 * 
 * 存储学生提交的作业内容和评分信息
 */
@Entity(
    tableName = "submissions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["student_id"]),
        Index(value = ["assignment_id"]),
        Index(value = ["student_id", "assignment_id"], unique = true),
        Index(value = ["submitted_at"]),
        Index(value = ["status"])
    ]
)
data class SubmissionEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "student_id")
    val studentId: String,
    
    @ColumnInfo(name = "assignment_id")
    val assignmentId: String,
    
    @ColumnInfo(name = "content")
    val content: String, // 作业内容
    
    @ColumnInfo(name = "attachments")
    val attachments: String? = null, // JSON格式的附件信息
    
    @ColumnInfo(name = "status")
    val status: String = "submitted", // submitted, graded, returned
    
    @ColumnInfo(name = "score")
    val score: Int? = null,
    
    @ColumnInfo(name = "feedback")
    val feedback: String? = null, // 教师反馈
    
    @ColumnInfo(name = "graded_by")
    val gradedBy: String? = null, // 批改教师ID
    
    @ColumnInfo(name = "submitted_at")
    val submittedAt: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "graded_at")
    val gradedAt: LocalDateTime? = null,
    
    @ColumnInfo(name = "is_late")
    val isLate: Boolean = false,
    
    @ColumnInfo(name = "draft_content")
    val draftContent: String? = null, // 草稿内容
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) 