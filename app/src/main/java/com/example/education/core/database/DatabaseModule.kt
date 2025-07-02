package com.example.education.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.education.core.database.dao.*
import javax.inject.Singleton

/**
 * 数据库模块 - Hilt依赖注入配置
 * 简化版本，只包含核心DAO
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEducationDatabase(
        @ApplicationContext context: Context
    ): EducationDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            EducationDatabase::class.java,
            EducationDatabase.DATABASE_NAME
        )
            // 移除FTS5回调，因为Android SQLite可能不支持
            // 后续如需全文搜索，可考虑使用Room的@Fts4注解或其他方案
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: EducationDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideConversationDao(database: EducationDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: EducationDatabase): MessageDao {
        return database.messageDao()
    }
    
    @Provides
    @Singleton
    fun provideCourseDao(database: EducationDatabase): CourseDao {
        return database.courseDao()
    }
    
    @Provides
    @Singleton
    fun provideChapterDao(database: EducationDatabase): ChapterDao {
        return database.chapterDao()
    }
    
    @Provides
    @Singleton
    fun provideLearningProgressDao(database: EducationDatabase): LearningProgressDao {
        return database.learningProgressDao()
    }
    
    @Provides
    @Singleton
    fun provideAssignmentDao(database: EducationDatabase): AssignmentDao {
        return database.assignmentDao()
    }
    
    @Provides
    @Singleton
    fun provideSubmissionDao(database: EducationDatabase): SubmissionDao {
        return database.submissionDao()
    }
} 