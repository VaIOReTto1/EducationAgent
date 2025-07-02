package com.example.education.core.repository

import android.util.Log
import com.example.education.core.database.dao.UserDao
import com.example.education.core.database.entities.UserEntity
import com.example.education.core.sync.FirebaseSyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户仓库实现类
 * 
 * 处理用户数据的本地缓存和远程同步
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firebaseSyncRepository: FirebaseSyncRepository
) : UserRepository {
    
    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
    
    override suspend fun getUserById(userId: String): UserEntity? {
        return try {
            Log.d(TAG, "获取用户信息: $userId")
            userDao.getUserById(userId)
        } catch (e: Exception) {
            Log.e(TAG, "获取用户信息失败: $userId", e)
            null
        }
    }
    
    override fun getAllUsers(): Flow<List<UserEntity>> {
        Log.d(TAG, "获取所有用户列表")
        return userDao.getAllUsers()
            .catch { e ->
                Log.e(TAG, "获取用户列表失败", e)
                emit(emptyList())
            }
    }
    
    override suspend fun insertUser(user: UserEntity) {
        try {
            Log.d(TAG, "插入用户: ${user.displayName}")
            userDao.insertUser(user)
            
            // 同步到Firebase
            firebaseSyncRepository.syncUserToFirebase(user)
        } catch (e: Exception) {
            Log.e(TAG, "插入用户失败", e)
            throw e
        }
    }
    
    override suspend fun updateUser(user: UserEntity) {
        try {
            Log.d(TAG, "更新用户: ${user.displayName}")
            userDao.updateUser(user)
            
            // 同步到Firebase
            firebaseSyncRepository.syncUserToFirebase(user)
        } catch (e: Exception) {
            Log.e(TAG, "更新用户失败", e)
            throw e
        }
    }
    
    override suspend fun deleteUser(userId: String) {
        try {
            Log.d(TAG, "删除用户: $userId")
            userDao.deleteUserById(userId)
            
            // 从Firebase删除
            firebaseSyncRepository.deleteUserFromFirebase(userId)
        } catch (e: Exception) {
            Log.e(TAG, "删除用户失败", e)
            throw e
        }
    }
    
    override fun searchUsers(query: String): Flow<List<UserEntity>> {
        Log.d(TAG, "搜索用户: $query")
        return userDao.searchUsersByName(query)
            .catch { e ->
                Log.e(TAG, "搜索用户失败", e)
                emit(emptyList())
            }
    }
    
    override suspend fun syncUsersFromFirebase(): Flow<Result<List<UserEntity>>> = flow {
        try {
            Log.d(TAG, "从Firebase同步用户数据")
            // TODO: 实现Firebase同步逻辑
            emit(Result.success(emptyList()))
        } catch (e: Exception) {
            Log.e(TAG, "同步用户数据失败", e)
            emit(Result.failure(e))
        }
    }
} 