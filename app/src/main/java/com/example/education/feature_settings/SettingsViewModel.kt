package com.example.education.feature_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.user.RoleManager
import com.example.education.core.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置页面UI状态
 */
data class SettingsUiState(
    val userId: String? = null,
    val userName: String? = null,
    val userRole: UserRole? = null,
    val isDarkMode: Boolean = false,
    val learningReminder: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val aiAssistantMode: String = "basic", // basic, advanced
    val autoSave: Boolean = true,
    val showPlaybackSpeedDialog: Boolean = false
)

/**
 * 设置页面ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val roleManager: RoleManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        observeUserData()
        observeSettings()
    }
    
    /**
     * 观察用户数据
     */
    private fun observeUserData() {
        viewModelScope.launch {
            combine(
                roleManager.currentUserId,
                roleManager.currentUserName,
                roleManager.currentRole
            ) { userId, userName, role ->
                Triple(userId, userName, role)
            }.collect { (userId, userName, role) ->
                _uiState.value = _uiState.value.copy(
                    userId = userId,
                    userName = userName,
                    userRole = role
                )
            }
        }
    }
    
    /**
     * 观察设置数据
     */
    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkMode,
                settingsRepository.learningReminder,
                settingsRepository.playbackSpeed,
                settingsRepository.aiAssistantMode,
                settingsRepository.autoSave
            ) { isDarkMode, learningReminder, playbackSpeed, aiAssistantMode, autoSave ->
                _uiState.value = _uiState.value.copy(
                    isDarkMode = isDarkMode,
                    learningReminder = learningReminder,
                    playbackSpeed = playbackSpeed,
                    aiAssistantMode = aiAssistantMode,
                    autoSave = autoSave
                )
            }.collect()
        }
    }
    
    /**
     * 切换夜间模式
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
        }
    }
    
    /**
     * 切换学习提醒
     */
    fun toggleLearningReminder(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setLearningReminder(enabled)
        }
    }
    
    /**
     * 设置播放速度
     */
    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            settingsRepository.setPlaybackSpeed(speed)
        }
    }
    
    /**
     * 显示播放速度对话框
     */
    fun showPlaybackSpeedDialog() {
        _uiState.value = _uiState.value.copy(showPlaybackSpeedDialog = true)
    }
    
    /**
     * 隐藏播放速度对话框
     */
    fun hidePlaybackSpeedDialog() {
        _uiState.value = _uiState.value.copy(showPlaybackSpeedDialog = false)
    }
    
    /**
     * 切换AI助手模式
     */
    fun toggleAiAssistantMode() {
        viewModelScope.launch {
            val newMode = if (_uiState.value.aiAssistantMode == "basic") "advanced" else "basic"
            settingsRepository.setAiAssistantMode(newMode)
        }
    }
    
    /**
     * 切换自动保存
     */
    fun toggleAutoSave(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoSave(enabled)
        }
    }
    
    /**
     * 清理缓存
     */
    fun clearCache() {
        viewModelScope.launch {
            settingsRepository.clearCache()
        }
    }
    
    /**
     * 登出
     */
    fun logout() {
        viewModelScope.launch {
            roleManager.clearUserData()
            settingsRepository.clearUserSettings()
        }
    }
}