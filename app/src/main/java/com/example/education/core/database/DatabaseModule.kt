package com.example.education.core.database

import android.content.Context
import androidx.room.Room
import com.example.education.core.database.dao.AssessmentDao
import com.example.education.core.database.dao.ChapterDao
import com.example.education.core.database.dao.ConversationDao
import com.example.education.core.database.dao.CourseDao
import com.example.education.core.database.dao.LearningProgressDao
import com.example.education.core.database.dao.MessageDao
import com.example.education.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库模块 - 提供Room数据库相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供教育数据库实例
     */
    @Provides
    @Singleton
    fun provideEducationDatabase(
        @ApplicationContext context: Context
    ): EducationDatabase {
        return Room.databaseBuilder(
            context,
            EducationDatabase::class.java,
            "education_database"
        )
            .fallbackToDestructiveMigration() // 开发阶段使用，生产环境需要提供迁移策略
            .build()
    }

    /**
     * 提供用户DAO
     */
    @Provides
    fun provideUserDao(database: EducationDatabase): UserDao {
        return database.userDao()
    }

    /**
     * 提供课程DAO
     */
    @Provides
    fun provideCourseDao(database: EducationDatabase): CourseDao {
        return database.courseDao()
    }

    /**
     * 提供章节DAO
     */
    @Provides
    fun provideChapterDao(database: EducationDatabase): ChapterDao {
        return database.chapterDao()
    }

    /**
     * 提供对话DAO
     */
    @Provides
    fun provideConversationDao(database: EducationDatabase): ConversationDao {
        return database.conversationDao()
    }

    /**
     * 提供消息DAO
     */
    @Provides
    fun provideMessageDao(database: EducationDatabase): MessageDao {
        return database.messageDao()
    }

    /**
     * 提供评估DAO
     */
    @Provides
    fun provideAssessmentDao(database: EducationDatabase): AssessmentDao {
        return database.assessmentDao()
    }

    /**
     * 提供学习进度DAO
     */
    @Provides
    fun provideLearningProgressDao(database: EducationDatabase): LearningProgressDao {
        return database.learningProgressDao()
    }
}