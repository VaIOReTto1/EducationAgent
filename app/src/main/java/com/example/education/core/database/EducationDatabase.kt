package com.example.education.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.education.core.database.dao.*
import com.example.education.core.database.entities.*

/**
 * 教育应用主数据库
 * 
 * 包含所有实体，支持离线优先架构
 */
@Database(
    entities = [
        UserEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        CourseEntity::class,
        ChapterEntity::class,
        LearningProgressEntity::class,
        AssignmentEntity::class,
        SubmissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class EducationDatabase : RoomDatabase() {
    
    // DAO 抽象方法
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun courseDao(): CourseDao
    abstract fun chapterDao(): ChapterDao
    abstract fun learningProgressDao(): LearningProgressDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun submissionDao(): SubmissionDao
    
    companion object {
        const val DATABASE_NAME = "education_database"
        
        @Volatile
        private var INSTANCE: EducationDatabase? = null
        
        fun getDatabase(context: Context): EducationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EducationDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 