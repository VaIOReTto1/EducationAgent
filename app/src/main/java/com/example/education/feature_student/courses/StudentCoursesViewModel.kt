package com.example.education.feature_student.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 学生课程页面的ViewModel
 * 实现MVI架构，管理UI状态和处理用户意图
 */
@HiltViewModel
class StudentCoursesViewModel @Inject constructor(
    private val studentCoursesUseCase: StudentCoursesUseCase
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(StudentCoursesUiState())
    val uiState: StateFlow<StudentCoursesUiState> = _uiState.asStateFlow()
    
    init {
        // 初始化时加载数据
        handleIntent(StudentCoursesIntent.LoadCourses)
        handleIntent(StudentCoursesIntent.LoadRecommendedCourses)
        handleIntent(StudentCoursesIntent.LoadContinueLearningCourses)
        handleIntent(StudentCoursesIntent.LoadLearningStatistics)
    }
    
    /**
     * 处理用户意图
     */
    fun handleIntent(intent: StudentCoursesIntent) {
        when (intent) {
            is StudentCoursesIntent.LoadCourses -> loadCourses()
            is StudentCoursesIntent.LoadRecommendedCourses -> loadRecommendedCourses()
            is StudentCoursesIntent.LoadContinueLearningCourses -> loadContinueLearningCourses()
            is StudentCoursesIntent.LoadLearningStatistics -> loadLearningStatistics()
            is StudentCoursesIntent.RefreshCourses -> refreshCourses()
            is StudentCoursesIntent.SearchCourses -> searchCourses(intent.query)
            is StudentCoursesIntent.FilterByCategory -> filterByCategory(intent.category)
            is StudentCoursesIntent.FilterByDifficulty -> filterByDifficulty(intent.difficulty)
            is StudentCoursesIntent.SortCourses -> sortCourses(intent.sortOption)
            is StudentCoursesIntent.ToggleEnrolledOnly -> toggleEnrolledOnly()
            is StudentCoursesIntent.EnrollCourse -> enrollCourse(intent.courseId)
            is StudentCoursesIntent.SelectCourse -> selectCourse(intent.courseId)
            is StudentCoursesIntent.ClearError -> clearError()
            is StudentCoursesIntent.RetryLastAction -> retryLastAction()
        }
    }
    
    /**
     * 加载课程列表
     */
    private fun loadCourses() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                studentCoursesUseCase.getStudentCourses().collect { courses ->
                    _uiState.update { currentState ->
                        val filteredCourses = StudentCoursesUtils.filterCourses(
                            courses = courses,
                            searchQuery = currentState.searchQuery,
                            selectedCategory = currentState.selectedCategory,
                            selectedDifficulty = currentState.selectedDifficulty,
                            showOnlyEnrolled = currentState.showOnlyEnrolled
                        )
                        
                        val sortedCourses = StudentCoursesUtils.sortCourses(
                            courses = filteredCourses,
                            sortBy = currentState.sortOption
                        )
                        
                        currentState.copy(
                            allCourses = courses,
                            filteredCourses = sortedCourses,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "加载课程失败: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * 加载推荐课程
     */
    private fun loadRecommendedCourses() {
        viewModelScope.launch {
            try {
                studentCoursesUseCase.getRecommendedCourses().collect { courses ->
                    _uiState.update { it.copy(recommendedCourses = courses) }
                }
            } catch (e: Exception) {
                // 推荐课程加载失败不影响主要功能
                _uiState.update { it.copy(recommendedCourses = emptyList()) }
            }
        }
    }
    
    /**
     * 加载继续学习的课程
     */
    private fun loadContinueLearningCourses() {
        viewModelScope.launch {
            try {
                studentCoursesUseCase.getContinueLearningCourses().collect { courses ->
                    _uiState.update { it.copy(continueLearningCourses = courses) }
                }
            } catch (e: Exception) {
                // 继续学习课程加载失败不影响主要功能
                _uiState.update { it.copy(continueLearningCourses = emptyList()) }
            }
        }
    }
    
    /**
     * 加载学习统计
     */
    private fun loadLearningStatistics() {
        viewModelScope.launch {
            try {
                studentCoursesUseCase.getLearningStatistics().collect { statistics ->
                    _uiState.update { it.copy(learningStatistics = statistics) }
                }
            } catch (e: Exception) {
                // 统计数据加载失败不影响主要功能
                _uiState.update { 
                    it.copy(
                        learningStatistics = LearningStatistics(
                            enrolledCourses = 0,
                            completedCourses = 0,
                            averageProgress = 0f,
                            totalTimeSpent = 0L,
                            totalAssessments = 0,
                            averageScore = 0f
                        )
                    ) 
                }
            }
        }
    }
    
    /**
     * 刷新课程数据
     */
    private fun refreshCourses() {
        _uiState.update { it.copy(isRefreshing = true) }
        
        viewModelScope.launch {
            try {
                // 重新加载所有数据
                loadCourses()
                loadRecommendedCourses()
                loadContinueLearningCourses()
                loadLearningStatistics()
                
                _uiState.update { it.copy(isRefreshing = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRefreshing = false,
                        error = "刷新失败: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * 搜索课程
     */
    private fun searchCourses(query: String) {
        val validationError = StudentCoursesUtils.validateSearchQuery(query)
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }
        
        _uiState.update { currentState ->
            val filteredCourses = StudentCoursesUtils.filterCourses(
                courses = currentState.allCourses,
                searchQuery = query,
                selectedCategory = currentState.selectedCategory,
                selectedDifficulty = currentState.selectedDifficulty,
                showOnlyEnrolled = currentState.showOnlyEnrolled
            )
            
            val sortedCourses = StudentCoursesUtils.sortCourses(
                courses = filteredCourses,
                sortBy = currentState.sortOption
            )
            
            currentState.copy(
                searchQuery = query,
                filteredCourses = sortedCourses
            )
        }
    }
    
    /**
     * 按类别过滤
     */
    private fun filterByCategory(category: String) {
        _uiState.update { currentState ->
            val filteredCourses = StudentCoursesUtils.filterCourses(
                courses = currentState.allCourses,
                searchQuery = currentState.searchQuery,
                selectedCategory = category,
                selectedDifficulty = currentState.selectedDifficulty,
                showOnlyEnrolled = currentState.showOnlyEnrolled
            )
            
            val sortedCourses = StudentCoursesUtils.sortCourses(
                courses = filteredCourses,
                sortBy = currentState.sortOption
            )
            
            currentState.copy(
                selectedCategory = category,
                filteredCourses = sortedCourses
            )
        }
    }
    
    /**
     * 按难度过滤
     */
    private fun filterByDifficulty(difficulty: String) {
        _uiState.update { currentState ->
            val filteredCourses = StudentCoursesUtils.filterCourses(
                courses = currentState.allCourses,
                searchQuery = currentState.searchQuery,
                selectedCategory = currentState.selectedCategory,
                selectedDifficulty = difficulty,
                showOnlyEnrolled = currentState.showOnlyEnrolled
            )
            
            val sortedCourses = StudentCoursesUtils.sortCourses(
                courses = filteredCourses,
                sortBy = currentState.sortOption
            )
            
            currentState.copy(
                selectedDifficulty = difficulty,
                filteredCourses = sortedCourses
            )
        }
    }
    
    /**
     * 排序课程
     */
    private fun sortCourses(sortOption: CourseSortOption) {
        _uiState.update { currentState ->
            val sortedCourses = StudentCoursesUtils.sortCourses(
                courses = currentState.filteredCourses,
                sortBy = sortOption
            )
            
            currentState.copy(
                sortOption = sortOption,
                filteredCourses = sortedCourses
            )
        }
    }
    
    /**
     * 切换只显示已注册课程
     */
    private fun toggleEnrolledOnly() {
        _uiState.update { currentState ->
            val newShowOnlyEnrolled = !currentState.showOnlyEnrolled
            
            val filteredCourses = StudentCoursesUtils.filterCourses(
                courses = currentState.allCourses,
                searchQuery = currentState.searchQuery,
                selectedCategory = currentState.selectedCategory,
                selectedDifficulty = currentState.selectedDifficulty,
                showOnlyEnrolled = newShowOnlyEnrolled
            )
            
            val sortedCourses = StudentCoursesUtils.sortCourses(
                courses = filteredCourses,
                sortBy = currentState.sortOption
            )
            
            currentState.copy(
                showOnlyEnrolled = newShowOnlyEnrolled,
                filteredCourses = sortedCourses
            )
        }
    }
    
    /**
     * 注册课程
     */
    private fun enrollCourse(courseId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isEnrolling = true, error = null) }
                
                val result = studentCoursesUseCase.enrollCourse(courseId)
                
                if (result.isSuccess) {
                    _uiState.update { it.copy(isEnrolling = false) }
                    // 重新加载课程数据以更新注册状态
                    loadCourses()
                    loadLearningStatistics()
                } else {
                    _uiState.update { 
                        it.copy(
                            isEnrolling = false,
                            error = result.exceptionOrNull()?.message ?: "注册课程失败"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isEnrolling = false,
                        error = "注册课程失败: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * 选择课程
     */
    private fun selectCourse(courseId: String) {
        _uiState.update { it.copy(selectedCourseId = courseId) }
    }
    
    /**
     * 清除错误
     */
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    /**
     * 重试上次操作
     */
    private fun retryLastAction() {
        val currentState = _uiState.value
        if (currentState.error != null) {
            clearError()
            // 重新加载数据
            loadCourses()
        }
    }
}

/**
 * 学生课程UI状态
 */
data class StudentCoursesUiState(
    val allCourses: List<CourseWithProgress> = emptyList(),
    val filteredCourses: List<CourseWithProgress> = emptyList(),
    val recommendedCourses: List<CourseWithProgress> = emptyList(),
    val continueLearningCourses: List<CourseWithProgress> = emptyList(),
    val learningStatistics: LearningStatistics? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "全部",
    val selectedDifficulty: String = "全部",
    val sortOption: CourseSortOption = CourseSortOption.CREATED_DESC,
    val showOnlyEnrolled: Boolean = false,
    val selectedCourseId: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isEnrolling: Boolean = false,
    val error: String? = null
) {
    /**
     * 获取可用的课程类别
     */
    fun getAvailableCategories(): List<String> {
        val categories = allCourses.map { it.course.category }.distinct().sorted()
        return listOf("全部") + categories
    }
    
    /**
     * 获取可用的课程难度
     */
    fun getAvailableDifficulties(): List<String> {
        val difficulties = allCourses.map { it.course.difficulty }.distinct()
        val sortedDifficulties = difficulties.sortedBy { difficulty ->
            when (difficulty.lowercase()) {
                "beginner", "初级" -> 1
                "intermediate", "中级" -> 2
                "advanced", "高级" -> 3
                "expert", "专家" -> 4
                else -> 5
            }
        }
        return listOf("全部") + sortedDifficulties
    }
    
    /**
     * 检查是否有数据
     */
    fun hasData(): Boolean {
        return allCourses.isNotEmpty()
    }
    
    /**
     * 检查是否有搜索结果
     */
    fun hasSearchResults(): Boolean {
        return filteredCourses.isNotEmpty()
    }
    
    /**
     * 检查是否正在搜索
     */
    fun isSearching(): Boolean {
        return searchQuery.isNotBlank()
    }
    
    /**
     * 检查是否有过滤条件
     */
    fun hasFilters(): Boolean {
        return selectedCategory != "全部" || 
               selectedDifficulty != "全部" || 
               showOnlyEnrolled
    }
}

/**
 * 学生课程用户意图
 */
sealed class StudentCoursesIntent {
    object LoadCourses : StudentCoursesIntent()
    object LoadRecommendedCourses : StudentCoursesIntent()
    object LoadContinueLearningCourses : StudentCoursesIntent()
    object LoadLearningStatistics : StudentCoursesIntent()
    object RefreshCourses : StudentCoursesIntent()
    data class SearchCourses(val query: String) : StudentCoursesIntent()
    data class FilterByCategory(val category: String) : StudentCoursesIntent()
    data class FilterByDifficulty(val difficulty: String) : StudentCoursesIntent()
    data class SortCourses(val sortOption: CourseSortOption) : StudentCoursesIntent()
    object ToggleEnrolledOnly : StudentCoursesIntent()
    data class EnrollCourse(val courseId: String) : StudentCoursesIntent()
    data class SelectCourse(val courseId: String) : StudentCoursesIntent()
    object ClearError : StudentCoursesIntent()
    object RetryLastAction : StudentCoursesIntent()
}