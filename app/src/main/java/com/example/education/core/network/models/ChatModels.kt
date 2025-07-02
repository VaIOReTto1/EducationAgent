package com.example.education.core.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Dify API 聊天请求数据模型
 */
@JsonClass(generateAdapter = true)
data class ChatRequest(
    @Json(name = "query") 
    val query: String,
    @Json(name = "user") 
    val user: String,
    @Json(name = "conversation_id") 
    val conversationId: String? = null,
    @Json(name = "inputs") 
    val inputs: Map<String, Any> = emptyMap(),
    @Json(name = "response_mode") 
    val responseMode: String = "streaming", // streaming 或 blocking
    @Json(name = "auto_generate_name") 
    val autoGenerateName: Boolean = false
)

/**
 * Dify API 聊天响应数据模型
 */
@JsonClass(generateAdapter = true)
data class ChatResponse(
    @Json(name = "event") 
    val event: String,
    @Json(name = "task_id") 
    val taskId: String? = null,
    @Json(name = "id") 
    val id: String? = null,
    @Json(name = "message_id") 
    val messageId: String? = null,
    @Json(name = "conversation_id") 
    val conversationId: String? = null,
    @Json(name = "answer") 
    val answer: String? = null,
    @Json(name = "created_at") 
    val createdAt: Long? = null,
    @Json(name = "metadata") 
    val metadata: Map<String, Any>? = null
)

/**
 * 流式聊天事件
 */
sealed class ChatStreamEvent {
    /**
     * 消息开始事件
     */
    data class MessageStart(
        val messageId: String,
        val conversationId: String,
        val createdAt: Long
    ) : ChatStreamEvent()
    
    /**
     * 消息增量更新事件
     */
    data class MessageDelta(
        val delta: String,
        val messageId: String
    ) : ChatStreamEvent()
    
    /**
     * 消息结束事件
     */
    data class MessageEnd(
        val messageId: String,
        val message: String,
        val metadata: Map<String, Any> = emptyMap()
    ) : ChatStreamEvent()
    
    /**
     * 错误事件
     */
    data class Error(
        val code: String,
        val message: String,
        val status: Int = 400
    ) : ChatStreamEvent()
    
    /**
     * 会话完成事件
     */
    data class MessageReplace(
        val messageId: String,
        val answer: String
    ) : ChatStreamEvent()
}

/**
 * 删除会话请求
 */
@JsonClass(generateAdapter = true)
data class DeleteConversationRequest(
    @Json(name = "user") 
    val user: String
)

/**
 * Dify API 错误响应
 */
@JsonClass(generateAdapter = true)
data class DifyErrorResponse(
    @Json(name = "code") 
    val code: String,
    @Json(name = "message") 
    val message: String,
    @Json(name = "status") 
    val status: Int
)

/**
 * 消息历史记录
 */
@JsonClass(generateAdapter = true)
data class MessageHistory(
    @Json(name = "id") 
    val id: String,
    @Json(name = "conversation_id") 
    val conversationId: String,
    @Json(name = "query") 
    val query: String,
    @Json(name = "answer") 
    val answer: String,
    @Json(name = "created_at") 
    val createdAt: Long,
    @Json(name = "feedback") 
    val feedback: Map<String, Any>? = null
)

/**
 * 会话列表响应
 */
@JsonClass(generateAdapter = true)
data class ConversationListResponse(
    @Json(name = "has_more") 
    val hasMore: Boolean,
    @Json(name = "data") 
    val data: List<ConversationItem>
)

/**
 * 会话项目
 */
@JsonClass(generateAdapter = true)
data class ConversationItem(
    @Json(name = "id") 
    val id: String,
    @Json(name = "name") 
    val name: String,
    @Json(name = "inputs") 
    val inputs: Map<String, Any>,
    @Json(name = "introduction") 
    val introduction: String? = null,
    @Json(name = "created_at") 
    val createdAt: Long
)

/**
 * 文件上传模型
 */
@JsonClass(generateAdapter = true)
data class FileUpload(
    @Json(name = "type") val type: String, // document, image, audio, video, custom
    @Json(name = "transfer_method") val transferMethod: String, // remote_url, local_file
    @Json(name = "url") val url: String? = null,
    @Json(name = "upload_file_id") val uploadFileId: String? = null
)

/**
 * 聊天完成响应模型（阻塞模式）
 */
@Serializable
@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @SerialName("event") @Json(name = "event") val event: String,
    @SerialName("task_id") @Json(name = "task_id") val taskId: String,
    @SerialName("id") @Json(name = "id") val id: String,
    @SerialName("message_id") @Json(name = "message_id") val messageId: String,
    @SerialName("conversation_id") @Json(name = "conversation_id") val conversationId: String,
    @SerialName("mode") @Json(name = "mode") val mode: String,
    @SerialName("answer") @Json(name = "answer") val answer: String,
    @SerialName("metadata") @Json(name = "metadata") val metadata: ResponseMetadata,
    @SerialName("created_at") @Json(name = "created_at") val createdAt: Long
)

/**
 * 流式聊天响应模型
 */
