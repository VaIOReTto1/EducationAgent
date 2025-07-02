package com.example.education.agent

import android.util.Log
import com.example.education.core.network.DifyApiService
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
 * 基于Dify API实现五个智能体的交互逻辑
 */
@Singleton
class AgentRepositoryImpl @Inject constructor(
    private val apiService: DifyApiService,
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
     * 根据文档："管理与本地知识库的交互，执行高效检索，构建知识图谱"
     */
    override suspend fun queryKnowledgeBase(
        userId: String,
        query: String,
        conversationId: String?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "知识库管理智能体查询: $query")
        
        val request = ChatMessageRequest(
            inputs = mapOf(),
            query = buildKnowledgeQuery(query),
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = apiService.sendChatMessageStreaming(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "知识库查询失败", e)
            emit(ChatStreamEvent.Error("error", "知识库查询失败: ${e.message}"))
        }
    }
    
    /**
     * 辅导智能体
     * 根据文档："动态管理学生互动，提供高质量的教学支持"
     */
    override suspend fun startTutoring(
        userId: String,
        question: String,
        conversationId: String?,
        studentLevel: String?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "辅导智能体启动: $question")
        
        val request = ChatMessageRequest(
            inputs = mapOf(),
            query = buildTutoringQuery(question, studentLevel),
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = apiService.sendChatMessageStreaming(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "辅导服务失败", e)
            emit(ChatStreamEvent.Error("error", "辅导服务失败: ${e.message}"))
        }
    }
    
    /**
     * 评估智能体
     * 根据文档："生成多样化评估项目，确保难度适中，提供参考答案"
     */
    override suspend fun generateAssessment(
        userId: String,
        topic: String,
        difficulty: String,
        questionTypes: List<String>,
        conversationId: String?
    ): Flow<ChatStreamEvent> = flow {
        Log.d(TAG, "评估智能体生成题目: $topic, 难度: $difficulty")
        
        val request = ChatMessageRequest(
            inputs = mapOf(),
            query = buildAssessmentQuery(topic, difficulty, questionTypes),
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = apiService.sendChatMessageStreaming(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "评估生成失败", e)
            emit(ChatStreamEvent.Error("error", "评估生成失败: ${e.message}"))
        }
    }
    
    /**
     * 学生端智能体
     * 学习进度跟踪、答疑解惑、个性化指导
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
        
        val request = ChatMessageRequest(
            inputs = mutableMapOf<String, Any>(),
            query = message,
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = apiService.sendChatMessageStreaming(request)
            emitStreamingResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "学生对话失败", e)
            emit(ChatStreamEvent.Error("error", "学生对话失败: ${e.message}"))
        }
    }
    
    /**
     * 教师端智能体
     * 智能备课、内容生成、教学建议
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
        
        val request = ChatMessageRequest(
            inputs = mutableMapOf<String, Any>(),
            query = message,
            user = userId,
            responseMode = ApiConstants.ResponseMode.BLOCKING,
            conversationId = ""
        )
        
        try {
            val response = apiService.sendChatMessageStreaming(request)
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
            val response = apiService.getMessages(conversationId, userId, limit = 50)
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
            val deleteRequest = DeleteConversationRequest(user = userId)
            val response = apiService.deleteConversation(conversationId, deleteRequest)
            if (response.isSuccessful) {
                emit(ChatResponse(
                    event = "conversation_deleted",
                    conversationId = conversationId,
                    answer = "对话已删除"
                ))
            } else {
                emit(ChatResponse(
                    event = "error",
                    conversationId = conversationId,
                    answer = "删除失败"
                ))
            }
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
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                val contentType = response.headers()["Content-Type"] ?: ""
                if (contentType.contains("application/json")) {
                    // 非流式阻塞模式
                    val jsonString = responseBody.string()
                    try {
                        val completion = json.decodeFromString<ChatCompletionResponse>(jsonString)
                        emit(ChatStreamEvent.MessageEnd(completion.messageId, completion.answer))
                    } catch (e: Exception) {
                        Log.e(TAG, "解析阻塞模式响应失败", e)
                        emit(ChatStreamEvent.Error("parse_error", "解析响应失败: ${e.message}"))
                    }
                    return
                }
                val source = responseBody.source()
                try {
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line()
                        if (line != null && line.startsWith("data: ")) {
                            val jsonData = line.substring(6).trim()
                            if (jsonData != "[DONE]") {
                                try {
                                    if (jsonData.contains("\"event\":\"message\"")) {
                                        val chatResp = json.decodeFromString<StreamChatResponse>(jsonData)
                                        emit(ChatStreamEvent.MessageEnd(chatResp.messageId ?: "", chatResp.answer ?: ""))
                                    } else if (jsonData.contains("\"event\":\"message_replace\"")) {
                                        val chatResp = json.decodeFromString<StreamChatResponse>(jsonData)
                                        emit(ChatStreamEvent.MessageReplace(chatResp.messageId ?: "", chatResp.answer ?: ""))
                                    }
                                } catch (e: Exception) {
                                    Log.w(TAG, "解析流式响应失败: $jsonData", e)
                                }
                            }
                        }
                    }
                } finally {
                    responseBody.close()
                }
            }
        } else {
            emit(ChatStreamEvent.Error("http_error", "HTTP ${response.code()}: ${response.message()}"))
        }
    }
    
    /**
     * 构建知识库查询
     */
    private fun buildKnowledgeQuery(query: String): String {
        return "检索关于「$query」的教学资料，提供相关的知识图谱信息和参考文献。"
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
} 