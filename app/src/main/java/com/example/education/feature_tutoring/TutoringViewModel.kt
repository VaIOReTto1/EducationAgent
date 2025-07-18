package com.example.education.feature_tutoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 智能辅导页面的ViewModel
 */
@HiltViewModel
class TutoringViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(TutoringUiState())
    val uiState: StateFlow<TutoringUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * 加载数据
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // 模拟加载数据
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 刷新数据
     */
    fun refreshData() {
        loadData()
    }
}

/**
 * UI状态数据类
 */
data class TutoringUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) 