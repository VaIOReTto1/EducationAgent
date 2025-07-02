package com.example.education.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.agent.AgentRepository
import com.example.education.agent.StudentContext
import com.example.education.agent.TeacherContext
import com.example.education.core.network.models.ChatStreamEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 聊天界面ViewModel
 * 
 * 管理与五个智能体的对话状态和消息流
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "ChatViewModel"
    }
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private var currentUserId = "user_001" // TODO: 从认证系统获取
    private var currentAgentType = ""
    private var conversationId: String? = null
    
    /**
     * 初始化智能体
     */
    fun initAgent(agentType: String) {
        Log.d(TAG, "初始化智能体: $agentType")
        currentAgentType = agentType
        conversationId = generateConversationId(currentUserId, agentType)
        
        // 可以在这里加载历史对话
        loadConversationHistory()
    }
    
    /**
     * 更新输入消息
     */
    fun updateInputMessage(message: String) {
        _uiState.value = _uiState.value.copy(inputMessage = message)
    }
    
    /**
     * 发送消息
     */
    fun sendMessage() {
        val message = _uiState.value.inputMessage.trim()
        if (message.isEmpty()) return
        
        Log.d(TAG, "发送消息: $message")
        
        viewModelScope.launch {
            // 添加用户消息到列表
            val userMessage = ChatMessage(
                id = generateMessageId(),
                content = message,
                isFromUser = true,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENDING
            )
            
            val currentMessages = _uiState.value.messages.toMutableList()
            currentMessages.add(userMessage)
            
            _uiState.value = _uiState.value.copy(
                messages = currentMessages,
                inputMessage = "",
                isLoading = true,
                isTyping = true
            )
            
            try {
                // 标记用户消息为已发送
                val updatedUserMessage = userMessage.copy(status = MessageStatus.SENT)
                currentMessages[currentMessages.size - 1] = updatedUserMessage
                _uiState.value = _uiState.value.copy(messages = currentMessages.toList())
                
                // 调用相应的智能体
                val responseFlow = when (currentAgentType) {
                    "knowledge_base" -> agentRepository.queryKnowledgeBase(
                        userId = currentUserId,
                        query = message,
                        conversationId = conversationId
                    )
                    "tutoring" -> agentRepository.startTutoring(
                        userId = currentUserId,
                        question = message,
                        conversationId = conversationId,
                        studentLevel = "beginner"
                    )
                    "assessment" -> agentRepository.generateAssessment(
                        userId = currentUserId,
                        topic = message,
                        difficulty = "medium",
                        questionTypes = listOf("multiple_choice", "short_answer"),
                        conversationId = conversationId
                    )
                    "student" -> agentRepository.studentChat(
                        userId = currentUserId,
                        message = message,
                        conversationId = conversationId,
                        context = StudentContext(
                            userId = currentUserId,
                            currentCourse = "",
                            currentChapter = "",
                            learningProgress = 0f,
                            difficultyLevel = "beginner",
                            learningStyle = "visual",
                            academicLevel = "beginner",
                            learningGoals = emptyList(),
                            completedChapters = emptyList(),
                            difficultyPreference = "medium",
                            studyTimeAvailable = 60,
                            currentCourseId = "",
                            currentChapterId = "",
                            preferredStyle = "visual"
                        )
                    )
                    "teacher" -> agentRepository.teacherChat(
                        userId = currentUserId,
                        message = message,
                        conversationId = conversationId,
                        context = TeacherContext(
                            subject = "综合",
                            grade = "中学",
                            courseContent = "通用课程",
                            teachingGoal = "提升教学效果",
                            classSize = 30
                        )
                    )
                    else -> throw IllegalArgumentException("未知的智能体类型: $currentAgentType")
                }
                
                // 处理流式响应
                var aiMessage = ChatMessage(
                    id = generateMessageId(),
                    content = "",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis(),
                    status = MessageStatus.DELIVERED
                )
                
                val messagesWithAI = currentMessages.toMutableList()
                messagesWithAI.add(aiMessage)
                
                responseFlow.collect { event ->
                    when (event) {
                        is ChatStreamEvent.MessageStart -> {
                            Log.d(TAG, "AI消息开始")
                            // 可以在这里显示打字动画
                        }
                        is ChatStreamEvent.MessageDelta -> {
                            // 累积AI回复内容
                            aiMessage = aiMessage.copy(
                                content = aiMessage.content + event.delta
                            )
                            messagesWithAI[messagesWithAI.size - 1] = aiMessage
                            _uiState.value = _uiState.value.copy(
                                messages = messagesWithAI.toList(),
                                isTyping = true
                            )
                        }
                        is ChatStreamEvent.MessageEnd -> {
                            Log.d(TAG, "AI消息完成")
                            aiMessage = aiMessage.copy(
                                content = event.message
                            )
                            messagesWithAI[messagesWithAI.size - 1] = aiMessage
                            _uiState.value = _uiState.value.copy(
                                messages = messagesWithAI.toList(),
                                isLoading = false,
                                isTyping = false
                            )
                        }
                        is ChatStreamEvent.MessageReplace -> {
                            Log.d(TAG, "AI消息替换")
                            aiMessage = aiMessage.copy(
                                content = event.answer
                            )
                            messagesWithAI[messagesWithAI.size - 1] = aiMessage
                            _uiState.value = _uiState.value.copy(
                                messages = messagesWithAI.toList(),
                                isLoading = false,
                                isTyping = false
                            )
                        }
                        is ChatStreamEvent.Error -> {
                            Log.e(TAG, "AI回复出错: ${event.message}")
                            _uiState.value = _uiState.value.copy(
                                error = event.message,
                                isLoading = false,
                                isTyping = false
                            )
                        }
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "发送消息失败", e)
                
                // 标记用户消息为失败
                val failedUserMessage = userMessage.copy(status = MessageStatus.FAILED)
                currentMessages[currentMessages.size - 1] = failedUserMessage
                
                _uiState.value = _uiState.value.copy(
                    messages = currentMessages.toList(),
                    error = "发送失败: ${e.message}",
                    isLoading = false,
                    isTyping = false
                )
            }
        }
    }
    
    /**
     * 清空对话
     */
    fun clearConversation() {
        Log.d(TAG, "清空对话")
        viewModelScope.launch {
            try {
                if (conversationId != null) {
                    agentRepository.deleteConversation(conversationId!!, currentUserId)
                }
                _uiState.value = _uiState.value.copy(messages = emptyList())
            } catch (e: Exception) {
                Log.e(TAG, "清空对话失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "清空对话失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 显示智能体信息
     */
    fun showAgentInfo() {
        Log.d(TAG, "显示智能体信息: $currentAgentType")
        // TODO: 显示智能体信息对话框
    }
    
    /**
     * 导航返回
     */
    fun navigateBack() {
        Log.d(TAG, "导航返回")
        // TODO: 实现导航返回
    }
    
    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 加载对话历史
     */
    private fun loadConversationHistory() {
        if (conversationId == null) return
        
        viewModelScope.launch {
            try {
                agentRepository.getConversationHistory(
                    userId = currentUserId,
                    conversationId = conversationId!!
                ).collect { chatResponses ->
                    val messages = chatResponses.map { response ->
                        ChatMessage(
                            id = response.messageId ?: generateMessageId(),
                            content = response.answer ?: "",
                            isFromUser = false, // 历史消息默认为AI回复
                            timestamp = response.createdAt ?: System.currentTimeMillis(),
                            status = MessageStatus.DELIVERED
                        )
                    }
                    
                    _uiState.value = _uiState.value.copy(messages = messages)
                    Log.d(TAG, "加载历史消息 ${messages.size} 条")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "加载对话历史失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "加载历史失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 生成会话ID
     */
    private fun generateConversationId(userId: String, agentType: String): String {
        return "${userId}_${agentType}"
    }
    
    /**
     * 生成消息ID
     */
    private fun generateMessageId(): String {
        return "msg_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

/**
 * 聊天UI状态
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputMessage: String = "",
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val error: String? = null
)

/**
 * 聊天消息
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val status: MessageStatus = MessageStatus.DELIVERED
)

/**
 * 消息状态
 */
enum class MessageStatus {
    SENDING,    // 发送中
    SENT,       // 已发送
    DELIVERED,  // 已送达
    FAILED      // 发送失败
} 