package com.example.education.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * 学习进度实体类
 * 
 * 记录学生的学习进度
 */
@Entity(
    tableName = "learning_progress",
    primaryKeys = ["userId", "chapterId"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["chapterId"]),
        Index(value = ["courseId"]),
        Index(value = ["completedAt"])
    ]
)
data class LearningProgressEntity(
    val userId: String,
    val courseId: String,
    val chapterId: String,
    val progressPercent: Float = 0f, // 完成百分比 0-100
    val timeSpent: Long = 0, // 学习时长（秒）
    val isCompleted: Boolean = false,
    val lastAccessAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) 