@Serializable
@JsonClass(generateAdapter = true)
data class StreamChatResponse(
    @SerialName("event") @Json(name = "event") val event: String,
    @SerialName("task_id") @Json(name = "task_id") val taskId: String? = null,
    @SerialName("message_id") @Json(name = "message_id") val messageId: String? = null,
    @SerialName("conversation_id") @Json(name = "conversation_id") val conversationId: String? = null,
    @SerialName("answer") @Json(name = "answer") val answer: String? = null,
    @SerialName("created_at") @Json(name = "created_at") val createdAt: Long? = null,
    @SerialName("metadata") @Json(name = "metadata") val metadata: ResponseMetadata? = null,
    @SerialName("workflow_run_id") @Json(name = "workflow_run_id") val workflowRunId: String? = null,
    @SerialName("node_id") @Json(name = "node_id") val nodeId: String? = null,
    @SerialName("node_type") @Json(name = "node_type") val nodeType: String? = null,
    @SerialName("title") @Json(name = "title") val title: String? = null,
    @SerialName("index") @Json(name = "index") val index: Int? = null,
    @SerialName("status") @Json(name = "status") val status: String? = null,
    @SerialName("outputs") @Json(name = "outputs") val outputs: Map<String, JsonElement>? = null,
    @SerialName("elapsed_time") @Json(name = "elapsed_time") val elapsedTime: Double? = null,
    @SerialName("total_steps") @Json(name = "total_steps") val totalSteps: Int? = null,
    @SerialName("finished_at") @Json(name = "finished_at") val finishedAt: Long? = null,
    @SerialName("code") @Json(name = "code") val code: String? = null,
    @SerialName("message") @Json(name = "message") val message: String? = null
)

/**
 * 响应元数据
 */
@Serializable
@JsonClass(generateAdapter = true)
data class ResponseMetadata(
    @SerialName("usage") @Json(name = "usage") val usage: TokenUsage? = null,
    @SerialName("retriever_resources") @Json(name = "retriever_resources") val retrieverResources: List<RetrieverResource>? = null
)

/**
 * Token 使用情况
 */
@Serializable
@JsonClass(generateAdapter = true)
data class TokenUsage(
    @SerialName("prompt_tokens") @Json(name = "prompt_tokens") val promptTokens: Int,
    @SerialName("prompt_unit_price") @Json(name = "prompt_unit_price") val promptUnitPrice: String,
    @SerialName("prompt_price_unit") @Json(name = "prompt_price_unit") val promptPriceUnit: String,
    @SerialName("completion_tokens") @Json(name = "completion_tokens") val completionTokens: Int,
    @SerialName("completion_unit_price") @Json(name = "completion_unit_price") val completionUnitPrice: String,
    @SerialName("completion_price_unit") @Json(name = "completion_price_unit") val completionPriceUnit: String,
    @SerialName("total_tokens") @Json(name = "total_tokens") val totalTokens: Int,
    @SerialName("total_price") @Json(name = "total_price") val totalPrice: String,
    @SerialName("currency") @Json(name = "currency") val currency: String,
    @SerialName("latency") @Json(name = "latency") val latency: Double
)

/**
 * 检索资源
 */
@Serializable
@JsonClass(generateAdapter = true)
data class RetrieverResource(
    @SerialName("position") @Json(name = "position") val position: Int,
    @SerialName("dataset_id") @Json(name = "dataset_id") val datasetId: String,
    @SerialName("dataset_name") @Json(name = "dataset_name") val datasetName: String,
    @SerialName("document_id") @Json(name = "document_id") val documentId: String,
    @SerialName("document_name") @Json(name = "document_name") val documentName: String,
    @SerialName("segment_id") @Json(name = "segment_id") val segmentId: String,
    @SerialName("score") @Json(name = "score") val score: Double,
    @SerialName("content") @Json(name = "content") val content: String
)

/**
 * 历史消息响应
 */
@JsonClass(generateAdapter = true)
data class MessagesResponse(
    @Json(name = "limit") val limit: Int,
    @Json(name = "has_more") val hasMore: Boolean,
    @Json(name = "data") val data: List<MessageItem>
)

/**
 * 消息项目
 */
@JsonClass(generateAdapter = true)
data class MessageItem(
    @Json(name = "id") val id: String,
    @Json(name = "conversation_id") val conversationId: String,
    @Json(name = "inputs") val inputs: Map<String, Any>,
    @Json(name = "query") val query: String,
    @Json(name = "answer") val answer: String,
    @Json(name = "message_files") val messageFiles: List<MessageFile>? = null,
    @Json(name = "feedback") val feedback: MessageFeedback? = null,
    @Json(name = "retriever_resources") val retrieverResources: List<RetrieverResource>? = null,
    @Json(name = "created_at") val createdAt: Long
)

/**
 * 消息文件
 */
@JsonClass(generateAdapter = true)
data class MessageFile(
    @Json(name = "type") val type: String,
    @Json(name = "url") val url: String,
    @Json(name = "belongs_to") val belongsTo: String
)

/**
 * 消息反馈
 */
@JsonClass(generateAdapter = true)
data class MessageFeedback(
    @Json(name = "rating") val rating: String? = null
)

/**
 * 错误响应
 */
@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "code") val code: String,
    @Json(name = "message") val message: String,
    @Json(name = "status") val status: Int
)

/**
 * Chat message request alias for compatibility
 */
typealias ChatMessageRequest = ChatRequest

/**
 * Conversations response alias for compatibility
 */
typealias ConversationsResponse = ConversationListResponse 