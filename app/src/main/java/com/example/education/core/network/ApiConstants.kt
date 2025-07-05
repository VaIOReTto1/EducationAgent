package com.example.education.core.network

/**
 * API 相关常量配置
 * 
 * 用于配置 Dify 工作流编排对话型应用 API 的基础参数
 */
object ApiConstants {
    
    // Dify API 基础配置
    const val BASE_URL = "https://api.dify.ai/v1/"
    
    // 多智能体 API Key 配置
    object ApiKeys {
        const val STUDENT = "app-mTevUPVC20OFXKn4HRvea1GV"           // 智能体学生端
        const val KNOWLEDGE_BASE = "app-4EKbCtVu8kl7ma0BS1mRuv3R"      // 知识库管理
        const val TUTORING = "app-UOktKFCXqIg1Em9Llu8mvfvD"          // 辅导端
        const val ASSESSMENT = "app-45d3YaGnQh0MLZcxGanotNLa"         // 评估端
        const val TEACHER = "app-56XMBM9poUyIyfAKnIXvi459"           // 教师端
        
        /**
         * 根据智能体类型获取对应的API Key
         */
        fun getApiKey(agentType: String): String {
            return when (agentType) {
                AgentRoles.STUDENT -> STUDENT
                AgentRoles.KNOWLEDGE_BASE -> KNOWLEDGE_BASE
                AgentRoles.TUTORING -> TUTORING
                AgentRoles.ASSESSMENT -> ASSESSMENT
                AgentRoles.TEACHER -> TEACHER
                else -> STUDENT // 默认使用学生端API Key
            }
        }
    }
    
    // API 端点路径
    object Endpoints {
        const val CHAT_MESSAGES = "chat-messages"
        const val MESSAGES = "messages"
        const val CONVERSATIONS = "conversations"
        const val CONVERSATIONS_NAME = "conversations/{conversation_id}/name"
        const val CONVERSATIONS_VARIABLES = "conversations/{conversation_id}/variables"
    }
    
    // 响应模式
    object ResponseMode {
        const val STREAMING = "streaming"
        const val BLOCKING = "blocking"
    }
    
    // 智能体角色映射
    object AgentRoles {
        const val KNOWLEDGE_BASE = "knowledge_base"  // 知识库管理智能体
        const val TUTORING = "tutoring"             // 辅导智能体
        const val ASSESSMENT = "assessment"         // 评估智能体  
        const val STUDENT = "student"               // 学生端智能体
        const val TEACHER = "teacher"               // 教师端智能体
    }
    
    // 会话ID格式: {userId}_{role}
    object ConversationIdFormat {
        const val TEACHER_PREFIX = "teacher"
        const val STUDENT_PREFIX = "student"
        const val SEPARATOR = "_"
        
        fun generateConversationId(userId: String, role: String): String {
            return "${userId}${SEPARATOR}${role}"
        }
    }
    
    // 网络配置
    object NetworkConfig {
        const val CONNECT_TIMEOUT = 30L // 秒
        const val READ_TIMEOUT = 60L    // 秒  
        const val WRITE_TIMEOUT = 60L   // 秒
        const val CACHE_SIZE = 50L * 1024 * 1024 // 50MB
    }
} 