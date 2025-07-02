package com.example.education.agent

/**
 * 学生学习上下文数据类
 * 
 * 封装学生的学习状态、偏好设置和当前课程信息，
 * 用于为AI智能体提供个性化服务的上下文信息
 */
data class StudentContext(
    // === 基础信息 ===
    /**
     * 学生用户ID
     */
    val userId: String,
    
    /**
     * 当前课程ID
     */
    val currentCourseId: String? = null,
    
    /**
     * 当前章节ID
     */
    val currentChapterId: String? = null,
    
    // === 学习进度信息 ===
    /**
     * 当前学习进度百分比（0.0-100.0）
     */
    val learningProgress: Float = 0f,
    
    /**
     * 累计学习时间（毫秒）
     */
    val timeSpent: Long = 0L,
    
    /**
     * 是否完成当前章节
     */
    val isCompleted: Boolean = false,
    
    /**
     * 最后访问时间
     */
    val lastAccessTime: Long = System.currentTimeMillis(),
    
    /**
     * 已完成的章节列表
     */
    val completedChapters: List<String> = emptyList(),
    
    // === 学习偏好设置 ===
    /**
     * 学术水平 (初级/中级/高级)
     */
    val academicLevel: String = "中级",
    
    /**
     * 学习风格 (视觉/听觉/实践/阅读)
     */
    val learningStyle: String = "视觉",
    
    /**
     * 难度偏好 (简单/适中/困难)
     */
    val difficultyPreference: String = "适中",
    
    /**
     * 可用学习时间（分钟）
     */
    val studyTimeAvailable: Int = 60,
    
    /**
     * 学习目标列表
     */
    val learningGoals: List<String> = emptyList(),
    
    // === 实时状态 ===
    /**
     * 当前学习状态 (学习中/休息/离线)
     */
    val currentStatus: String = "学习中",
    
    /**
     * 专注度评分 (0-100)
     */
    val focusScore: Int = 80,
    
    /**
     * 学习效率评分 (0-100)
     */
    val efficiencyScore: Int = 75,
    
    // === 历史数据 ===
    /**
     * 总学习时长（小时）
     */
    val totalStudyHours: Float = 0f,
    
    /**
     * 总完成章节数
     */
    val totalCompletedChapters: Int = 0,
    
    /**
     * 平均学习进度
     */
    val averageProgress: Float = 0f,
    
    /**
     * 最近的学习会话记录
     */
    val recentSessions: List<StudySession> = emptyList(),
    
    // === 别名属性（用于兼容性） ===
    /**
     * 当前课程（别名）
     */
    val currentCourse: String? = currentCourseId,
    
    /**
     * 当前章节（别名）
     */
    val currentChapter: String? = currentChapterId,
    
    /**
     * 难度级别（别名）
     */
    val difficultyLevel: String = difficultyPreference,
    
    /**
     * 偏好风格（别名）
     */
    val preferredStyle: String = learningStyle
) {
    
    /**
     * 获取学习阶段
     */
    fun getStudyPhase(): String = when {
        learningProgress < 25f -> "入门阶段"
        learningProgress < 50f -> "基础阶段"  
        learningProgress < 75f -> "进阶阶段"
        learningProgress < 95f -> "精通阶段"
        else -> "完成阶段"
    }
    
    /**
     * 判断是否需要休息
     */
    fun needsBreak(): Boolean = timeSpent > 45 * 60 * 1000L // 超过45分钟
    
    /**
     * 获取学习建议
     */
    fun getStudyRecommendation(): String = when {
        focusScore < 60 -> "建议休息片刻，恢复专注力"
        efficiencyScore < 50 -> "可以尝试调整学习方法"
        learningProgress > 90f -> "即将完成，加油冲刺！"
        else -> "保持当前学习节奏"
    }
    
    /**
     * 转换为Map格式（用于API调用）
     */
    fun toInputMap(): Map<String, Any> = mapOf(
        "user_id" to userId,
        "course_id" to (currentCourseId ?: ""),
        "chapter_id" to (currentChapterId ?: ""),
        "progress" to learningProgress,
        "time_spent" to timeSpent,
        "academic_level" to academicLevel,
        "learning_style" to learningStyle,
        "difficulty" to difficultyPreference,
        "study_phase" to getStudyPhase(),
        "focus_score" to focusScore,
        "efficiency_score" to efficiencyScore
    )
}

/**
 * 学习会话记录
 */
data class StudySession(
    val sessionId: String,
    val startTime: Long,
    val endTime: Long,
    val chapterId: String,
    val progressGained: Float,
    val focusScore: Int,
    val notes: String = ""
) {
    
    /**
     * 获取会话时长（分钟）
     */
    fun getDurationMinutes(): Int = ((endTime - startTime) / (1000 * 60)).toInt()
    
    /**
     * 是否为有效会话（时长超过5分钟）
     */
    fun isValidSession(): Boolean = getDurationMinutes() >= 5
} 