package com.example.education.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 聊天消息请求模型
 */
@Serializable
data class ChatMessageRequest(
    @SerialName("query")
    val query: String,
    @SerialName("inputs")
    val inputs: Map<String, String> = emptyMap(),
    @SerialName("response_mode")
    val responseMode: String = "streaming",
    @SerialName("user")
    val user: String,
    @SerialName("conversation_id")
    val conversationId: String? = null,
    @SerialName("files")
    val files: List<FileInput> = emptyList(),
    @SerialName("auto_generate_name")
    val autoGenerateName: Boolean = true
)

/**
 * 文件输入模型
 */
@Serializable
data class FileInput(
    @SerialName("type")
    val type: String, // document, image, audio, video, custom
    @SerialName("transfer_method")
    val transferMethod: String, // remote_url, local_file
    @SerialName("url")
    val url: String? = null,
    @SerialName("upload_file_id")
    val uploadFileId: String? = null
)

/**
 * 聊天消息响应模型（阻塞模式）
 */
@Serializable
data class ChatMessageResponse(
    @SerialName("event")
    val event: String,
    @SerialName("task_id")
    val taskId: String,
    @SerialName("id")
    val id: String,
    @SerialName("message_id")
    val messageId: String,
    @SerialName("conversation_id")
    val conversationId: String,
    @SerialName("mode")
    val mode: String,
    @SerialName("answer")
    val answer: String,
    @SerialName("metadata")
    val metadata: MessageMetadata,
    @SerialName("created_at")
    val createdAt: Long
)

/**
 * 流式聊天消息响应模型
 */
@Serializable
data class StreamChatMessageResponse(
    @SerialName("event")
    val event: String,
    @SerialName("task_id")
    val taskId: String? = null,
    @SerialName("message_id")
    val messageId: String? = null,
    @SerialName("conversation_id")
    val conversationId: String? = null,
    @SerialName("answer")
    val answer: String? = null,
    @SerialName("metadata")
    val metadata: MessageMetadata? = null,
    @SerialName("created_at")
    val createdAt: Long? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("message")
    val message: String? = null
)

/**
 * 消息元数据
 */
@Serializable
data class MessageMetadata(
    @SerialName("usage")
    val usage: Usage? = null,
    @SerialName("retriever_resources")
    val retrieverResources: List<RetrieverResource> = emptyList()
)

/**
 * 使用量统计
 */
@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

/**
 * 检索资源
 */
@Serializable
data class RetrieverResource(
    @SerialName("position")
    val position: Int,
    @SerialName("dataset_id")
    val datasetId: String,
    @SerialName("dataset_name")
    val datasetName: String,
    @SerialName("document_id")
    val documentId: String,
    @SerialName("document_name")
    val documentName: String,
    @SerialName("segment_id")
    val segmentId: String,
    @SerialName("score")
    val score: Double,
    @SerialName("content")
    val content: String
)

/**
 * 错误响应模型
 */
@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int
)