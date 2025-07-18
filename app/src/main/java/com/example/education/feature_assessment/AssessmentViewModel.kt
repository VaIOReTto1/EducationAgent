package com.example.education.feature_assessment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 智能评估页面的ViewModel
 */
@HiltViewModel
class AssessmentViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * 加载数据
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // 模拟网络延迟
                delay(1000)
                
                val courses = getMockCourses()
                val assessmentTypes = getMockAssessmentTypes()
                val recentAssessments = getMockRecentAssessments()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    courses = courses,
                    assessmentTypes = assessmentTypes,
                    recentAssessments = recentAssessments
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
    
    /**
     * 选择课程
     */
    fun selectCourse(course: AssessmentCourse) {
        _uiState.value = _uiState.value.copy(selectedCourse = course)
    }
    
    /**
     * 查看评估详情
     */
    fun viewAssessmentDetails(assessmentId: String) {
        // TODO: 实现评估详情查看逻辑
    }
    
    /**
     * 获取模拟课程数据
     */
    private fun getMockCourses(): List<AssessmentCourse> {
        return listOf(
            AssessmentCourse(
                id = "math_basic",
                name = "基础数学",
                studentsCount = 45,
                lastAssessment = "3天前"
            ),
            AssessmentCourse(
                id = "chinese_reading",
                name = "语文阅读",
                studentsCount = 38,
                lastAssessment = "1周前"
            ),
            AssessmentCourse(
                id = "english_basic",
                name = "基础英语",
                studentsCount = 42,
                lastAssessment = "5天前"
            ),
            AssessmentCourse(
                id = "science_nature",
                name = "自然科学",
                studentsCount = 35,
                lastAssessment = "2天前"
            ),
            AssessmentCourse(
                id = "history_culture",
                name = "历史文化",
                studentsCount = 40,
                lastAssessment = "1周前"
            )
        )
    }
    
    /**
     * 获取模拟评估类型数据
     */
    private fun getMockAssessmentTypes(): List<AssessmentType> {
        return listOf(
            AssessmentType(
                id = "knowledge_test",
                title = "知识测评",
                description = "测试学生对课程知识点的掌握程度",
                icon = Icons.Default.Quiz,
                difficulty = "简单",
                estimatedTime = "15-20分钟"
            ),
            AssessmentType(
                id = "skill_assessment",
                title = "技能评估",
                description = "评估学生的实际应用能力",
                icon = Icons.Default.Build,
                difficulty = "中等",
                estimatedTime = "20-30分钟"
            ),
            AssessmentType(
                id = "comprehensive_test",
                title = "综合测试",
                description = "全面评估知识与技能的综合应用",
                icon = Icons.Default.Assessment,
                difficulty = "困难",
                estimatedTime = "30-45分钟"
            ),
            AssessmentType(
                id = "adaptive_test",
                title = "自适应测试",
                description = "根据学生表现动态调整难度",
                icon = Icons.Default.Psychology,
                difficulty = "智能",
                estimatedTime = "20-40分钟"
            ),
            AssessmentType(
                id = "creative_assessment",
                title = "创新评估",
                description = "评估学生的创造性思维能力",
                icon = Icons.Default.Lightbulb,
                difficulty = "困难",
                estimatedTime = "25-35分钟"
            )
        )
    }
    
    /**
     * 获取模拟最近评估数据
     */
    private fun getMockRecentAssessments(): List<AssessmentHistory> {
        return listOf(
            AssessmentHistory(
                id = "assessment_001",
                title = "基础数学 - 知识测评",
                courseName = "基础数学",
                type = "知识测评",
                date = "2024-01-15",
                score = 85,
                studentCount = 28,
                status = "已完成"
            ),
            AssessmentHistory(
                id = "assessment_002",
                title = "语文阅读 - 技能评估",
                courseName = "语文阅读",
                type = "技能评估",
                date = "2024-01-12",
                score = 92,
                studentCount = 25,
                status = "已完成"
            ),
            AssessmentHistory(
                id = "assessment_003",
                title = "基础英语 - 综合测试",
                courseName = "基础英语",
                type = "综合测试",
                date = "2024-01-10",
                score = 78,
                studentCount = 30,
                status = "已完成"
            ),
            AssessmentHistory(
                id = "assessment_004",
                title = "自然科学 - 创新评估",
                courseName = "自然科学",
                type = "创新评估",
                date = "2024-01-08",
                score = 88,
                studentCount = 22,
                status = "已完成"
            ),
            AssessmentHistory(
                id = "assessment_005",
                title = "历史文化 - 自适应测试",
                courseName = "历史文化",
                type = "自适应测试",
                date = "2024-01-05",
                score = 91,
                studentCount = 26,
                status = "已完成"
            )
        )
    }
}

/**
 * 智能评估UI状态
 */
data class AssessmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val courses: List<AssessmentCourse> = emptyList(),
    val selectedCourse: AssessmentCourse? = null,
    val assessmentTypes: List<AssessmentType> = emptyList(),
    val recentAssessments: List<AssessmentHistory> = emptyList()
)

// AssessmentCourse 和 AssessmentType 数据类在 AssessmentScreen.kt 中定义

/**
 * 评估历史数据模型
 */
data class AssessmentHistory(
    val id: String,
    val title: String,
    val courseName: String,
    val type: String,
    val date: String,
    val score: Int,
    val studentCount: Int,
    val status: String
)