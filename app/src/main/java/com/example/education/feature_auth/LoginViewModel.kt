package com.example.education.feature_auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.user.RoleManager
import com.example.education.core.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录页面UI状态
 */
data class LoginUiState(
    val account: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.STUDENT,
    val accountError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isLoading: Boolean = false
) {
    val canLogin: Boolean
        get() = account.isNotBlank() && 
                password.isNotBlank() && 
                accountError == null && 
                passwordError == null
}

/**
 * 登录页面ViewModel
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val roleManager: RoleManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    companion object {
        // 账号正则：字母开头，可包含字母数字，长度6-20
        private val ACCOUNT_REGEX = "^[a-zA-Z][a-zA-Z0-9]{5,19}$".toRegex()
        // 密码正则：至少6位，包含字母和数字
        private val PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$".toRegex()
        
        // 硬编码的测试账号
        private val TEST_ACCOUNTS = mapOf(
            "teacher001" to Pair("ye123456", UserRole.TEACHER),
            "teacher002" to Pair("123456", UserRole.TEACHER),
            "student001" to Pair("ye123456", UserRole.STUDENT),
            "student002" to Pair("123456", UserRole.STUDENT)
        )
    }
    
    /**
     * 更新账号
     */
    fun updateAccount(account: String) {
        _uiState.value = _uiState.value.copy(
            account = account,
            accountError = validateAccount(account),
            loginError = null
        )
    }
    
    /**
     * 更新密码
     */
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password),
            loginError = null
        )
    }
    
    /**
     * 选择角色
     */
    fun selectRole(role: UserRole) {
        _uiState.value = _uiState.value.copy(
            selectedRole = role,
            loginError = null
        )
    }
    
    /**
     * 执行登录
     */
    fun login(onSuccess: (UserRole) -> Unit) {
        val currentState = _uiState.value
        
        // 验证输入
        val accountError = validateAccount(currentState.account)
        val passwordError = validatePassword(currentState.password)
        
        if (accountError != null || passwordError != null) {
            _uiState.value = currentState.copy(
                accountError = accountError,
                passwordError = passwordError
            )
            return
        }
        
        _uiState.value = currentState.copy(isLoading = true, loginError = null)
        
        viewModelScope.launch {
            try {
                // 模拟网络延迟
                kotlinx.coroutines.delay(1000)
                
                // 验证账号密码
                val testAccount = TEST_ACCOUNTS[currentState.account]
                if (testAccount == null) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        loginError = "账号不存在"
                    )
                    return@launch
                }
                
                if (testAccount.first != currentState.password) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        loginError = "密码错误"
                    )
                    return@launch
                }
                
                // 检查角色是否匹配
                if (testAccount.second != currentState.selectedRole) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        loginError = "所选角色与账号不匹配"
                    )
                    return@launch
                }
                
                // 登录成功，保存用户信息
                val userName = if (currentState.selectedRole == UserRole.TEACHER) {
                    when (currentState.account) {
                        "teacher001" -> "张老师"
                        "teacher002" -> "李老师"
                        else -> "教师"
                    }
                } else {
                    when (currentState.account) {
                        "student001" -> "小明"
                        "student002" -> "小红"
                        else -> "学生"
                    }
                }
                
                roleManager.setUserInfo(
                    userId = currentState.account,
                    userName = userName,
                    role = currentState.selectedRole
                )
                
                _uiState.value = currentState.copy(isLoading = false)
                onSuccess(currentState.selectedRole)
                
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    loginError = "登录失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 验证账号格式
     */
    private fun validateAccount(account: String): String? {
        return when {
            account.isBlank() -> null // 空白时不显示错误
            !ACCOUNT_REGEX.matches(account) -> "账号格式不正确（字母开头，6-20位字母数字）"
            else -> null
        }
    }
    
    /**
     * 验证密码格式
     */
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null // 空白时不显示错误
            password.length < 6 -> "密码至少6位"
            !password.any { it.isLetter() } -> "密码必须包含字母"
            !password.any { it.isDigit() } -> "密码必须包含数字"
            else -> null
        }
    }
}