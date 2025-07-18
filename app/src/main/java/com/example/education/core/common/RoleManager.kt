package com.example.education.core.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户角色枚举
 */
enum class UserRole(val value: String) {
    TEACHER("teacher"),
    STUDENT("student");

    companion object {
        fun fromString(value: String): UserRole {
            return values().find { it.value == value } ?: STUDENT
        }
    }
}

/**
 * 角色管理器
 * 负责管理用户角色切换和相关状态
 */
@Singleton
class RoleManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val CURRENT_ROLE_KEY = stringPreferencesKey("current_role")
        private val CURRENT_USER_ID_KEY = stringPreferencesKey("current_user_id")
        private val CURRENT_USER_NAME_KEY = stringPreferencesKey("current_user_name")
        private val CURRENT_USER_EMAIL_KEY = stringPreferencesKey("current_user_email")
    }

    /**
     * 当前用户角色流
     */
    val currentRole: Flow<UserRole> = dataStore.data.map { preferences ->
        val roleValue = preferences[CURRENT_ROLE_KEY] ?: UserRole.STUDENT.value
        UserRole.fromString(roleValue)
    }

    /**
     * 当前用户ID流
     */
    val currentUserId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[CURRENT_USER_ID_KEY]
    }

    /**
     * 当前用户名流
     */
    val currentUserName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[CURRENT_USER_NAME_KEY]
    }

    /**
     * 当前用户邮箱流
     */
    val currentUserEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[CURRENT_USER_EMAIL_KEY]
    }

    /**
     * 是否为教师角色
     */
    val isTeacher: Flow<Boolean> = currentRole.map { it == UserRole.TEACHER }

    /**
     * 是否为学生角色
     */
    val isStudent: Flow<Boolean> = currentRole.map { it == UserRole.STUDENT }

    /**
     * 设置当前用户信息
     * @param userId 用户ID
     * @param name 用户名
     * @param email 用户邮箱
     * @param role 用户角色
     */
    suspend fun setCurrentUser(
        userId: String,
        name: String,
        email: String,
        role: UserRole
    ) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID_KEY] = userId
            preferences[CURRENT_USER_NAME_KEY] = name
            preferences[CURRENT_USER_EMAIL_KEY] = email
            preferences[CURRENT_ROLE_KEY] = role.value
        }
    }

    /**
     * 切换用户角色
     * @param newRole 新角色
     */
    suspend fun switchRole(newRole: UserRole) {
        dataStore.edit { preferences ->
            preferences[CURRENT_ROLE_KEY] = newRole.value
        }
    }

    /**
     * 清除当前用户信息（登出）
     */
    suspend fun clearCurrentUser() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID_KEY)
            preferences.remove(CURRENT_USER_NAME_KEY)
            preferences.remove(CURRENT_USER_EMAIL_KEY)
            preferences.remove(CURRENT_ROLE_KEY)
        }
    }

    /**
     * 设置用户角色
     * @param role 新角色
     */
    suspend fun setRole(role: UserRole) {
        dataStore.edit { preferences ->
            preferences[CURRENT_ROLE_KEY] = role.value
        }
    }

    /**
     * 获取当前用户ID（挂起函数）
     */
    suspend fun getCurrentUserId(): String? {
        var result: String? = null
        currentUserId.collect { result = it }
        return result
    }

    /**
     * 获取当前角色（同步方法）
     */
    fun getCurrentRole(): UserRole {
        // 这里返回默认值，实际应用中应该从StateFlow获取
        return UserRole.STUDENT
    }

    /**
     * 获取当前角色（挂起函数）
     */
    suspend fun getCurrentRoleSuspend(): UserRole {
        var result: UserRole = UserRole.STUDENT
        currentRole.collect { result = it }
        return result
    }

    /**
     * 生成角色特定的会话ID
     * @param baseId 基础ID
     * @param role 角色
     * @return 格式化的会话ID
     */
    fun generateRoleConversationId(baseId: String, role: UserRole): String {
        return "${baseId}_${role.value}_${System.currentTimeMillis()}"
    }

    /**
     * 检查是否已登录
     */
    val isLoggedIn: Flow<Boolean> = currentUserId.map { it != null }
}