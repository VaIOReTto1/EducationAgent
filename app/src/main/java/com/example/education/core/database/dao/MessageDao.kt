package com.example.education.core.database.dao

import androidx.room.*
import com.example.education.core.database.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * 消息数据访问对象
 */
@Dao
interface MessageDao {
    
    /**
     * 获取未同步的消息
     */
    @Query("SELECT * FROM messages WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getUnsyncedMessages(): List<MessageEntity>
    
    /**
     * 更新消息同步状态
     */
    @Query("UPDATE messages SET isSynced = :isSynced WHERE id = :messageId")
    suspend fun updateSyncStatus(messageId: String, isSynced: Boolean)
    
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun getMessagesByConversation(conversationId: String): Flow<List<MessageEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
    
    @Delete
    suspend fun deleteMessage(message: MessageEntity)
    
    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)
} 