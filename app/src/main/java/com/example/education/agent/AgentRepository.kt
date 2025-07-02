package com.example.education.agent

import com.example.education.core.network.models.ChatRequest
import com.example.education.core.network.models.ChatResponse
import com.example.education.core.network.models.ChatStreamEvent
import kotlinx.coroutines.flow.Flow

/**
 * 智能体仓库接口
 * 
 * 定义与五个AI智能体的交互方法
 */
interface AgentRepository {
    
    /**
     * 知识库管理智能体查询
     */
    suspend fun queryKnowledgeBase(
        userId: String,
        query: String,
        conversationId: String?
    ): Flow<ChatStreamEvent>
    
    /**
     * 个性化辅导智能体
     */
    suspend fun startTutoring(
        userId: String,
        question: String,
        conversationId: String?,
        studentLevel: String?
    ): Flow<ChatStreamEvent>
    
    /**
     * 评估反馈智能体
     */
    suspend fun generateAssessment(
        userId: String,
        topic: String,
        difficulty: String,
        questionTypes: List<String>,
        conversationId: String?
    ): Flow<ChatStreamEvent>
    
    /**
     * 学生端智能体对话
     */
    suspend fun studentChat(
        userId: String,
        message: String,
        conversationId: String?,
        context: StudentContext?
    ): Flow<ChatStreamEvent>
    
    /**
     * 教师端智能体对话
     */
    suspend fun teacherChat(
        userId: String,
        message: String,
        conversationId: String?,
        context: TeacherContext?
    ): Flow<ChatStreamEvent>
    
    /**
     * 获取对话历史
     */
    suspend fun getConversationHistory(
        userId: String,
        conversationId: String
    ): Flow<List<ChatResponse>>
    
    /**
     * 删除对话历史
     */
    suspend fun deleteConversation(
        conversationId: String,
        userId: String
    ): Flow<ChatResponse>
} 