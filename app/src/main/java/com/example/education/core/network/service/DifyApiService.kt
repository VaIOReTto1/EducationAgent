package com.example.education.core.network.service

import com.example.education.core.network.model.ChatMessageRequest
import com.example.education.core.network.model.ChatMessageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * Dify API服务接口
 * 定义与Dify平台的所有API交互
 */
interface DifyApiService {

    /**
     * 发送聊天消息（阻塞模式）
     * @param request 聊天消息请求
     * @return 聊天消息响应
     */
    @POST("chat-messages")
    suspend fun sendChatMessage(
        @Body request: ChatMessageRequest
    ): Response<ChatMessageResponse>

    /**
     * 发送聊天消息（流式模式）
     * @param request 聊天消息请求
     * @return 流式响应体
     */
    @POST("chat-messages")
    @Streaming
    suspend fun sendChatMessageStream(
        @Body request: ChatMessageRequest
    ): Response<ResponseBody>
}