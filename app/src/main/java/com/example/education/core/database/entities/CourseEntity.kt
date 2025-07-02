package com.example.education.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 课程实体类
 * 
 * 存储课程基本信息和元数据
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
    indices = [
        Index(value = ["teacher_id"]),
        Index(value = ["subject"]),
        Index(value = ["created_at"])
    ]
)
data class CourseEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "teacher_id")
    val teacherId: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "subject")
    val subject: String, // 学科分类
    
    @ColumnInfo(name = "grade_level")
    val gradeLevel: String, // 年级水平
    
    @ColumnInfo(name = "cover_image_url")
    val coverImageUrl: String? = null,
    
    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: Int = 1, // 1-5难度等级
    
    @ColumnInfo(name = "estimated_hours")
    val estimatedHours: Int = 0, // 预计学习小时数
    
    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) 