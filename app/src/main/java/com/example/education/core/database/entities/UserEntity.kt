package com.example.education.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户实体类
 * 
 * 存储用户基本信息，支持学生和教师两种角色
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 显示名称
     */
    val displayName: String,
    
    /**
     * 邮箱地址
     */
    val email: String,
    
    /**
     * 头像URL
     */
    val avatarUrl: String? = null,
    
    /**
     * 用户角色：STUDENT（学生）或 TEACHER（教师）
     */
    val role: String,
    
    /**
     * 学校或机构名称
     */
    val institution: String? = null,
    
    /**
     * 年级（学生）或教学年限（教师）
     */
    val grade: String? = null,
    
    /**
     * 偏好设置（JSON格式）
     */
    val preferences: String? = null,
    
    /**
     * 账户状态
     */
    val isActive: Boolean = true,
    
    /**
     * 创建时间（时间戳）
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * 最后登录时间
     */
    val lastLoginAt: Long = System.currentTimeMillis(),
    
    /**
     * 最后更新时间
     */
    val updatedAt: Long = System.currentTimeMillis()
) 