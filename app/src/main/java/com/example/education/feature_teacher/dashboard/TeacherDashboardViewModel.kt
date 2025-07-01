package com.example.education.feature_teacher.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.database.entity.CourseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 教师仪表盘ViewModel
 * 实现MVI架构模式
 */
@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val teacherDashboardUseCase: TeacherDashboardUseCase
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(TeacherDashboardUiState())
    val uiState: StateFlow<TeacherDashboardUiState> = _uiState.asStateFlow()
    
    init {
        // 初始化时加载数据
        handleIntent(TeacherDashboardIntent.LoadDashboard)
    }
    
    /**
     * 处理用户意图
     */
    fun handleIntent(intent: TeacherDashboardIntent) {
        when (intent) {
            is TeacherDashboardIntent.LoadDashboard -> loadDashboard()
            is TeacherDashboardIntent.RefreshDashboard -> refreshDashboard()
            is TeacherDashboardIntent.SelectCourse -> selectCourse(intent.courseId)
            is TeacherDashboardIntent.CreateCourse -> createCourse(intent.title, intent.description, intent.category, intent.difficulty)
            is TeacherDashboardIntent.PublishCourse -> publishCourse(intent.courseId)
            is TeacherDashboardIntent.LoadCourseAnalytics -> loadCourseAnalytics(intent.courseId, intent.days)
            is TeacherDashboardIntent.LoadStudentProgress -> loadStudentProgress(intent.courseId)
            is TeacherDashboardIntent.LoadPendingAssessments -> loadPendingAssessments()
            is TeacherDashboardIntent.LoadRecentQuestions -> loadRecentQuestions(intent.limit)
            is TeacherDashboardIntent.ClearError -> clearError()
            is TeacherDashboardIntent.ShowCreateCourseDialog -> showCreateCourseDialog(intent.show)
            is TeacherDashboardIntent.ShowCourseAnalytics -> showCourseAnalytics(intent.show)
        }
    }
    
    /**
     * 加载仪表盘数据
     */
    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 加载教师课程
                teacherDashboardUseCase.getTeacherCourses().collect { courses ->
                    _uiState.value = _uiState.value.copy(
                        courses = courses,
                        isLoading = false
                    )
                    
                    // 如果有课程，加载第一个课程的统计信息
                    if (courses.isNotEmpty() && _uiState.value.selectedCourseId == null) {
                        selectCourse(courses.first().id)
                    }
                }
                
                // 加载待批改评估
                loadPendingAssessments()
                
                // 加载最近提问
                loadRecentQuestions()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载仪表盘数据失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 刷新仪表盘
     */
    private fun refreshDashboard() {
        loadDashboard()
    }
    
    /**
     * 选择课程
     */
    private fun selectCourse(courseId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedCourseId = courseId,
                    isLoadingCourseData = true
                )
                
                // 加载课程统计信息
                teacherDashboardUseCase.getCourseStatistics(courseId).collect { statistics ->
                    _uiState.value = _uiState.value.copy(
                        courseStatistics = statistics,
                        isLoadingCourseData = false
                    )
                }
                
                // 加载学生进度
                loadStudentProgress(courseId)
                
                // 加载课程分析数据
                loadCourseAnalytics(courseId)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingCourseData = false,
                    error = "加载课程数据失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 创建课程
     */
    private fun createCourse(
        title: String,
        description: String,
        category: String,
        difficulty: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isCreatingCourse = true)
                
                // 验证输入数据
                val errors = TeacherDashboardUtils.validateCourseData(title, description, category, difficulty)
                if (errors.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isCreatingCourse = false,
                        error = errors.joinToString("\n")
                    )
                    return@launch
                }
                
                val result = teacherDashboardUseCase.createCourse(title, description, category, difficulty)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isCreatingCourse = false,
                        showCreateCourseDialog = false
                    )
                    // 刷新课程列表
                    loadDashboard()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isCreatingCourse = false,
                        error = "创建课程失败: ${result.exceptionOrNull()?.message}"
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreatingCourse = false,
                    error = "创建课程失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 发布课程
     */
    private fun publishCourse(courseId: String) {
        viewModelScope.launch {
            try {
                val result = teacherDashboardUseCase.publishCourse(courseId)
                
                if (result.isSuccess) {
                    // 刷新课程列表
                    loadDashboard()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "发布课程失败: ${result.exceptionOrNull()?.message}"
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "发布课程失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载课程分析数据
     */
    private fun loadCourseAnalytics(courseId: String, days: Int = 30) {
        viewModelScope.launch {
            try {
                teacherDashboardUseCase.getCourseAnalytics(courseId, days).collect { analytics ->
                    _uiState.value = _uiState.value.copy(courseAnalytics = analytics)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "加载课程分析数据失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载学生进度
     */
    private fun loadStudentProgress(courseId: String) {
        viewModelScope.launch {
            try {
                teacherDashboardUseCase.getStudentProgressOverview(courseId).collect { progressList ->
                    _uiState.value = _uiState.value.copy(studentProgressList = progressList)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "加载学生进度失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载待批改评估
     */
    private fun loadPendingAssessments() {
        viewModelScope.launch {
            try {
                teacherDashboardUseCase.getPendingAssessments().collect { assessments ->
                    _uiState.value = _uiState.value.copy(pendingAssessments = assessments)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "加载待批改评估失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载最近提问
     */
    private fun loadRecentQuestions(limit: Int = 10) {
        viewModelScope.launch {
            try {
                teacherDashboardUseCase.getRecentStudentQuestions(limit).collect { questions ->
                    _uiState.value = _uiState.value.copy(recentQuestions = questions)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "加载最近提问失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除错误
     */
    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 显示/隐藏创建课程对话框
     */
    private fun showCreateCourseDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateCourseDialog = show)
    }
    
    /**
     * 显示/隐藏课程分析
     */
    private fun showCourseAnalytics(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCourseAnalytics = show)
    }
}

/**
 * 教师仪表盘UI状态
 */
data class TeacherDashboardUiState(
    val isLoading: Boolean = false,
    val isLoadingCourseData: Boolean = false,
    val isCreatingCourse: Boolean = false,
    val courses: List<CourseEntity> = emptyList(),
    val selectedCourseId: String? = null,
    val courseStatistics: CourseStatistics? = null,
    val courseAnalytics: CourseAnalytics? = null,
    val studentProgressList: List<StudentProgress> = emptyList(),
    val pendingAssessments: List<PendingAssessment> = emptyList(),
    val recentQuestions: List<StudentQuestion> = emptyList(),
    val showCreateCourseDialog: Boolean = false,
    val showCourseAnalytics: Boolean = false,
    val error: String? = null
)

/**
 * 教师仪表盘用户意图
 */
sealed class TeacherDashboardIntent {
    object LoadDashboard : TeacherDashboardIntent()
    object RefreshDashboard : TeacherDashboardIntent()
    data class SelectCourse(val courseId: String) : TeacherDashboardIntent()
    data class CreateCourse(
        val title: String,
        val description: String,
        val category: String,
        val difficulty: String
    ) : TeacherDashboardIntent()
    data class PublishCourse(val courseId: String) : TeacherDashboardIntent()
    data class LoadCourseAnalytics(val courseId: String, val days: Int = 30) : TeacherDashboardIntent()
    data class LoadStudentProgress(val courseId: String) : TeacherDashboardIntent()
    object LoadPendingAssessments : TeacherDashboardIntent()
    data class LoadRecentQuestions(val limit: Int = 10) : TeacherDashboardIntent()
    object ClearError : TeacherDashboardIntent()
    data class ShowCreateCourseDialog(val show: Boolean) : TeacherDashboardIntent()
    data class ShowCourseAnalytics(val show: Boolean) : TeacherDashboardIntent()
}