package com.example.education.core.user

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户角色枚举
 */
enum class UserRole(val value: String, val displayName: String) {
    TEACHER("teacher", "教师"),
    STUDENT("student", "学生");
    
    companion object {
        fun fromValue(value: String): UserRole {
            return values().find { it.value == value } ?: STUDENT
        }
    }
}

/**
 * 角色管理器
 * 
 * 管理用户角色切换（教师/学生），支持持久化存储
 */
@Singleton
class RoleManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    companion object {
        private const val TAG = "RoleManager"
        
        // DataStore keys
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val LAST_SWITCHED_TIME_KEY = longPreferencesKey("last_switched_time")
        
        // 角色常量
        const val ROLE_TEACHER = "teacher"
        const val ROLE_STUDENT = "student"
        const val DEFAULT_ROLE = ROLE_STUDENT
    }
    
    /**
     * 获取当前用户角色
     */
    val currentRole: Flow<UserRole> = dataStore.data.map { preferences ->
        val roleValue = preferences[USER_ROLE_KEY] ?: UserRole.STUDENT.value
        UserRole.fromValue(roleValue)
    }
    
    /**
     * 获取当前用户ID
     */
    val currentUserId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }
    
    /**
     * 获取当前用户名
     */
    val currentUserName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }
    
    /**
     * 获取当前用户信息
     */
    val currentUser: Flow<UserInfo?> = dataStore.data.map { preferences ->
        val userId = preferences[USER_ID_KEY]
        val userName = preferences[USER_NAME_KEY]
        val userEmail = preferences[USER_EMAIL_KEY]
        val role = preferences[USER_ROLE_KEY] ?: UserRole.STUDENT.value
        
        if (userId != null && userName != null && userEmail != null) {
            UserInfo(
                id = userId,
                name = userName,
                email = userEmail,
                currentRole = role
            )
        } else {
            null
        }
    }
    
    /**
     * 设置用户角色
     */
    suspend fun setUserRole(role: UserRole) {
        dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role.value
        }
    }
    
    /**
     * 设置用户信息
     */
    suspend fun setUserInfo(
        userId: String,
        userName: String,
        role: UserRole
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[USER_ROLE_KEY] = role.value
        }
    }
    
    /**
     * 切换到教师角色
     */
    suspend fun switchToTeacher() {
        Log.d(TAG, "切换到教师角色")
        setUserRole(UserRole.TEACHER)
    }
    
    /**
     * 切换到学生角色
     */
    suspend fun switchToStudent() {
        Log.d(TAG, "切换到学生角色")
        setUserRole(UserRole.STUDENT)
    }
    
    /**
     * 获取当前角色（同步方法）
     */
    suspend fun getCurrentRole(): UserRole {
        val preferences = dataStore.data
        var currentRoleValue = UserRole.STUDENT
        preferences.collect { prefs ->
            val roleValue = prefs[USER_ROLE_KEY] ?: UserRole.STUDENT.value
            currentRoleValue = UserRole.fromValue(roleValue)
        }
        return currentRoleValue
    }
    
    /**
     * 获取当前用户ID（同步方法）
     */
    suspend fun getCurrentUserId(): String? {
        val preferences = dataStore.data
        var userId: String? = null
        preferences.collect { prefs ->
            userId = prefs[USER_ID_KEY]
        }
        return userId
    }
    
    /**
     * 检查是否为教师
     */
    fun isTeacher(): Flow<Boolean> = currentRole.map { it == UserRole.TEACHER }
    
    /**
     * 检查是否为学生
     */
    fun isStudent(): Flow<Boolean> = currentRole.map { it == UserRole.STUDENT }
    
    /**
     * 获取角色显示名称
     */
    fun getRoleDisplayName(role: String): String {
        return when (role) {
            ROLE_TEACHER -> "教师"
            ROLE_STUDENT -> "学生"
            else -> "未知角色"
        }
    }
    
    /**
     * 获取角色描述
     */
    fun getRoleDescription(role: String): String {
        return when (role) {
            ROLE_TEACHER -> "智能备课、教学分析、学生管理"
            ROLE_STUDENT -> "课程学习、AI辅导、进度跟踪"
            else -> ""
        }
    }
    
    /**
     * 清除用户数据
     */
    suspend fun clearUserData() {
        Log.d(TAG, "清除用户信息")
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * 生成会话ID（用于Dify API）
     */
    fun generateConversationId(userId: String, role: UserRole, agentType: String): String {
        return "${userId}_${role.value}_${agentType}"
    }
}

/**
 * 用户信息数据类
 */
data class UserInfo(
    val id: String,
    val name: String,
    val email: String,
    val currentRole: String,
    val avatarUrl: String? = null
) 