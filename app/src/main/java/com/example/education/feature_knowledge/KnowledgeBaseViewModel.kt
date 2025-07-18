package com.example.education.feature_knowledge

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Course 数据类在 KnowledgeBaseScreen.kt 中定义

/**
 * 知识库页面UI状态
 */
data class KnowledgeBaseUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val recommendedCourses: List<Course> = emptyList(),
    val allCourses: List<Course> = emptyList(),
    val selectedCourse: Course? = null,
    val showCourseDetails: Boolean = false
)

/**
 * 知识库页面ViewModel
 */
@HiltViewModel
class KnowledgeBaseViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(KnowledgeBaseUiState())
    val uiState: StateFlow<KnowledgeBaseUiState> = _uiState.asStateFlow()
    
    init {
        loadCourses()
    }
    
    /**
     * 加载课程数据
     */
    private fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // 模拟网络延迟
                delay(1000)
                
                val allCourses = generateMockCourses()
                val recommendedCourses = allCourses.take(3) // 取前3个作为推荐
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recommendedCourses = recommendedCourses,
                    allCourses = allCourses
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载课程失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 刷新课程
     */
    fun refreshCourses() {
        loadCourses()
    }
    
    /**
     * 显示课程详情
     */
    fun showCourseDetails(courseId: String) {
        val course = _uiState.value.allCourses.find { it.id == courseId }
        _uiState.value = _uiState.value.copy(
            selectedCourse = course,
            showCourseDetails = true
        )
    }
    
    /**
     * 隐藏课程详情
     */
    fun hideCourseDetails() {
        _uiState.value = _uiState.value.copy(
            selectedCourse = null,
            showCourseDetails = false
        )
    }
    
    /**
     * 生成模拟课程数据
     */
    private fun generateMockCourses(): List<Course> {
        return listOf(
            Course(
                id = "linux_basics",
                title = "Linux系统基础",
                description = "从零开始学习Linux操作系统，掌握命令行操作、文件管理、权限控制等核心技能。",
                teacherName = "张教授",
                studentCount = 1245,
                progress = 0.65f,
                category = "计算机科学",
                difficulty = "初级",
                duration = "32课时"
            ),
            Course(
                id = "machine_learning",
                title = "机器学习入门",
                description = "深入浅出地介绍机器学习的基本概念、算法原理和实际应用，包括监督学习、无监督学习等。",
                teacherName = "李博士",
                studentCount = 892,
                progress = 0.23f,
                category = "人工智能",
                difficulty = "中级",
                duration = "48课时"
            ),
            Course(
                id = "python_programming",
                title = "Python编程实战",
                description = "通过实际项目学习Python编程，涵盖基础语法、数据结构、面向对象编程和常用库的使用。",
                teacherName = "王老师",
                studentCount = 2156,
                progress = 0.89f,
                category = "编程语言",
                difficulty = "初级",
                duration = "40课时"
            ),
            Course(
                id = "data_structures",
                title = "数据结构与算法",
                description = "系统学习常用数据结构和算法，提升编程思维和解决问题的能力。",
                teacherName = "陈教授",
                studentCount = 1567,
                progress = 0.45f,
                category = "计算机科学",
                difficulty = "中级",
                duration = "56课时"
            ),
            Course(
                id = "web_development",
                title = "Web前端开发",
                description = "学习HTML、CSS、JavaScript等前端技术，掌握现代Web开发框架和工具。",
                teacherName = "刘工程师",
                studentCount = 1834,
                progress = 0.12f,
                category = "Web开发",
                difficulty = "初级",
                duration = "44课时"
            ),
            Course(
                id = "database_design",
                title = "数据库设计与管理",
                description = "深入学习关系型数据库的设计原理、SQL语言和数据库管理技术。",
                teacherName = "赵专家",
                studentCount = 923,
                progress = null,
                category = "数据库",
                difficulty = "中级",
                duration = "36课时"
            ),
            Course(
                id = "mobile_development",
                title = "移动应用开发",
                description = "学习Android和iOS移动应用开发，掌握跨平台开发技术和最佳实践。",
                teacherName = "孙架构师",
                studentCount = 756,
                progress = null,
                category = "移动开发",
                difficulty = "高级",
                duration = "52课时"
            ),
            Course(
                id = "network_security",
                title = "网络安全基础",
                description = "了解网络安全威胁、防护措施和安全协议，培养网络安全意识和技能。",
                teacherName = "周专家",
                studentCount = 634,
                progress = null,
                category = "网络安全",
                difficulty = "中级",
                duration = "38课时"
            )
        )
    }
}