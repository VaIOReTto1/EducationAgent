package com.example.education.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.education.core.database.entities.ConversationEntity
import com.example.education.core.database.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * 会话数据访问对象
 */
@Dao
interface ConversationDao {
    
    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getConversationsByUser(userId: String): Flow<List<ConversationEntity>>
    
    @Query("SELECT * FROM conversations WHERE userId = :userId AND agentRole = :agentRole")
    suspend fun getConversationByUserAndAgent(userId: String, agentRole: String): ConversationEntity?
    
    @Query("SELECT * FROM conversations WHERE userId = :userId AND agentRole = :agentRole")
    fun getConversationByUserAndAgentFlow(userId: String, agentRole: String): Flow<ConversationEntity?>
    
    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationEntity?
    
    @Query("SELECT * FROM conversations WHERE isActive = 1 ORDER BY updatedAt DESC")
    fun getActiveConversations(): Flow<List<ConversationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)
    
    @Update
    suspend fun updateConversation(conversation: ConversationEntity)
    
    @Query("UPDATE conversations SET lastMessage = :lastMessage, messageCount = messageCount + 1, updatedAt = :updatedAt WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId: String, lastMessage: String, updatedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE conversations SET title = :title, updatedAt = :updatedAt WHERE id = :conversationId")
    suspend fun updateConversationTitle(conversationId: String, title: String, updatedAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)
    
    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversationById(conversationId: String)
    
    @Query("SELECT COUNT(*) FROM conversations WHERE userId = :userId")
    suspend fun getConversationCountByUser(userId: String): Int
}
