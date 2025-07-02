package com.example.education.core.repository

import com.example.education.core.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据仓库接口
 * 
 * 定义用户相关的数据操作方法
 */
interface UserRepository {
    
    /**
     * 根据用户ID获取用户信息
     */
    suspend fun getUserById(userId: String): UserEntity?
    
    /**
     * 获取所有用户列表
     */
    fun getAllUsers(): Flow<List<UserEntity>>
    
    /**
     * 插入新用户
     */
    suspend fun insertUser(user: UserEntity)
    
    /**
     * 更新用户信息
     */
    suspend fun updateUser(user: UserEntity)
    
    /**
     * 删除用户
     */
    suspend fun deleteUser(userId: String)
    
    /**
     * 搜索用户
     */
    fun searchUsers(query: String): Flow<List<UserEntity>>
    
    /**
     * 从Firebase同步用户数据
     */
    suspend fun syncUsersFromFirebase(): Flow<Result<List<UserEntity>>>
} 