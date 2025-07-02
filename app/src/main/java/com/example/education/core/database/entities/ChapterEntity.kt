package com.example.education.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 章节实体类
 * 
 * 存储课程章节信息，支持教学内容组织
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
    indices = [
        Index(value = ["course_id"]),
        Index(value = ["chapter_order"]),
        Index(value = ["is_published"])
    ]
)
data class ChapterEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "course_id")
    val courseId: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "chapter_order")
    val chapterOrder: Int,
    
    @ColumnInfo(name = "content")
    val content: String? = null, // 章节内容，可以是Markdown格式
    
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int? = null, // 预计学习时长（分钟）
    
    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = false,
    
    @ColumnInfo(name = "is_free")
    val isFree: Boolean = true,
    
    @ColumnInfo(name = "prerequisites")
    val prerequisites: String? = null, // JSON格式的前置章节ID列表
    
    @ColumnInfo(name = "learning_objectives")
    val learningObjectives: String? = null, // JSON格式的学习目标列表
    
    @ColumnInfo(name = "resources")
    val resources: String? = null, // JSON格式的资源链接
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) 