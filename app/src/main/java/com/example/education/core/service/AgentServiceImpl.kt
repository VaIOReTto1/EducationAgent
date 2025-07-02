package com.example.education.core.service

import android.util.Log
import com.example.education.agent.AgentRepository
import com.example.education.agent.StudentContext
import com.example.education.agent.TeacherContext
import com.example.education.core.network.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI智能体服务实现类
 * 
 * 提供统一的AI智能体交互接口
 */
@Singleton
class AgentServiceImpl @Inject constructor(
    private val agentRepository: AgentRepository
) : AgentService {
    
    companion object {
        private const val TAG = "AgentServiceImpl"
    }
    
    override fun chatWithCurriculumAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse> = flow {
        Log.d(TAG, "与课程规划智能体对话: $message")
        
        agentRepository.queryKnowledgeBase(
            userId = context.userId,
            query = message,
            conversationId = "${context.userId}_curriculum"
        ).map { event ->
            when (event) {
                is ChatStreamEvent.MessageEnd -> ChatResponse(
                    event = "message",
                    answer = event.message,
                    conversationId = "${context.userId}_curriculum",
                    createdAt = System.currentTimeMillis()
                )
                is ChatStreamEvent.Error -> ChatResponse(
                    event = "error",
                    answer = "抱歉，课程规划服务暂时不可用：${event.message}",
                    conversationId = "${context.userId}_curriculum",
                    createdAt = System.currentTimeMillis()
                )
                else -> ChatResponse(
                    event = "processing",
                    answer = "正在处理中...",
                    conversationId = "${context.userId}_curriculum",
                    createdAt = System.currentTimeMillis()
                )
            }
        }.catch { e ->
            Log.e(TAG, "课程规划智能体对话失败", e)
            emit(ChatResponse(
                event = "error",
                answer = "抱歉，课程规划服务暂时不可用，请稍后重试。",
                conversationId = "${context.userId}_curriculum",
                createdAt = System.currentTimeMillis()
            ))
        }.collect { response ->
            Log.d(TAG, "课程规划智能体回复: ${response.answer}")
            emit(response)
        }
    }
    
    override fun chatWithTutoringAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse> = flow {
        Log.d(TAG, "与个性化辅导智能体对话: $message")
        
        agentRepository.startTutoring(
            userId = context.userId,
            question = message,
            conversationId = "${context.userId}_tutoring",
            studentLevel = context.academicLevel
        ).map { event ->
            when (event) {
                is ChatStreamEvent.MessageEnd -> ChatResponse(
                    event = "message",
                    answer = event.message,
                    conversationId = "${context.userId}_tutoring",
                    createdAt = System.currentTimeMillis()
                )
                is ChatStreamEvent.Error -> ChatResponse(
                    event = "error",
                    answer = "抱歉，个性化辅导服务暂时不可用：${event.message}",
                    conversationId = "${context.userId}_tutoring",
                    createdAt = System.currentTimeMillis()
                )
                else -> ChatResponse(
                    event = "processing",
                    answer = "正在处理中...",
                    conversationId = "${context.userId}_tutoring",
                    createdAt = System.currentTimeMillis()
                )
            }
        }.catch { e ->
            Log.e(TAG, "个性化辅导智能体对话失败", e)
            emit(ChatResponse(
                event = "error",
                answer = "抱歉，个性化辅导服务暂时不可用，请稍后重试。",
                conversationId = "${context.userId}_tutoring",
                createdAt = System.currentTimeMillis()
            ))
        }.collect { response ->
            Log.d(TAG, "个性化辅导智能体回复: ${response.answer}")
            emit(response)
        }
    }
    
    override fun chatWithAssessmentAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse> = flow {
        Log.d(TAG, "与评估反馈智能体对话: $message")
        
        agentRepository.generateAssessment(
            userId = context.userId,
            topic = message,
            difficulty = context.academicLevel,
            questionTypes = listOf("multiple_choice", "short_answer"),
            conversationId = "${context.userId}_assessment"
        ).map { event ->
            when (event) {
                is ChatStreamEvent.MessageEnd -> ChatResponse(
                    event = "message", 
                    answer = event.message,
                    conversationId = "${context.userId}_assessment",
                    createdAt = System.currentTimeMillis()
                )
                is ChatStreamEvent.Error -> ChatResponse(
                    event = "error",
                    answer = "抱歉，评估反馈服务暂时不可用：${event.message}",
                    conversationId = "${context.userId}_assessment",
                    createdAt = System.currentTimeMillis()
                )
                else -> ChatResponse(
                    event = "processing",
                    answer = "正在处理中...",
                    conversationId = "${context.userId}_assessment",
                    createdAt = System.currentTimeMillis()
                )
            }
        }.catch { e ->
            Log.e(TAG, "评估反馈智能体对话失败", e)
            emit(ChatResponse(
                event = "error",
                answer = "抱歉，评估反馈服务暂时不可用，请稍后重试。",
                conversationId = "${context.userId}_assessment",
                createdAt = System.currentTimeMillis()
            ))
        }.collect { response ->
            Log.d(TAG, "评估反馈智能体回复: ${response.answer}")
            emit(response)
        }
    }
    
    override fun chatWithKnowledgeAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse> = flow {
        Log.d(TAG, "与知识检索智能体对话: $message")
        
        agentRepository.queryKnowledgeBase(
            userId = context.userId,
            query = message,
            conversationId = "${context.userId}_knowledge"
        ).map { event ->
            when (event) {
                is ChatStreamEvent.MessageEnd -> ChatResponse(
                    event = "message",
                    answer = event.message,
                    conversationId = "${context.userId}_knowledge",
                    createdAt = System.currentTimeMillis()
                )
                is ChatStreamEvent.Error -> ChatResponse(
                    event = "error",
                    answer = "抱歉，知识检索服务暂时不可用：${event.message}",
                    conversationId = "${context.userId}_knowledge",
                    createdAt = System.currentTimeMillis()
                )
                else -> ChatResponse(
                    event = "processing",
                    answer = "正在处理中...",
                    conversationId = "${context.userId}_knowledge",
                    createdAt = System.currentTimeMillis()
                )
            }
        }.catch { e ->
            Log.e(TAG, "知识检索智能体对话失败", e)
            emit(ChatResponse(
                event = "error",
                answer = "抱歉，知识检索服务暂时不可用，请稍后重试。",
                conversationId = "${context.userId}_knowledge",
                createdAt = System.currentTimeMillis()
            ))
        }.collect { response ->
            Log.d(TAG, "知识检索智能体回复: ${response.answer}")
            emit(response)
        }
    }
    
    override fun chatWithDialogueAgent(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse> = flow {
        Log.d(TAG, "与对话管理智能体对话: $message")
        
        agentRepository.studentChat(
            userId = context.userId,
            message = message,
            conversationId = "${context.userId}_dialogue",
            context = context
        ).map { event ->
            when (event) {
                is ChatStreamEvent.MessageEnd -> ChatResponse(
                    event = "message",
                    answer = event.message,
                    conversationId = "${context.userId}_dialogue",
                    createdAt = System.currentTimeMillis()
                )
                is ChatStreamEvent.Error -> ChatResponse(
                    event = "error",
                    answer = "抱歉，对话服务暂时不可用：${event.message}",
                    conversationId = "${context.userId}_dialogue",
                    createdAt = System.currentTimeMillis()
                )
                else -> ChatResponse(
                    event = "processing",
                    answer = "正在处理中...",
                    conversationId = "${context.userId}_dialogue",
                    createdAt = System.currentTimeMillis()
                )
            }
        }.catch { e ->
            Log.e(TAG, "对话管理智能体对话失败", e)
            emit(ChatResponse(
                event = "error",
                answer = "抱歉，对话服务暂时不可用，请稍后重试。",
                conversationId = "${context.userId}_dialogue",
                createdAt = System.currentTimeMillis()
            ))
        }.collect { response ->
            Log.d(TAG, "对话管理智能体回复: ${response.answer}")
            emit(response)
        }
    }
    
    /**
     * 智能路由：根据用户消息自动选择合适的智能体
     */
    override fun smartRouteMessage(
        message: String,
        context: StudentContext
    ): Flow<ChatResponse> = flow {
        Log.d(TAG, "智能路由消息: $message")
        
        val targetAgent = determineTargetAgent(message)
        Log.d(TAG, "选择智能体: $targetAgent")
        
        val responseFlow = when (targetAgent) {
            AgentType.CURRICULUM -> chatWithCurriculumAgent(message, context)
            AgentType.TUTORING -> chatWithTutoringAgent(message, context)
            AgentType.ASSESSMENT -> chatWithAssessmentAgent(message, context)
            AgentType.KNOWLEDGE -> chatWithKnowledgeAgent(message, context)
            AgentType.DIALOGUE -> chatWithDialogueAgent(message, context)
        }
        
        responseFlow.collect { response ->
            emit(response.copy(
                metadata = response.metadata?.plus("routed_agent" to targetAgent.name)
            ))
        }
    }
    
    /**
     * 根据消息内容确定目标智能体
     */
    private fun determineTargetAgent(message: String): AgentType {
        val lowerMessage = message.lowercase()
        
        return when {
            // 课程规划相关关键词
            lowerMessage.contains("课程") || lowerMessage.contains("学习计划") ||
            lowerMessage.contains("进度安排") || lowerMessage.contains("学习路径") -> AgentType.CURRICULUM
            
            // 个性化辅导相关关键词
            lowerMessage.contains("不懂") || lowerMessage.contains("解释") ||
            lowerMessage.contains("怎么学") || lowerMessage.contains("方法") -> AgentType.TUTORING
            
            // 评估反馈相关关键词
            lowerMessage.contains("测试") || lowerMessage.contains("评估") ||
            lowerMessage.contains("检查") || lowerMessage.contains("反馈") -> AgentType.ASSESSMENT
            
            // 知识检索相关关键词
            lowerMessage.contains("什么是") || lowerMessage.contains("查找") ||
            lowerMessage.contains("搜索") || lowerMessage.contains("定义") -> AgentType.KNOWLEDGE
            
            // 默认使用对话管理智能体
            else -> AgentType.DIALOGUE
        }
    }
}

/**
 * 智能体类型枚举
 */
enum class AgentType {
    CURRICULUM,  // 课程规划
    TUTORING,    // 个性化辅导  
    ASSESSMENT,  // 评估反馈
    KNOWLEDGE,   // 知识检索
    DIALOGUE     // 对话管理
} 