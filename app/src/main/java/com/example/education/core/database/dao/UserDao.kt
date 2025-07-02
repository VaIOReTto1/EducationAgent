package com.example.education.core.database.dao

import androidx.room.*
import com.example.education.core.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 * 
 * 提供用户相关的数据库操作方法
 */
@Dao
interface UserDao {
    
    /**
     * 根据用户ID获取用户信息
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    /**
     * 根据邮箱获取用户信息
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    /**
     * 获取所有用户
     */
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    /**
     * 根据角色获取用户列表
     */
    @Query("SELECT * FROM users WHERE role = :role ORDER BY createdAt DESC")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>
    
    /**
     * 插入用户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    /**
     * 更新用户信息
     */
    @Update
    suspend fun updateUser(user: UserEntity)
    
    /**
     * 根据ID删除用户
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
    
    /**
     * 删除用户
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    /**
     * 更新用户角色
     */
    @Query("UPDATE users SET role = :role WHERE id = :userId")
    suspend fun updateUserRole(userId: String, role: String)
    
    /**
     * 更新最后登录时间
     */
    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLoginTime(userId: String, timestamp: Long)
    
    /**
     * 根据姓名搜索用户
     */
    @Query("SELECT * FROM users WHERE displayName LIKE '%' || :query || '%' ORDER BY displayName ASC")
    fun searchUsersByName(query: String): Flow<List<UserEntity>>
    
    /**
     * 根据邮箱搜索用户
     */
    @Query("SELECT * FROM users WHERE email LIKE '%' || :query || '%' ORDER BY email ASC")
    fun searchUsersByEmail(query: String): Flow<List<UserEntity>>
    
    /**
     * 检查用户是否存在
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE id = :userId")
    suspend fun userExists(userId: String): Boolean
    
    /**
     * 检查邮箱是否已注册
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Boolean
    
    /**
     * 获取用户总数
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    /**
     * 根据角色获取用户数量
     */
    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    suspend fun getUserCountByRole(role: String): Int
    
    /**
     * 获取活跃用户（最近30天有登录）
     */
    @Query("""
        SELECT * FROM users 
        WHERE lastLoginAt > :thirtyDaysAgo 
        ORDER BY lastLoginAt DESC
    """)
    fun getActiveUsers(thirtyDaysAgo: Long = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L): Flow<List<UserEntity>>
    
    /**
     * 批量插入用户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    /**
     * 更新用户状态
     */
    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, isActive: Boolean)
} 