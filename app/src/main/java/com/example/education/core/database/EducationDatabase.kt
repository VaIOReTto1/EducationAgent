package com.example.education.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.education.core.database.dao.*
import com.example.education.core.database.entity.*

/**
 * 教育应用主数据库
 * 包含所有实体和数据访问对象
 */
@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        ChapterEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        AssessmentEntity::class,
        LearningProgressEntity::class,
        AssessmentResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class EducationDatabase : RoomDatabase() {

    /**
     * 用户数据访问对象
     */
    abstract fun userDao(): UserDao

    /**
     * 课程数据访问对象
     */
    abstract fun courseDao(): CourseDao

    /**
     * 章节数据访问对象
     */
    abstract fun chapterDao(): ChapterDao

    /**
     * 对话数据访问对象
     */
    abstract fun conversationDao(): ConversationDao

    /**
     * 消息数据访问对象
     */
    abstract fun messageDao(): MessageDao

    /**
     * 评估数据访问对象
     */
    abstract fun assessmentDao(): AssessmentDao

    /**
     * 学习进度数据访问对象
     */
    abstract fun learningProgressDao(): LearningProgressDao

    /**
     * 评估结果数据访问对象
     */
    abstract fun assessmentResultDao(): AssessmentResultDao
}