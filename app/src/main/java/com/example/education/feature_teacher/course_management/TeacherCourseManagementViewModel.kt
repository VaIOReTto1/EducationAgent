package com.example.education.feature_teacher.course_management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.database.entity.Course
import com.example.education.core.database.entity.Chapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 教师课程管理页面的ViewModel
 * 实现MVI架构模式
 */
@HiltViewModel
class TeacherCourseManagementViewModel @Inject constructor(
    private val useCase: TeacherCourseManagementUseCase,
    private val utils: TeacherCourseManagementUtils
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(TeacherCourseManagementUiState())
    val uiState: StateFlow<TeacherCourseManagementUiState> = _uiState.asStateFlow()
    
    // 用户意图处理
    fun handleIntent(intent: TeacherCourseManagementIntent) {
        when (intent) {
            is TeacherCourseManagementIntent.LoadCourses -> loadCourses()
            is TeacherCourseManagementIntent.RefreshCourses -> refreshCourses()
            is TeacherCourseManagementIntent.SearchCourses -> searchCourses(intent.query)
            is TeacherCourseManagementIntent.FilterByCategory -> filterByCategory(intent.category)
            is TeacherCourseManagementIntent.FilterByDifficulty -> filterByDifficulty(intent.difficulty)
            is TeacherCourseManagementIntent.FilterByStatus -> filterByStatus(intent.status)
            is TeacherCourseManagementIntent.SortCourses -> sortCourses(intent.sortOption)
            is TeacherCourseManagementIntent.SelectCourse -> selectCourse(intent.courseId)
            is TeacherCourseManagementIntent.CreateCourse -> createCourse(intent.course)
            is TeacherCourseManagementIntent.UpdateCourse -> updateCourse(intent.course)
            is TeacherCourseManagementIntent.PublishCourse -> publishCourse(intent.courseId)
            is TeacherCourseManagementIntent.DeleteCourse -> deleteCourse(intent.courseId)
            is TeacherCourseManagementIntent.AddChapter -> addChapter(intent.courseId, intent.chapter)
            is TeacherCourseManagementIntent.UpdateChapter -> updateChapter(intent.chapter)
            is TeacherCourseManagementIntent.DeleteChapter -> deleteChapter(intent.chapterId)
            is TeacherCourseManagementIntent.LoadCourseStudents -> loadCourseStudents(intent.courseId)
            is TeacherCourseManagementIntent.RemoveStudent -> removeStudent(intent.courseId, intent.studentId)
            is TeacherCourseManagementIntent.ShowCreateCourseDialog -> showCreateCourseDialog()
            is TeacherCourseManagementIntent.HideCreateCourseDialog -> hideCreateCourseDialog()
            is TeacherCourseManagementIntent.ShowEditCourseDialog -> showEditCourseDialog(intent.course)
            is TeacherCourseManagementIntent.HideEditCourseDialog -> hideEditCourseDialog()
            is TeacherCourseManagementIntent.ShowCourseDetail -> showCourseDetail(intent.courseId)
            is TeacherCourseManagementIntent.HideCourseDetail -> hideCourseDetail()
            is TeacherCourseManagementIntent.ShowStudentList -> showStudentList(intent.courseId)
            is TeacherCourseManagementIntent.HideStudentList -> hideStudentList()
            is TeacherCourseManagementIntent.ClearError -> clearError()
            is TeacherCourseManagementIntent.RetryLastAction -> retryLastAction()
        }
    }
    
    init {
        loadCourses()
    }
    
    /**
     * 加载课程列表
     */
    private fun loadCourses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                useCase.getTeacherCourses().collect { courses ->
                    val filteredCourses = applyFilters(courses)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            courses = courses,
                            filteredCourses = filteredCourses,
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
     * 刷新课程列表
     */
    private fun refreshCourses() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadCourses()
        _uiState.update { it.copy(isRefreshing = false) }
    }
    
    /**
     * 搜索课程
     */
    private fun searchCourses(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredCourses = applyFilters(it.courses)
            )
        }
    }
    
    /**
     * 按类别过滤
     */
    private fun filterByCategory(category: String) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                filteredCourses = applyFilters(it.courses)
            )
        }
    }
    
    /**
     * 按难度过滤
     */
    private fun filterByDifficulty(difficulty: String) {
        _uiState.update {
            it.copy(
                selectedDifficulty = difficulty,
                filteredCourses = applyFilters(it.courses)
            )
        }
    }
    
    /**
     * 按状态过滤
     */
    private fun filterByStatus(status: String) {
        _uiState.update {
            it.copy(
                selectedStatus = status,
                filteredCourses = applyFilters(it.courses)
            )
        }
    }
    
    /**
     * 排序课程
     */
    private fun sortCourses(sortOption: CourseSortOption) {
        _uiState.update {
            it.copy(
                sortOption = sortOption,
                filteredCourses = utils.sortCourses(it.filteredCourses, sortOption)
            )
        }
    }
    
    /**
     * 选择课程
     */
    private fun selectCourse(courseId: String) {
        _uiState.update { it.copy(selectedCourseId = courseId) }
    }
    
    /**
     * 创建课程
     */
    private fun createCourse(course: Course) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCourse = true) }
            
            try {
                // 验证课程数据
                val validationError = utils.validateCourseData(
                    course.title,
                    course.description,
                    course.category,
                    course.difficulty
                )
                
                if (validationError != null) {
                    _uiState.update {
                        it.copy(
                            isCreatingCourse = false,
                            error = validationError
                        )
                    }
                    return@launch
                }
                
                useCase.createCourse(course)
                _uiState.update {
                    it.copy(
                        isCreatingCourse = false,
                        showCreateCourseDialog = false,
                        error = null
                    )
                }
                
                // 重新加载课程列表
                loadCourses()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingCourse = false,
                        error = "创建课程失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 更新课程
     */
    private fun updateCourse(course: Course) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingCourse = true) }
            
            try {
                // 验证课程数据
                val validationError = utils.validateCourseData(
                    course.title,
                    course.description,
                    course.category,
                    course.difficulty
                )
                
                if (validationError != null) {
                    _uiState.update {
                        it.copy(
                            isUpdatingCourse = false,
                            error = validationError
                        )
                    }
                    return@launch
                }
                
                useCase.updateCourse(course)
                _uiState.update {
                    it.copy(
                        isUpdatingCourse = false,
                        showEditCourseDialog = false,
                        error = null
                    )
                }
                
                // 重新加载课程列表
                loadCourses()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatingCourse = false,
                        error = "更新课程失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 发布课程
     */
    private fun publishCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPublishingCourse = true) }
            
            try {
                useCase.publishCourse(courseId)
                _uiState.update {
                    it.copy(
                        isPublishingCourse = false,
                        error = null
                    )
                }
                
                // 重新加载课程列表
                loadCourses()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isPublishingCourse = false,
                        error = "发布课程失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 删除课程
     */
    private fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingCourse = true) }
            
            try {
                useCase.deleteCourse(courseId)
                _uiState.update {
                    it.copy(
                        isDeletingCourse = false,
                        error = null
                    )
                }
                
                // 重新加载课程列表
                loadCourses()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeletingCourse = false,
                        error = "删除课程失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 添加章节
     */
    private fun addChapter(courseId: String, chapter: Chapter) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingChapter = true) }
            
            try {
                // 验证章节数据
                val validationError = utils.validateChapterData(
                    chapter.title,
                    chapter.content
                )
                
                if (validationError != null) {
                    _uiState.update {
                        it.copy(
                            isAddingChapter = false,
                            error = validationError
                        )
                    }
                    return@launch
                }
                
                useCase.addChapter(courseId, chapter)
                _uiState.update {
                    it.copy(
                        isAddingChapter = false,
                        error = null
                    )
                }
                
                // 如果当前显示课程详情，重新加载
                if (uiState.value.showCourseDetail && uiState.value.selectedCourseDetail?.course?.id == courseId) {
                    loadCourseDetail(courseId)
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAddingChapter = false,
                        error = "添加章节失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 更新章节
     */
    private fun updateChapter(chapter: Chapter) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingChapter = true) }
            
            try {
                // 验证章节数据
                val validationError = utils.validateChapterData(
                    chapter.title,
                    chapter.content
                )
                
                if (validationError != null) {
                    _uiState.update {
                        it.copy(
                            isUpdatingChapter = false,
                            error = validationError
                        )
                    }
                    return@launch
                }
                
                useCase.updateChapter(chapter)
                _uiState.update {
                    it.copy(
                        isUpdatingChapter = false,
                        error = null
                    )
                }
                
                // 如果当前显示课程详情，重新加载
                if (uiState.value.showCourseDetail && uiState.value.selectedCourseDetail?.course?.id == chapter.courseId) {
                    loadCourseDetail(chapter.courseId)
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatingChapter = false,
                        error = "更新章节失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 删除章节
     */
    private fun deleteChapter(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingChapter = true) }
            
            try {
                useCase.deleteChapter(chapterId)
                _uiState.update {
                    it.copy(
                        isDeletingChapter = false,
                        error = null
                    )
                }
                
                // 如果当前显示课程详情，重新加载
                uiState.value.selectedCourseDetail?.let { courseDetail ->
                    loadCourseDetail(courseDetail.course.id)
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeletingChapter = false,
                        error = "删除章节失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 加载课程学生列表
     */
    private fun loadCourseStudents(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStudents = true) }
            
            try {
                val students = useCase.getCourseStudents(courseId)
                _uiState.update {
                    it.copy(
                        isLoadingStudents = false,
                        courseStudents = students,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingStudents = false,
                        error = "加载学生列表失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 移除学生
     */
    private fun removeStudent(courseId: String, studentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRemovingStudent = true) }
            
            try {
                useCase.removeStudentFromCourse(courseId, studentId)
                _uiState.update {
                    it.copy(
                        isRemovingStudent = false,
                        error = null
                    )
                }
                
                // 重新加载学生列表
                loadCourseStudents(courseId)
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRemovingStudent = false,
                        error = "移除学生失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 显示创建课程对话框
     */
    private fun showCreateCourseDialog() {
        _uiState.update { it.copy(showCreateCourseDialog = true) }
    }
    
    /**
     * 隐藏创建课程对话框
     */
    private fun hideCreateCourseDialog() {
        _uiState.update { it.copy(showCreateCourseDialog = false) }
    }
    
    /**
     * 显示编辑课程对话框
     */
    private fun showEditCourseDialog(course: Course) {
        _uiState.update {
            it.copy(
                showEditCourseDialog = true,
                editingCourse = course
            )
        }
    }
    
    /**
     * 隐藏编辑课程对话框
     */
    private fun hideEditCourseDialog() {
        _uiState.update {
            it.copy(
                showEditCourseDialog = false,
                editingCourse = null
            )
        }
    }
    
    /**
     * 显示课程详情
     */
    private fun showCourseDetail(courseId: String) {
        _uiState.update { it.copy(showCourseDetail = true) }
        loadCourseDetail(courseId)
    }
    
    /**
     * 隐藏课程详情
     */
    private fun hideCourseDetail() {
        _uiState.update {
            it.copy(
                showCourseDetail = false,
                selectedCourseDetail = null
            )
        }
    }
    
    /**
     * 显示学生列表
     */
    private fun showStudentList(courseId: String) {
        _uiState.update { it.copy(showStudentList = true) }
        loadCourseStudents(courseId)
    }
    
    /**
     * 隐藏学生列表
     */
    private fun hideStudentList() {
        _uiState.update {
            it.copy(
                showStudentList = false,
                courseStudents = emptyList()
            )
        }
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
        // 重新加载课程列表
        loadCourses()
    }
    
    /**
     * 加载课程详情
     */
    private fun loadCourseDetail(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCourseDetail = true) }
            
            try {
                val courseDetail = useCase.getCourseDetail(courseId)
                _uiState.update {
                    it.copy(
                        isLoadingCourseDetail = false,
                        selectedCourseDetail = courseDetail,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingCourseDetail = false,
                        error = "加载课程详情失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 应用过滤条件
     */
    private fun applyFilters(courses: List<TeacherCourseDetail>): List<TeacherCourseDetail> {
        val state = uiState.value
        val filtered = utils.filterCourses(
            courses,
            state.searchQuery,
            state.selectedCategory,
            state.selectedDifficulty,
            state.selectedStatus
        )
        return utils.sortCourses(filtered, state.sortOption)
    }
}

/**
 * UI状态数据类
 */
data class TeacherCourseManagementUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val courses: List<TeacherCourseDetail> = emptyList(),
    val filteredCourses: List<TeacherCourseDetail> = emptyList(),
    val selectedCourseId: String? = null,
    val selectedCourseDetail: TeacherCourseDetail? = null,
    val courseStudents: List<StudentProgress> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "全部",
    val selectedDifficulty: String = "全部",
    val selectedStatus: String = "全部",
    val sortOption: CourseSortOption = CourseSortOption.UPDATED_DESC,
    val showCreateCourseDialog: Boolean = false,
    val showEditCourseDialog: Boolean = false,
    val showCourseDetail: Boolean = false,
    val showStudentList: Boolean = false,
    val editingCourse: Course? = null,
    val isCreatingCourse: Boolean = false,
    val isUpdatingCourse: Boolean = false,
    val isPublishingCourse: Boolean = false,
    val isDeletingCourse: Boolean = false,
    val isAddingChapter: Boolean = false,
    val isUpdatingChapter: Boolean = false,
    val isDeletingChapter: Boolean = false,
    val isLoadingCourseDetail: Boolean = false,
    val isLoadingStudents: Boolean = false,
    val isRemovingStudent: Boolean = false,
    val error: String? = null
)

/**
 * 用户意图密封类
 */
sealed class TeacherCourseManagementIntent {
    object LoadCourses : TeacherCourseManagementIntent()
    object RefreshCourses : TeacherCourseManagementIntent()
    data class SearchCourses(val query: String) : TeacherCourseManagementIntent()
    data class FilterByCategory(val category: String) : TeacherCourseManagementIntent()
    data class FilterByDifficulty(val difficulty: String) : TeacherCourseManagementIntent()
    data class FilterByStatus(val status: String) : TeacherCourseManagementIntent()
    data class SortCourses(val sortOption: CourseSortOption) : TeacherCourseManagementIntent()
    data class SelectCourse(val courseId: String) : TeacherCourseManagementIntent()
    data class CreateCourse(val course: Course) : TeacherCourseManagementIntent()
    data class UpdateCourse(val course: Course) : TeacherCourseManagementIntent()
    data class PublishCourse(val courseId: String) : TeacherCourseManagementIntent()
    data class DeleteCourse(val courseId: String) : TeacherCourseManagementIntent()
    data class AddChapter(val courseId: String, val chapter: Chapter) : TeacherCourseManagementIntent()
    data class UpdateChapter(val chapter: Chapter) : TeacherCourseManagementIntent()
    data class DeleteChapter(val chapterId: String) : TeacherCourseManagementIntent()
    data class LoadCourseStudents(val courseId: String) : TeacherCourseManagementIntent()
    data class RemoveStudent(val courseId: String, val studentId: String) : TeacherCourseManagementIntent()
    object ShowCreateCourseDialog : TeacherCourseManagementIntent()
    object HideCreateCourseDialog : TeacherCourseManagementIntent()
    data class ShowEditCourseDialog(val course: Course) : TeacherCourseManagementIntent()
    object HideEditCourseDialog : TeacherCourseManagementIntent()
    data class ShowCourseDetail(val courseId: String) : TeacherCourseManagementIntent()
    object HideCourseDetail : TeacherCourseManagementIntent()
    data class ShowStudentList(val courseId: String) : TeacherCourseManagementIntent()
    object HideStudentList : TeacherCourseManagementIntent()
    object ClearError : TeacherCourseManagementIntent()
    object RetryLastAction : TeacherCourseManagementIntent()
}