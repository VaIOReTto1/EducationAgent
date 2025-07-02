package com.example.education.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 对话实体类
 * 
 * 存储用户与智能体的对话会话信息
 */
@Entity(
    tableName = "conversations",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["agentRole"]),
        Index(value = ["updatedAt"])
    ]
)
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    
    val userId: String,
    
    val agentRole: String, // knowledge_base, tutoring, assessment, student, teacher
    
    val title: String,
    
    val lastMessage: String? = null,
    
    val messageCount: Int = 0,
    
    val isActive: Boolean = true,
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis()
) 