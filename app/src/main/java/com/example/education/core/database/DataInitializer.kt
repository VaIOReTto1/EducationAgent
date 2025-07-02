package com.example.education.core.database

import android.util.Log
import com.example.education.core.database.dao.*
import com.example.education.core.database.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据初始化器
 * 
 * 用于初始化应用基础数据，包括默认课程、章节等
 */
@Singleton
class DataInitializer @Inject constructor(
    private val userDao: UserDao,
    private val courseDao: CourseDao,
    private val chapterDao: ChapterDao
) {
    
    companion object {
        private const val TAG = "DataInitializer"
    }
    
    // 嵌入式系统课程内容
    private val embeddedSystemContent = """
前 言

嵌入式系统是为特定应用而设计的专用计算机系统，已经广泛应用于智能手机、数码产品、工业控制、通信和信息系统、军事、航空航天、医疗电子等领域，整个社会对嵌入式系统的开发和应用人才的需求也不断加大。嵌入式 Linux 是以 Linux 为基础的嵌入式操作系统，因为其具有代码开源、性能优异、资源众多等优点，在嵌入式领域广为使用。

为了进一步加强嵌入式 Linux 的实践教学工作，适应高等学校正在开展的课程体系与教学内容的改革，及时反映嵌入式系统教学的研究成果，积极探索适应 21 世纪人才培养的教学模式，编者编写了本书。

本书具有如下特色：

1）入门简单，本书内容安排深浅适宜，实践操作讲解详细，大部分内容只要求有基本的计算机基础知识和程序设计基础即可开始上手。

2）内容涵盖范围广，本书实践内容围绕嵌入式 Linux 开发的应用编程展开，内容涵盖 Linux 操作系统介绍、安装和基本使用，嵌入式 Linux 开发平台，Bootloader 移植，驱动应用以及嵌入式 Linux 的应用开发，通过简单经典的实践操作引导读者走进嵌入式的大门。

3）硬件实践的目标平台为广州友善之臂计算机科技有限公司的 Mini2451 开发板，Mini2451 是国内广为使用且资源众多的 Mini2440 开发板继承者，性价比高，极大地降低了嵌入式技术的自学入门费用。

4）本书注重将嵌入式 Linux 技术的最新发展适当地引入到教学中，保证了教学内容的先进性。此外，本书源于高校嵌入式课程的实践教学，凝聚了工作在第一线的任课教师多年的教学经验与教学成果。
    """.trimIndent()
    
    /**
     * 初始化应用基础数据
     */
    suspend fun initializeData() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始初始化应用基础数据")
            
            // 检查是否已经初始化过
            val existingCourse = courseDao.getCourseById("course_embedded_system")
            if (existingCourse != null) {
                Log.d(TAG, "数据已初始化，跳过")
                return@withContext
            }
            
            // 初始化默认用户
            initializeUsers()
            
            // 初始化默认课程
            initializeCourses()
            
            // 初始化默认章节
            initializeChapters()
            
            Log.d(TAG, "应用基础数据初始化完成")
            
        } catch (e: Exception) {
            Log.e(TAG, "初始化数据失败", e)
        }
    }
    
    /**
     * 初始化默认用户
     */
    private suspend fun initializeUsers() {
        val currentTime = System.currentTimeMillis()
        val defaultUsers = listOf(
            UserEntity(
                id = "user_001",
                username = "teacher_zhang",
                displayName = "张老师",
                email = "zhang.teacher@example.com",
                role = "TEACHER",
                avatarUrl = null,
                institution = "计算机学院",
                grade = "10年教学经验",
                isActive = true,
                preferences = """{"theme": "auto", "language": "zh-CN"}""",
                createdAt = currentTime,
                lastLoginAt = currentTime,
                updatedAt = currentTime
            ),
            UserEntity(
                id = "user_002", 
                username = "student_li",
                displayName = "李同学",
                email = "li.student@example.com",
                role = "STUDENT",
                avatarUrl = null,
                institution = "计算机学院",
                grade = "大三",
                isActive = true,
                preferences = """{"theme": "auto", "language": "zh-CN"}""",
                createdAt = currentTime,
                lastLoginAt = currentTime,
                updatedAt = currentTime
            )
        )
        
        for (user in defaultUsers) {
            userDao.insertUser(user)
            Log.d(TAG, "插入用户: ${user.displayName}")
        }
    }
    
    /**
     * 初始化默认课程
     */
    private suspend fun initializeCourses() {
        val defaultCourses = listOf(
            CourseEntity(
                id = "course_embedded_system",
                title = "嵌入式Linux系统开发",
                description = "本课程全面介绍嵌入式Linux系统的开发理论与实践，涵盖从基础知识到高级应用的全过程。",
                teacherId = "user_001",
                subject = "计算机科学",
                gradeLevel = "本科",
                isPublished = true,
                coverImageUrl = null,
                difficultyLevel = 3,
                estimatedHours = 48,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
        
        for (course in defaultCourses) {
            courseDao.insertCourse(course)
            Log.d(TAG, "插入课程: ${course.title}")
        }
    }
    
    /**
     * 初始化默认章节
     */
    private suspend fun initializeChapters() {
        val defaultChapters = listOf(
            ChapterEntity(
                id = "chapter_001",
                courseId = "course_embedded_system",
                title = "前言 - 嵌入式系统概述",
                description = "介绍嵌入式系统的基本概念、应用领域和发展趋势，以及本书的特色和学习目标。",
                chapterOrder = 1,
                content = embeddedSystemContent,
                durationMinutes = 45,
                isPublished = true,
                isFree = true,
                prerequisites = null,
                learningObjectives = """["了解嵌入式系统的定义和特点", "掌握嵌入式Linux的优势", "明确课程学习目标和方法"]""",
                resources = """[
                    {
                        "title": "嵌入式系统发展历史",
                        "type": "article",
                        "url": "https://example.com/embedded-history"
                    },
                    {
                        "title": "Linux内核架构简介",
                        "type": "video", 
                        "url": "https://example.com/linux-kernel-intro"
                    }
                ]""",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
        
        for (chapter in defaultChapters) {
            chapterDao.insertChapter(chapter)
            Log.d(TAG, "插入章节: ${chapter.title}")
        }
    }
} 