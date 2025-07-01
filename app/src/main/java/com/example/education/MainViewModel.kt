package com.example.education

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.common.RoleManager
import com.example.education.core.common.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主ViewModel
 * 负责管理用户角色状态和角色切换逻辑
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val roleManager: RoleManager
) : ViewModel() {
    
    /**
     * 当前用户角色状态流
     */
    val currentRole: StateFlow<UserRole> = roleManager.currentRole
    
    /**
     * 切换用户角色
     * @param role 新的用户角色
     */
    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            roleManager.setRole(role)
        }
    }
    
    /**
     * 获取当前角色
     * @return 当前用户角色
     */
    fun getCurrentRole(): UserRole {
        return roleManager.getCurrentRole()
    }
    
    /**
     * 检查是否为教师角色
     * @return 是否为教师
     */
    fun isTeacher(): Boolean {
        return getCurrentRole() == UserRole.TEACHER
    }
    
    /**
     * 检查是否为学生角色
     * @return 是否为学生
     */
    fun isStudent(): Boolean {
        return getCurrentRole() == UserRole.STUDENT
    }
}