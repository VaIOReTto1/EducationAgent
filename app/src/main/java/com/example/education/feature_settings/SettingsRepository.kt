package com.example.education.feature_settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设置数据仓库
 * 管理应用设置的持久化存储
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LEARNING_REMINDER_KEY = booleanPreferencesKey("learning_reminder")
        private val PLAYBACK_SPEED_KEY = floatPreferencesKey("playback_speed")
        private val AI_ASSISTANT_MODE_KEY = stringPreferencesKey("ai_assistant_mode")
        private val AUTO_SAVE_KEY = booleanPreferencesKey("auto_save")
    }
    
    /**
     * 夜间模式设置
     */
    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }
    
    /**
     * 学习提醒设置
     */
    val learningReminder: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[LEARNING_REMINDER_KEY] ?: true
    }
    
    /**
     * 播放速度设置
     */
    val playbackSpeed: Flow<Float> = dataStore.data.map { preferences ->
        preferences[PLAYBACK_SPEED_KEY] ?: 1.0f
    }
    
    /**
     * AI助手模式设置
     */
    val aiAssistantMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[AI_ASSISTANT_MODE_KEY] ?: "basic"
    }
    
    /**
     * 自动保存设置
     */
    val autoSave: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTO_SAVE_KEY] ?: true
    }
    
    /**
     * 设置夜间模式
     */
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }
    
    /**
     * 设置学习提醒
     */
    suspend fun setLearningReminder(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[LEARNING_REMINDER_KEY] = enabled
        }
    }
    
    /**
     * 设置播放速度
     */
    suspend fun setPlaybackSpeed(speed: Float) {
        dataStore.edit { preferences ->
            preferences[PLAYBACK_SPEED_KEY] = speed
        }
    }
    
    /**
     * 设置AI助手模式
     */
    suspend fun setAiAssistantMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[AI_ASSISTANT_MODE_KEY] = mode
        }
    }
    
    /**
     * 设置自动保存
     */
    suspend fun setAutoSave(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_SAVE_KEY] = enabled
        }
    }
    
    /**
     * 清理缓存（模拟实现）
     */
    suspend fun clearCache() {
        // 这里可以实现实际的缓存清理逻辑
        // 比如清理图片缓存、临时文件等
    }
    
    /**
     * 清理用户设置
     */
    suspend fun clearUserSettings() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}