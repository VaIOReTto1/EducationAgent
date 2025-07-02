package com.example.education.agent

/**
 * 教师上下文数据类
 * 
 * 包含教师相关的上下文信息，用于智能体对话
 */
data class TeacherContext(
    /**
     * 教学科目
     */
    val subject: String,
    
    /**
     * 教学年级
     */
    val grade: String,
    
    /**
     * 课程内容
     */
    val courseContent: String,
    
    /**
     * 教学目标
     */
    val teachingGoal: String,
    
    /**
     * 班级规模
     */
    val classSize: Int,
    
    /**
     * 教学经验（年）
     */
    val teachingExperience: Int = 0,
    
    /**
     * 教学风格偏好
     */
    val teachingStyle: String? = null,
    
    /**
     * 学生水平描述
     */
    val studentLevel: String? = null,
    
    /**
     * 当前教学重点
     */
    val currentFocus: String? = null,
    
    /**
     * 额外上下文信息
     */
    val additionalContext: Map<String, Any> = emptyMap()
) 