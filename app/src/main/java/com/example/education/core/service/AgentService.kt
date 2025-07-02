package com.example.education.core.service

import com.example.education.agent.StudentContext
import com.example.education.core.network.models.ChatResponse
import kotlinx.coroutines.flow.Flow

/**
 * 智能体服务接口
 * 
 * 定义五个智能体的统一服务方法
 */
interface AgentService {
    
    /**
     * 与课程规划智能体对话
     */
    fun chatWithCurriculumAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse>
    
    /**
     * 与个性化辅导智能体对话
     */
    fun chatWithTutoringAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse>
    
    /**
     * 与评估反馈智能体对话
     */
    fun chatWithAssessmentAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse>
    
    /**
     * 与知识检索智能体对话
     */
    fun chatWithKnowledgeAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse>
    
    /**
     * 与对话管理智能体对话
     */
    fun chatWithDialogueAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse>
    
    /**
     * 智能路由：根据消息内容自动选择合适的智能体
     */
    fun smartRouteMessage(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse>
} 