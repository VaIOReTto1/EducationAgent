package com.example.education.agent

import com.example.education.core.network.service.DifyApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 智能体模块 - 提供各种教育智能体服务
 */
@Module
@InstallIn(SingletonComponent::class)
object AgentModule {

    /**
     * 提供Dify API服务
     */
    @Provides
    @Singleton
    fun provideDifyApiService(retrofit: Retrofit): DifyApiService {
        return retrofit.create(DifyApiService::class.java)
    }

    /**
     * 提供知识库管理智能体
     */
    @Provides
    @Singleton
    fun provideKnowledgeBaseAgent(apiService: DifyApiService): KnowledgeBaseAgent {
        return KnowledgeBaseAgent(apiService)
    }

    /**
     * 提供教学辅导智能体
     */
    @Provides
    @Singleton
    fun provideTutoringAgent(apiService: DifyApiService): TutoringAgent {
        return TutoringAgent(apiService)
    }

    /**
     * 提供评估智能体
     */
    @Provides
    @Singleton
    fun provideAssessmentAgent(apiService: DifyApiService): AssessmentAgent {
        return AssessmentAgent(apiService)
    }

    /**
     * 提供学生端智能体
     */
    @Provides
    @Singleton
    fun provideStudentAgent(apiService: DifyApiService): StudentAgent {
        return StudentAgent(apiService)
    }

    /**
     * 提供教师端智能体
     */
    @Provides
    @Singleton
    fun provideTeacherAgent(apiService: DifyApiService): TeacherAgent {
        return TeacherAgent(apiService)
    }
}