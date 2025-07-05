package com.example.education.agent

import android.util.Log
import com.example.education.core.network.DifyApiService
import com.example.education.core.network.EnhancedDifyApiService
import com.example.education.core.network.models.*
import com.example.education.core.network.ApiConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 智能体仓库实现
 * 
 * 基于Dify API实现五个智能体的交互逻辑，支持多API Key动态切换
 */
@Singleton
class AgentRepositoryImpl @Inject constructor(
    private val enhancedApiService: EnhancedDifyApiService,
    private val json: Json
) : AgentRepository {
    
    companion object {
        private const val TAG = "AgentRepository"
        
        // 智能体角色映射
        private const val ROLE_KNOWLEDGE_BASE = "knowledge_base"
        private const val ROLE_TUTORING = "tutoring" 
        private const val ROLE_ASSESSMENT = "assessment"
        private const val ROLE_STUDENT = "student"
        private const val ROLE_TEACHER = "teacher"
    }
    
    /**
     * 知识库管理智能体
     * 使用专门的知识库管理API Key: app-4EKbCtVu8kl7ma0BS1mRuv3R
     */
    override suspend fun queryKnowledgeBase(
        userId: String,
        query: String,
        conversationId: String?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "知识库管理智能体查询: $query")
        
        val request = ChatRequest(
            inputs = mapOf(),
            query = query,
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = enhancedApiService.knowledgeBaseChat(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "知识库查询失败", e)
            emit(ChatStreamEvent.Error("error", "知识库查询失败: ${e.message}"))
        }
    }
    
    /**
     * 辅导智能体
     * 使用专门的辅导端API Key: app-UOktKFCXqIg1Em9Llu8mvfvD
     */
    override suspend fun startTutoring(
        userId: String,
        question: String,
        conversationId: String?,
        studentLevel: String?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "辅导智能体启动: $question")
        
        val request = ChatRequest(
            inputs = mapOf(),
            query = question,
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = enhancedApiService.tutoringChat(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "辅导服务失败", e)
            emit(ChatStreamEvent.Error("error", "辅导服务失败: ${e.message}"))
        }
    }
    
    /**
     * 评估智能体
     * 使用专门的评估端API Key: app-45d3YaGnQh0MLZcxGanotNLa
     */
    override suspend fun generateAssessment(
        userId: String,
        topic: String,
        difficulty: String,
        questionTypes: List<String>,
        conversationId: String?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "评估智能体生成题目: $topic, 难度: $difficulty")
        
        val request = ChatRequest(
            inputs = mapOf(),
            query = difficulty,
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = enhancedApiService.assessmentChat(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "评估生成失败", e)
            emit(ChatStreamEvent.Error("error", "评估生成失败: ${e.message}"))
        }
    }
    
    /**
     * 学生端智能体
     * 使用专门的学生端API Key: app-mTevUPVC20OFXKn4HRvea1GV
     */
    override suspend fun studentChat(
        userId: String,
        message: String,
        conversationId: String?,
        context: StudentContext?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "学生端智能体对话: $message")
        
        val inputs = mutableMapOf<String, Any>()
        context?.let {
            inputs["current_course"] = it.currentCourse ?: ""
            inputs["current_chapter"] = it.currentChapter ?: ""
            inputs["learning_progress"] = it.learningProgress.toString()
            inputs["difficulty_level"] = it.difficultyLevel
            inputs["preferred_style"] = it.preferredStyle
        }
        
        val request = ChatRequest(
            inputs = mutableMapOf<String, Any>(),
            query = message,
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = enhancedApiService.studentChat(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "学生对话失败", e)
            emit(ChatStreamEvent.Error("error", "学生对话失败: ${e.message}"))
        }
    }
    
    /**
     * 教师端智能体
     * 使用专门的教师端API Key: app-56XMBM9poUyIyfAKnIXvi459
     */
    override suspend fun teacherChat(
        userId: String,
        message: String,
        conversationId: String?,
        context: TeacherContext?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "教师端智能体对话: $message")
        
        val inputs = mutableMapOf<String, Any>()
        context?.let {
            inputs["subject"] = it.subject ?: ""
            inputs["grade"] = it.grade ?: ""
            inputs["course_content"] = it.courseContent ?: ""
            inputs["teaching_goal"] = it.teachingGoal ?: ""
            inputs["class_size"] = it.classSize?.toString() ?: ""
        }
        
        val request = ChatRequest(
            inputs = mutableMapOf<String, Any>(),
            query = message,
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = enhancedApiService.teacherChat(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "教师对话失败", e)
            emit(ChatStreamEvent.Error("error", "教师对话失败: ${e.message}"))
        }
    }
    
    /**
     * 获取对话历史
     */
    override suspend fun getConversationHistory(
        userId: String,
        conversationId: String
    ): Flow<List<ChatResponse>> = flow {
        try {
            // 从会话ID中推断智能体类型
            val agentType = extractAgentTypeFromConversationId(conversationId)
            val response = enhancedApiService.getAgentMessages(agentType, conversationId, userId, limit = 50)
            
            if (response.isSuccessful) {
                val messagesResponse = response.body()
                val chatResponses = messagesResponse?.data?.map { message ->
                    ChatResponse(
                        event = "message",
                        messageId = message.id,
                        conversationId = message.conversationId,
                        answer = message.answer,
                        createdAt = message.createdAt
                    )
                } ?: emptyList()
                emit(chatResponses)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取会话历史失败", e)
            emit(emptyList())
        }
    }
    
    /**
     * 删除对话历史
     */
    override suspend fun deleteConversation(
        conversationId: String,
        userId: String
    ): Flow<ChatResponse> = flow {
        try {
            val agentType = extractAgentTypeFromConversationId(conversationId)
            val deleteRequest = DeleteConversationRequest(user = userId)
            
            // 根据智能体类型使用对应的API Key进行删除
            val response = when (agentType) {
                ROLE_KNOWLEDGE_BASE -> enhancedApiService.knowledgeBaseChat(
                    ChatRequest(query = "", user = userId, conversationId = conversationId)
                )
                ROLE_TUTORING -> enhancedApiService.tutoringChat(
                    ChatRequest(query = "", user = userId, conversationId = conversationId)
                )
                ROLE_ASSESSMENT -> enhancedApiService.assessmentChat(
                    ChatRequest(query = "", user = userId, conversationId = conversationId)
                )
                ROLE_STUDENT -> enhancedApiService.studentChat(
                    ChatRequest(query = "", user = userId, conversationId = conversationId)
                )
                ROLE_TEACHER -> enhancedApiService.teacherChat(
                    ChatRequest(query = "", user = userId, conversationId = conversationId)
                )
                else -> enhancedApiService.studentChat(
                    ChatRequest(query = "", user = userId, conversationId = conversationId)
                )
            }
            
            emit(ChatResponse(
                event = "conversation_deleted",
                conversationId = conversationId,
                answer = "对话已删除"
            ))
        } catch (e: Exception) {
            Log.e(TAG, "删除对话失败", e)
            emit(ChatResponse(
                event = "error",
                conversationId = conversationId,
                answer = "删除失败: ${e.message}"
            ))
        }
    }
    
    /**
     * 处理流式响应
     */
    private suspend fun FlowCollector<ChatStreamEvent>.emitStreamingResponse(
        response: retrofit2.Response<okhttp3.ResponseBody>
    ) {
        if (!response.isSuccessful) {
            emit(ChatStreamEvent.Error("http_error", "HTTP ${response.code()}: ${response.message()}"))
            return
        }
        
        val contentType = response.headers()["Content-Type"] ?: ""
        val responseBody = response.body()
        if (responseBody == null) {
            emit(ChatStreamEvent.Error("empty_response", "响应体为空"))
            return
        }
        
        // 如果是JSON阻塞模式，直接解析
        if (contentType.contains("application/json")) {
            try {
                val jsonString = responseBody.string()
                val completion = json.decodeFromString<ChatCompletionResponse>(jsonString)
                emit(ChatStreamEvent.MessageEnd(completion.messageId, completion.answer))
            } catch (e: Exception) {
                Log.e(TAG, "解析阻塞响应失败", e)
                emit(ChatStreamEvent.Error("parse_error", "解析阻塞响应失败: ${e.message}"))
            }
            return
        }

        try {
            responseBody.byteStream().bufferedReader().use { reader ->
                reader.lineSequence().forEach { line ->
                    if (line.isNotBlank() && line.startsWith("data: ")) {
                        val jsonData = line.removePrefix("data: ").trim()
                        
                        if (jsonData == "[DONE]") {
                            return
                        }
                        
                        try {
                            val streamEvent = parseStreamEvent(jsonData)
                            emit(streamEvent)
                        } catch (e: Exception) {
                            Log.w(TAG, "解析流事件失败: $jsonData", e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "处理流式响应失败", e)
            emit(ChatStreamEvent.Error("stream_error", "处理流式响应失败: ${e.message}"))
        }
    }
    
    /**
     * 解析流事件
     */
    private fun parseStreamEvent(jsonData: String): ChatStreamEvent {
        return try {
            val chatResponse = json.decodeFromString<ChatResponse>(jsonData)
            
            when (chatResponse.event) {
                "message" -> {
                    if (chatResponse.answer.isNullOrBlank()) {
                        ChatStreamEvent.MessageDelta(
                            delta = chatResponse.answer ?: "",
                            messageId = chatResponse.messageId ?: ""
                        )
                    } else {
                        ChatStreamEvent.MessageEnd(
                            messageId = chatResponse.messageId ?: "",
                            message = chatResponse.answer,
                            metadata = chatResponse.metadata ?: emptyMap()
                        )
                    }
                }
                "message_start" -> ChatStreamEvent.MessageStart(
                    messageId = chatResponse.messageId ?: "",
                    conversationId = chatResponse.conversationId ?: "",
                    createdAt = chatResponse.createdAt ?: System.currentTimeMillis()
                )
                "message_delta" -> ChatStreamEvent.MessageDelta(
                    delta = chatResponse.answer ?: "",
                    messageId = chatResponse.messageId ?: ""
                )
                "message_end" -> ChatStreamEvent.MessageEnd(
                    messageId = chatResponse.messageId ?: "",
                    message = chatResponse.answer ?: "",
                    metadata = chatResponse.metadata ?: emptyMap()
                )
                "error" -> ChatStreamEvent.Error(
                    code = "api_error",
                    message = chatResponse.answer ?: "未知错误"
                )
                else -> ChatStreamEvent.MessageDelta(
                    delta = chatResponse.answer ?: "",
                    messageId = chatResponse.messageId ?: ""
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "解析ChatResponse失败，尝试直接解析", e)
            ChatStreamEvent.Error("parse_error", "解析失败: ${e.message}")
        }
    }
    
    /**
     * 构建知识库查询
     */
    private fun buildKnowledgeQuery(query: String): String {
        return "知识库检索请求：$query\n请基于知识库内容提供准确、详细的回答，并构建相关的知识图谱关系。"
    }
    
    /**
     * 构建辅导查询
     */
    private fun buildTutoringQuery(question: String, level: String?): String {
        val levelText = when (level) {
            "beginner" -> "初学者"
            "intermediate" -> "中级"
            "advanced" -> "高级"
            else -> "适中"
        }
        return "学生问题：$question\n请以${levelText}水平提供耐心细致的辅导解答，采用苏格拉底式提问引导学习。"
    }
    
    /**
     * 构建评估查询
     */
    private fun buildAssessmentQuery(
        topic: String,
        difficulty: String,
        questionTypes: List<String>
    ): String {
        val typesText = questionTypes.joinToString("、") { type ->
            when (type) {
                "choice" -> "选择题"
                "fill" -> "填空题"
                "analysis" -> "分析题"
                "programming" -> "编程题"
                else -> type
            }
        }
        val difficultyText = when (difficulty) {
            "easy" -> "简单"
            "medium" -> "中等"
            "hard" -> "困难"
            else -> difficulty
        }
        return "为「$topic」生成$difficultyText 难度的$typesText，确保题目多样性并提供详细参考答案。"
    }
    
    /**
     * 生成会话ID
     */
    private fun generateConversationId(userId: String, role: String): String {
        return ApiConstants.ConversationIdFormat.generateConversationId(userId, role)
    }
    
    /**
     * 从会话ID中提取智能体类型
     */
    private fun extractAgentTypeFromConversationId(conversationId: String): String {
        return when {
            conversationId.contains("knowledge") || conversationId.contains("kb") -> ROLE_KNOWLEDGE_BASE
            conversationId.contains("tutoring") -> ROLE_TUTORING
            conversationId.contains("assessment") -> ROLE_ASSESSMENT
            conversationId.contains("teacher") -> ROLE_TEACHER
            conversationId.contains("student") -> ROLE_STUDENT
            else -> ROLE_STUDENT // 默认为学生端
        }
    }
} 