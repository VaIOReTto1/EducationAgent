package com.example.education.core.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 消息实体类
 * 
 * 存储对话中的具体消息内容
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["conversationId"]),
        Index(value = ["createdAt"]),
        Index(value = ["isFromUser"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    
    val conversationId: String,
    
    val content: String,
    
    val isFromUser: Boolean, // true: 用户消息, false: AI回复
    
    val messageType: String = "text", // text, image, file
    
    val metadata: String? = null, // JSON格式的额外信息
    
    val tokenUsage: String? = null, // JSON格式的token使用统计
    
    val isSynced: Boolean = false, // 是否已同步到云端
    
    val createdAt: Long = System.currentTimeMillis()
) 