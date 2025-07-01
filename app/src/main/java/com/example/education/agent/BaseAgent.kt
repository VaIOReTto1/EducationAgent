package com.example.education.agent

import com.example.education.core.network.model.ChatMessageRequest
import com.example.education.core.network.model.StreamChatMessageResponse
import com.example.education.core.network.service.DifyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.BufferedReader
import javax.inject.Inject

/**
 * 基础智能体类
 * 提供与Dify API交互的通用功能
 */
abstract class BaseAgent constructor(
    protected val apiService: DifyApiService
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * 发送流式聊天消息
     * @param query 用户查询
     * @param userId 用户ID
     * @param conversationId 会话ID（可选）
     * @param inputs 额外输入参数
     * @return 流式响应Flow
     */
    protected suspend fun sendStreamMessage(
        query: String,
        userId: String,
        conversationId: String? = null,
        inputs: Map<String, String> = emptyMap()
    ): Flow<StreamChatMessageResponse> = flow {
        val request = ChatMessageRequest(
            query = query,
            user = userId,
            conversationId = conversationId,
            inputs = inputs,
            responseMode = "streaming"
        )

        val response = apiService.sendChatMessageStream(request)
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                parseStreamResponse(responseBody).collect { streamResponse ->
                    emit(streamResponse)
                }
            }
        } else {
            // 处理错误响应
            emit(
                StreamChatMessageResponse(
                    event = "error",
                    status = response.code().toString(),
                    message = "API请求失败: ${response.message()}"
                )
            )
        }
    }

    /**
     * 解析流式响应
     */
    private fun parseStreamResponse(responseBody: ResponseBody): Flow<StreamChatMessageResponse> = flow {
        responseBody.byteStream().bufferedReader().use { reader ->
            reader.lineSequence().forEach { line ->
                if (line.startsWith("data: ")) {
                    val jsonData = line.removePrefix("data: ").trim()
                    if (jsonData.isNotEmpty() && jsonData != "[DONE]") {
                        try {
                            val streamResponse = json.decodeFromString<StreamChatMessageResponse>(jsonData)
                            emit(streamResponse)
                        } catch (e: Exception) {
                            // 忽略解析错误的行
                            println("解析流式响应失败: $jsonData, 错误: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    /**
     * 生成会话ID
     * 格式: {userId}_{role}_{timestamp}
     */
    protected fun generateConversationId(userId: String, role: String): String {
        return "${userId}_${role}_${System.currentTimeMillis()}"
    }
}

/**
 * 知识库管理智能体
 * 负责知识检索和知识图谱构建
 */
class KnowledgeBaseAgent @Inject constructor(
    apiService: DifyApiService
) : BaseAgent(apiService) {

    /**
     * 检索知识库内容
     */
    suspend fun searchKnowledge(
        query: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "检索关于\"$query\"的教学资料",
            userId = userId,
            conversationId = generateConversationId(userId, "kb")
        )
    }

    /**
     * 更新知识图谱
     */
    suspend fun updateKnowledgeGraph(
        content: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "更新知识图谱：$content",
            userId = userId,
            conversationId = generateConversationId(userId, "kb")
        )
    }
}

/**
 * 教学辅导智能体
 * 负责学生辅导和答疑
 */
class TutoringAgent @Inject constructor(
    apiService: DifyApiService
) : BaseAgent(apiService) {

    /**
     * 回答学生问题
     */
    suspend fun answerQuestion(
        question: String,
        userId: String,
        conversationId: String? = null
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = question,
            userId = userId,
            conversationId = conversationId ?: generateConversationId(userId, "tutoring")
        )
    }

    /**
     * 提供学习指导
     */
    suspend fun provideLearningGuidance(
        topic: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "请为\"$topic\"提供学习指导",
            userId = userId,
            conversationId = generateConversationId(userId, "tutoring")
        )
    }
}

/**
 * 评估智能体
 * 负责生成评估题目和评分
 */
class AssessmentAgent @Inject constructor(
    apiService: DifyApiService
) : BaseAgent(apiService) {

    /**
     * 生成评估题目
     */
    suspend fun generateAssessment(
        topic: String,
        difficulty: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "为\"$topic\"生成${difficulty}难度的评估题目",
            userId = userId,
            conversationId = generateConversationId(userId, "assessment")
        )
    }

    /**
     * 评估学生答案
     */
    suspend fun evaluateAnswer(
        question: String,
        answer: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "评估以下答案：\n问题：$question\n答案：$answer",
            userId = userId,
            conversationId = generateConversationId(userId, "assessment")
        )
    }
}

/**
 * 学生端智能体
 * 负责学生学习进度跟踪和个性化推荐
 */
class StudentAgent @Inject constructor(
    apiService: DifyApiService
) : BaseAgent(apiService) {

    /**
     * 获取学习建议
     */
    suspend fun getLearningAdvice(
        currentTopic: String,
        progress: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "当前学习主题：$currentTopic，学习进度：$progress，请提供学习建议",
            userId = userId,
            conversationId = generateConversationId(userId, "student")
        )
    }

    /**
     * 请求练习题
     */
    suspend fun requestPractice(
        topic: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "请为\"$topic\"提供练习题",
            userId = userId,
            conversationId = generateConversationId(userId, "student")
        )
    }
}

/**
 * 教师端智能体
 * 负责课程设计和教学分析
 */
class TeacherAgent @Inject constructor(
    apiService: DifyApiService
) : BaseAgent(apiService) {

    /**
     * 生成教案
     */
    suspend fun generateLessonPlan(
        syllabus: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "根据以下教学大纲生成教案：$syllabus",
            userId = userId,
            conversationId = generateConversationId(userId, "teacher")
        )
    }

    /**
     * 分析学生表现
     */
    suspend fun analyzeStudentPerformance(
        performanceData: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "分析以下学生表现数据：$performanceData",
            userId = userId,
            conversationId = generateConversationId(userId, "teacher")
        )
    }

    /**
     * 设计课程内容
     */
    suspend fun designCourseContent(
        subject: String,
        targetAudience: String,
        userId: String
    ): Flow<StreamChatMessageResponse> {
        return sendStreamMessage(
            query = "为\"$subject\"设计面向\"$targetAudience\"的课程内容",
            userId = userId,
            conversationId = generateConversationId(userId, "teacher")
        )
    }
}