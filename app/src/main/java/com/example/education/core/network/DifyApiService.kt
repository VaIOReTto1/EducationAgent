package com.example.education.core.network

import com.example.education.core.network.models.*
import com.example.education.core.network.models.DeleteConversationRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Dify API 服务接口
 * 
 * 定义所有与 Dify 工作流编排对话型应用 API 的交互方法
 */
interface DifyApiService {
    
    /**
     * 发送聊天消息（流式模式）
     * 
     * @param request 聊天消息请求
     * @return 流式响应体
     */
    @POST(ApiConstants.Endpoints.CHAT_MESSAGES)
    @Streaming
    suspend fun sendChatMessageStreaming(
        @Body request: ChatMessageRequest
    ): Response<ResponseBody>
    
    /**
     * 发送聊天消息（阻塞模式）
     * 
     * @param request 聊天消息请求
     * @return 聊天完成响应
     */
    @POST(ApiConstants.Endpoints.CHAT_MESSAGES)
    suspend fun sendChatMessageBlocking(
        @Body request: ChatMessageRequest
    ): Response<ChatCompletionResponse>
    
    /**
     * 获取会话历史消息
     * 
     * @param conversationId 会话ID
     * @param user 用户标识
     * @param firstId 当前页第一条记录的ID
     * @param limit 返回记录数
     * @return 消息列表响应
     */
    @GET(ApiConstants.Endpoints.MESSAGES)
    suspend fun getMessages(
        @Query("conversation_id") conversationId: String,
        @Query("user") user: String,
        @Query("first_id") firstId: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<MessagesResponse>
    
    /**
     * 获取会话列表
     * 
     * @param user 用户标识
     * @param lastId 当前页最后一条记录的ID
     * @param limit 返回记录数
     * @param sortBy 排序字段
     * @return 会话列表响应
     */
    @GET(ApiConstants.Endpoints.CONVERSATIONS)
    suspend fun getConversations(
        @Query("user") user: String,
        @Query("last_id") lastId: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("sort_by") sortBy: String = "-updated_at"
    ): Response<ConversationsResponse>
    
    /**
     * 删除会话
     * 
     * @param conversationId 会话ID
     * @param deleteRequest 删除请求
     * @return 删除结果
     */
    @DELETE("conversations/{conversation_id}")
    suspend fun deleteConversation(
        @Path("conversation_id") conversationId: String,
        @Body deleteRequest: DeleteConversationRequest
    ): Response<Unit>
    
    /**
     * 会话重命名
     * 
     * @param conversationId 会话ID
     * @param renameRequest 重命名请求
     * @return 会话项目
     */
    @POST("conversations/{conversation_id}/name")
    suspend fun renameConversation(
        @Path("conversation_id") conversationId: String,
        @Body renameRequest: RenameConversationRequest
    ): Response<ConversationItem>
    
    /**
     * 获取对话变量
     * 
     * @param conversationId 会话ID
     * @param user 用户标识
     * @param lastId 当前页最后一条记录的ID
     * @param limit 返回记录数
     * @return 变量列表响应
     */
    @GET("conversations/{conversation_id}/variables")
    suspend fun getConversationVariables(
        @Path("conversation_id") conversationId: String,
        @Query("user") user: String,
        @Query("last_id") lastId: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<VariablesResponse>
    
    /**
     * 提供消息反馈
     * 
     * @param messageId 消息ID
     * @param feedbackRequest 反馈请求
     * @return 反馈结果
     */
    @POST("messages/{message_id}/feedbacks")
    suspend fun submitMessageFeedback(
        @Path("message_id") messageId: String,
        @Body feedbackRequest: MessageFeedbackRequest
    ): Response<Unit>
}

// 额外的请求模型
/**
 * 删除会话请求
 */
data class DeleteConversationRequest(
    val user: String
)

/**
 * 重命名会话请求
 */
data class RenameConversationRequest(
    val name: String? = null,
    val autoGenerate: Boolean = false,
    val user: String
)

/**
 * 变量列表响应
 */
data class VariablesResponse(
    val limit: Int,
    val hasMore: Boolean,
    val data: List<ConversationVariable>
)

/**
 * 会话变量
 */
data class ConversationVariable(
    val id: String,
    val name: String,
    val valueType: String,
    val value: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * 消息反馈请求
 */
data class MessageFeedbackRequest(
    val rating: String, // like, dislike
    val content: String? = null,
    val user: String
) 