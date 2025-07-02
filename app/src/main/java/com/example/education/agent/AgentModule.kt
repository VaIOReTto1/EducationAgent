package com.example.education.agent

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 智能体模块的依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AgentModule {
    
    @Binds
    @Singleton
    abstract fun bindAgentRepository(
        agentRepositoryImpl: AgentRepositoryImpl
    ): AgentRepository
} 