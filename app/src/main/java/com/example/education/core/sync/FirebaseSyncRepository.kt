package com.example.education.core.sync

import android.util.Log
import com.google.firebase.database.*
import com.example.education.core.database.entities.*
import com.example.education.core.database.dao.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase实时同步仓库
 * 
 * 实现本地数据库与Firebase Realtime Database的双向同步
 */
@Singleton
class FirebaseSyncRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val userDao: UserDao,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val courseDao: CourseDao,
    private val learningProgressDao: LearningProgressDao
) {
    
    companion object {
        private const val TAG = "FirebaseSync"
        private const val USERS_PATH = "users"
        private const val CONVERSATIONS_PATH = "conversations"
        private const val MESSAGES_PATH = "messages"
        private const val COURSES_PATH = "courses"
        private const val LEARNING_PROGRESS_PATH = "learning_progress"
    }
    
    /**
     * 解析时间字符串为LocalDateTime
     */
    private fun parseDateTime(dateTimeString: String?): LocalDateTime {
        if (dateTimeString == null) return LocalDateTime.now()
        return try {
            LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
    
    /**
     * 同步用户数据到Firebase
     */
    suspend fun syncUserToFirebase(user: UserEntity) {
        try {
            Log.d(TAG, "同步用户数据到Firebase: ${user.id}")
            
            val userRef = database.getReference("$USERS_PATH/${user.id}")
            val userMap = mapOf(
                "id" to user.id,
                "username" to user.username,
                "displayName" to user.displayName,
                "email" to user.email,
                "role" to user.role,
                "avatarUrl" to user.avatarUrl,
                "institution" to user.institution,
                "grade" to user.grade,
                "preferences" to user.preferences,
                "isActive" to user.isActive,
                "createdAt" to user.createdAt,
                "lastLoginAt" to user.lastLoginAt,
                "updatedAt" to user.updatedAt
            )
            
            userRef.setValue(userMap).await()
            Log.d(TAG, "用户数据同步成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "同步用户数据失败", e)
            throw e
        }
    }
    
    /**
     * 从Firebase同步用户数据
     */
    fun syncUsersFromFirebase(): Flow<List<UserEntity>> = callbackFlow {
        Log.d(TAG, "开始监听Firebase用户数据变化")
        
        val usersRef = database.getReference(USERS_PATH)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val users = mutableListOf<UserEntity>()
                    for (userSnapshot in snapshot.children) {
                        val userData = userSnapshot.value as? Map<String, Any> ?: continue
                        
                        val user = UserEntity(
                            id = userData["id"] as? String ?: "",
                            username = userData["username"] as? String ?: "",
                            displayName = userData["displayName"] as? String ?: "",
                            email = userData["email"] as? String ?: "",
                            role = userData["role"] as? String ?: "student",
                            avatarUrl = userData["avatarUrl"] as? String,
                            institution = userData["institution"] as? String,
                            grade = userData["grade"] as? String,
                            preferences = userData["preferences"] as? String,
                            isActive = userData["isActive"] as? Boolean ?: true,
                            createdAt = (userData["createdAt"] as? Number)?.toLong() ?: 0L,
                            lastLoginAt = (userData["lastLoginAt"] as? Number)?.toLong() ?: 0L,
                            updatedAt = (userData["updatedAt"] as? Number)?.toLong() ?: 0L
                        )
                        users.add(user)
                    }
                    
                    trySend(users)
                    Log.d(TAG, "接收到${users.size}个用户数据更新")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "处理用户数据失败", e)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase用户数据监听取消", error.toException())
                close(error.toException())
            }
        }
        
        usersRef.addValueEventListener(listener)
        
        awaitClose {
            usersRef.removeEventListener(listener)
            Log.d(TAG, "停止监听Firebase用户数据变化")
        }
    }
    
    /**
     * 同步消息到Firebase
     */
    suspend fun syncMessageToFirebase(message: MessageEntity) {
        try {
            Log.d(TAG, "同步消息到Firebase: ${message.id}")
            
            val messageRef = database.getReference("$MESSAGES_PATH/${message.conversationId}/${message.id}")
            val messageMap = mapOf(
                "id" to message.id,
                "conversationId" to message.conversationId,
                "content" to message.content,
                "isFromUser" to message.isFromUser,
                "messageType" to message.messageType,
                "metadata" to message.metadata,
                "tokenUsage" to message.tokenUsage,
                "isSynced" to true,
                "createdAt" to message.createdAt
            )
            
            messageRef.setValue(messageMap).await()
            
            // 更新本地同步状态
            messageDao.updateSyncStatus(message.id, true)
            
            Log.d(TAG, "消息同步成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "同步消息失败", e)
            throw e
        }
    }
    
    /**
     * 从Firebase监听特定会话的消息
     */
    fun listenToConversationMessages(conversationId: String): Flow<List<MessageEntity>> = callbackFlow {
        Log.d(TAG, "开始监听会话消息: $conversationId")
        
        val messagesRef = database.getReference("$MESSAGES_PATH/$conversationId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val messages = mutableListOf<MessageEntity>()
                    for (messageSnapshot in snapshot.children) {
                        val messageData = messageSnapshot.value as? Map<String, Any> ?: continue
                        
                        val message = MessageEntity(
                            id = messageData["id"] as? String ?: "",
                            conversationId = messageData["conversationId"] as? String ?: "",
                            content = messageData["content"] as? String ?: "",
                            isFromUser = messageData["isFromUser"] as? Boolean ?: false,
                            messageType = messageData["messageType"] as? String ?: "text",
                            metadata = messageData["metadata"] as? String,
                            tokenUsage = messageData["tokenUsage"] as? String,
                            isSynced = messageData["isSynced"] as? Boolean ?: false,
                            createdAt = (messageData["createdAt"] as? Number)?.toLong() ?: 0L
                        )
                        messages.add(message)
                    }
                    
                    // 按时间排序
                    messages.sortBy { it.createdAt }
                    trySend(messages)
                    Log.d(TAG, "接收到${messages.size}条消息更新")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "处理消息数据失败", e)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase消息监听取消", error.toException())
                close(error.toException())
            }
        }
        
        messagesRef.addValueEventListener(listener)
        
        awaitClose {
            messagesRef.removeEventListener(listener)
            Log.d(TAG, "停止监听会话消息: $conversationId")
        }
    }
    
    /**
     * 同步学习进度到Firebase
     */
    suspend fun syncLearningProgressToFirebase(progress: LearningProgressEntity) {
        try {
            Log.d(TAG, "同步学习进度到Firebase: ${progress.userId}_${progress.chapterId}")
            
            val progressRef = database.getReference("$LEARNING_PROGRESS_PATH/${progress.userId}/${progress.chapterId}")
            val progressMap = mapOf(
                "userId" to progress.userId,
                "courseId" to progress.courseId,
                "chapterId" to progress.chapterId,
                "progressPercent" to progress.progressPercent,
                "timeSpent" to progress.timeSpent,
                "isCompleted" to progress.isCompleted,
                "lastAccessAt" to progress.lastAccessAt,
                "completedAt" to progress.completedAt
            )
            
            progressRef.setValue(progressMap).await()
            Log.d(TAG, "学习进度同步成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "同步学习进度失败", e)
            throw e
        }
    }
    
    /**
     * 监听用户学习进度变化
     */
    fun listenToUserProgress(userId: String): Flow<List<LearningProgressEntity>> = callbackFlow {
        Log.d(TAG, "开始监听用户学习进度: $userId")
        
        val progressRef = database.getReference("$LEARNING_PROGRESS_PATH/$userId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val progressList = mutableListOf<LearningProgressEntity>()
                    for (progressSnapshot in snapshot.children) {
                        val progressData = progressSnapshot.value as? Map<String, Any> ?: continue
                        
                        val progress = LearningProgressEntity(
                            userId = progressData["userId"] as? String ?: "",
                            courseId = progressData["courseId"] as? String ?: "",
                            chapterId = progressData["chapterId"] as? String ?: "",
                            progressPercent = (progressData["progressPercent"] as? Number)?.toFloat() ?: 0f,
                            timeSpent = (progressData["timeSpent"] as? Number)?.toLong() ?: 0L,
                            isCompleted = progressData["isCompleted"] as? Boolean ?: false,
                            lastAccessAt = (progressData["lastAccessAt"] as? Number)?.toLong() ?: 0L,
                            completedAt = (progressData["completedAt"] as? Number)?.toLong()
                        )
                        progressList.add(progress)
                    }
                    
                    trySend(progressList)
                    Log.d(TAG, "接收到${progressList.size}条学习进度更新")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "处理学习进度数据失败", e)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase学习进度监听取消", error.toException())
                close(error.toException())
            }
        }
        
        progressRef.addValueEventListener(listener)
        
        awaitClose {
            progressRef.removeEventListener(listener)
            Log.d(TAG, "停止监听用户学习进度: $userId")
        }
    }
    
    /**
     * 同步未同步的本地数据到Firebase
     */
    suspend fun syncPendingData() {
        try {
            Log.d(TAG, "开始同步待同步数据")
            
            // 同步未同步的消息
            val unsyncedMessages = messageDao.getUnsyncedMessages()
            for (message in unsyncedMessages) {
                syncMessageToFirebase(message)
            }
            
            Log.d(TAG, "同步了${unsyncedMessages.size}条未同步消息")
            
        } catch (e: Exception) {
            Log.e(TAG, "同步待同步数据失败", e)
        }
    }
    
    /**
     * 检查Firebase连接状态
     */
    fun isFirebaseConnected(): Flow<Boolean> = callbackFlow {
        val connectedRef = database.getReference(".info/connected")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                trySend(connected)
                Log.d(TAG, "Firebase连接状态: $connected")
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase连接状态监听取消", error.toException())
                close(error.toException())
            }
        }
        
        connectedRef.addValueEventListener(listener)
        
        awaitClose {
            connectedRef.removeEventListener(listener)
        }
    }
    
    /**
     * 同步课程到Firebase
     */
    suspend fun syncCourseToFirebase(course: CourseEntity) {
        try {
            Log.d(TAG, "同步课程到Firebase: ${course.id}")
            
            val courseRef = database.getReference("$COURSES_PATH/${course.id}")
            val courseMap = mapOf(
                "id" to course.id,
                "title" to course.title,
                "description" to course.description,
                "teacherId" to course.teacherId,
                "subject" to course.subject,
                "gradeLevel" to course.gradeLevel,
                "isPublished" to course.isPublished,
                "coverImageUrl" to course.coverImageUrl,
                "difficultyLevel" to course.difficultyLevel,
                "estimatedHours" to course.estimatedHours,
                "createdAt" to course.createdAt.toString(),
                "updatedAt" to course.updatedAt.toString()
            )
            
            courseRef.setValue(courseMap).await()
            Log.d(TAG, "课程同步成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "同步课程失败", e)
            throw e
        }
    }
    
    /**
     * 同步章节到Firebase
     */
    suspend fun syncChapterToFirebase(chapter: ChapterEntity) {
        try {
            Log.d(TAG, "同步章节到Firebase: ${chapter.id}")
            
            val chapterRef = database.getReference("chapters/${chapter.courseId}/${chapter.id}")
            val chapterMap = mapOf(
                "id" to chapter.id,
                "courseId" to chapter.courseId,
                "title" to chapter.title,
                "content" to chapter.content,
                "orderIndex" to chapter.chapterOrder,
                "duration" to chapter.durationMinutes,
                "isPublished" to chapter.isPublished,
                "createdAt" to chapter.createdAt,
                "updatedAt" to chapter.updatedAt
            )
            
            chapterRef.setValue(chapterMap).await()
            Log.d(TAG, "章节同步成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "同步章节失败", e)
            throw e
        }
    }
    
    /**
     * 从Firebase删除课程
     */
    suspend fun deleteCourseFromFirebase(courseId: String) {
        try {
            Log.d(TAG, "从Firebase删除课程: $courseId")
            
            val courseRef = database.getReference("$COURSES_PATH/$courseId")
            courseRef.removeValue().await()
            
            // 同时删除相关章节
            val chaptersRef = database.getReference("chapters/$courseId")
            chaptersRef.removeValue().await()
            
            Log.d(TAG, "课程删除成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "删除课程失败", e)
            throw e
        }
    }
    
    /**
     * 从Firebase删除用户
     */
    suspend fun deleteUserFromFirebase(userId: String) {
        try {
            Log.d(TAG, "从Firebase删除用户: $userId")
            
            val userRef = database.getReference("$USERS_PATH/$userId")
            userRef.removeValue().await()
            
            // 删除用户相关的学习进度
            val progressRef = database.getReference("$LEARNING_PROGRESS_PATH/$userId")
            progressRef.removeValue().await()
            
            Log.d(TAG, "用户删除成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "删除用户失败", e)
            throw e
        }
    }
    
    /**
     * 从Firebase删除学习进度
     */
    suspend fun deleteLearningProgressFromFirebase(userId: String, chapterId: String) {
        try {
            Log.d(TAG, "从Firebase删除学习进度: $userId - $chapterId")
            
            val progressRef = database.getReference("$LEARNING_PROGRESS_PATH/$userId/$chapterId")
            progressRef.removeValue().await()
            
            Log.d(TAG, "学习进度删除成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "删除学习进度失败", e)
            throw e
        }
    }
    
    /**
     * 从Firebase监听课程变化
     */
    fun listenToCoursesChanges(): Flow<List<CourseEntity>> = callbackFlow {
        Log.d(TAG, "开始监听Firebase课程数据变化")
        
        val coursesRef = database.getReference(COURSES_PATH)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val courses = mutableListOf<CourseEntity>()
                    for (courseSnapshot in snapshot.children) {
                        val courseData = courseSnapshot.value as? Map<String, Any> ?: continue
                        
                        val course = CourseEntity(
                            id = courseData["id"] as? String ?: "",
                            title = courseData["title"] as? String ?: "",
                            description = courseData["description"] as? String ?: "",
                            teacherId = courseData["teacherId"] as? String ?: "",
                            subject = courseData["subject"] as? String ?: "",
                            gradeLevel = courseData["gradeLevel"] as? String ?: "",
                            isPublished = courseData["isPublished"] as? Boolean ?: false,
                            coverImageUrl = courseData["coverImageUrl"] as? String,
                            difficultyLevel = (courseData["difficultyLevel"] as? Number)?.toInt() ?: 1,
                            estimatedHours = (courseData["estimatedHours"] as? Number)?.toInt() ?: 0,
                            createdAt = parseDateTime(courseData["createdAt"] as? String),
                            updatedAt = parseDateTime(courseData["updatedAt"] as? String)
                        )
                        courses.add(course)
                    }
                    
                    trySend(courses)
                    Log.d(TAG, "接收到${courses.size}个课程数据更新")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "处理课程数据失败", e)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase课程数据监听取消", error.toException())
                close(error.toException())
            }
        }
        
        coursesRef.addValueEventListener(listener)
        
        awaitClose {
            coursesRef.removeEventListener(listener)
            Log.d(TAG, "停止监听Firebase课程数据变化")
        }
    }
}