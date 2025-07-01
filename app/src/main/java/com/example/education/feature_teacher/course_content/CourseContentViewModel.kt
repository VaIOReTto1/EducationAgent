package com.example.education.feature_teacher.course_content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.database.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 课程内容管理ViewModel
 */
@HiltViewModel
class CourseContentViewModel @Inject constructor(
    private val courseContentUseCase: CourseContentUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CourseContentUiState())
    val uiState: StateFlow<CourseContentUiState> = _uiState.asStateFlow()
    
    /**
     * 处理用户意图
     */
    fun handleIntent(intent: CourseContentIntent) {
        when (intent) {
            is CourseContentIntent.LoadCourseContent -> loadCourseContent(intent.courseId)
            is CourseContentIntent.RefreshContent -> refreshContent()
            is CourseContentIntent.CreateChapter -> createChapter(intent.title, intent.content, intent.description)
            is CourseContentIntent.UpdateChapter -> updateChapter(intent.chapterId, intent.title, intent.content, intent.description)
            is CourseContentIntent.DeleteChapter -> deleteChapter(intent.chapterId)
            is CourseContentIntent.ReorderChapters -> reorderChapters(intent.chapterOrders)
            is CourseContentIntent.DuplicateChapter -> duplicateChapter(intent.chapterId, intent.newTitle)
            is CourseContentIntent.CreateAssessment -> createAssessment(intent.chapterId, intent.title, intent.description, intent.type, intent.questions, intent.timeLimit, intent.passingScore)
            is CourseContentIntent.UpdateAssessment -> updateAssessment(intent.assessmentId, intent.title, intent.description, intent.questions, intent.timeLimit, intent.passingScore)
            is CourseContentIntent.DeleteAssessment -> deleteAssessment(intent.assessmentId)
            is CourseContentIntent.ImportContent -> importContent(intent.importData)
            is CourseContentIntent.ExportContent -> exportContent()
            is CourseContentIntent.ShowCreateChapterDialog -> showCreateChapterDialog()
            is CourseContentIntent.HideCreateChapterDialog -> hideCreateChapterDialog()
            is CourseContentIntent.ShowEditChapterDialog -> showEditChapterDialog(intent.chapter)
            is CourseContentIntent.HideEditChapterDialog -> hideEditChapterDialog()
            is CourseContentIntent.ShowCreateAssessmentDialog -> showCreateAssessmentDialog(intent.chapterId)
            is CourseContentIntent.HideCreateAssessmentDialog -> hideCreateAssessmentDialog()
            is CourseContentIntent.ShowEditAssessmentDialog -> showEditAssessmentDialog(intent.assessment)
            is CourseContentIntent.HideEditAssessmentDialog -> hideEditAssessmentDialog()
            is CourseContentIntent.ShowDeleteConfirmDialog -> showDeleteConfirmDialog(intent.itemType, intent.itemId, intent.itemTitle)
            is CourseContentIntent.HideDeleteConfirmDialog -> hideDeleteConfirmDialog()
            is CourseContentIntent.ShowImportDialog -> showImportDialog()
            is CourseContentIntent.HideImportDialog -> hideImportDialog()
            is CourseContentIntent.ShowExportDialog -> showExportDialog()
            is CourseContentIntent.HideExportDialog -> hideExportDialog()
            is CourseContentIntent.ShowStatisticsDialog -> showStatisticsDialog()
            is CourseContentIntent.HideStatisticsDialog -> hideStatisticsDialog()
            is CourseContentIntent.SelectChapter -> selectChapter(intent.chapter)
            is CourseContentIntent.SelectAssessment -> selectAssessment(intent.assessment)
            is CourseContentIntent.ClearSelection -> clearSelection()
            is CourseContentIntent.ClearError -> clearError()
            is CourseContentIntent.RetryLastAction -> retryLastAction()
        }
    }
    
    /**
     * 加载课程内容
     */
    private fun loadCourseContent(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, courseId = courseId) }
            
            // 监听课程内容变化
            courseContentUseCase.getCourseContentFlow(courseId)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "加载课程内容失败"
                        ) 
                    }
                }
                .collect { courseContentDetail ->
                    if (courseContentDetail != null) {
                        // 加载统计信息
                        val statisticsResult = courseContentUseCase.getContentStatistics(courseId)
                        val statistics = statisticsResult.getOrNull()
                        
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                courseContentDetail = courseContentDetail,
                                statistics = statistics,
                                error = null
                            ) 
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                error = "课程不存在"
                            ) 
                        }
                    }
                }
        }
    }
    
    /**
     * 刷新内容
     */
    private fun refreshContent() {
        val currentCourseId = _uiState.value.courseId
        if (currentCourseId != null) {
            loadCourseContent(currentCourseId)
        }
    }
    
    /**
     * 创建章节
     */
    private fun createChapter(title: String, content: String, description: String) {
        val courseId = _uiState.value.courseId ?: return
        val currentChapters = _uiState.value.courseContentDetail?.chapters ?: emptyList()
        val nextOrder = (currentChapters.maxOfOrNull { it.order } ?: 0) + 1
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.createChapter(
                courseId = courseId,
                title = title,
                content = content,
                order = nextOrder,
                description = description
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showCreateChapterDialog = false
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "创建章节失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 更新章节
     */
    private fun updateChapter(chapterId: String, title: String, content: String, description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.updateChapter(
                chapterId = chapterId,
                title = title,
                content = content,
                description = description
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showEditChapterDialog = false,
                            selectedChapter = null
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "更新章节失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 删除章节
     */
    private fun deleteChapter(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.deleteChapter(chapterId)
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showDeleteConfirmDialog = false,
                            selectedChapter = null
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "删除章节失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 重新排序章节
     */
    private fun reorderChapters(chapterOrders: List<Pair<String, Int>>) {
        val courseId = _uiState.value.courseId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.reorderChapters(courseId, chapterOrders)
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "重新排序失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 复制章节
     */
    private fun duplicateChapter(chapterId: String, newTitle: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.duplicateChapter(chapterId, newTitle)
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "复制章节失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 创建评估
     */
    private fun createAssessment(
        chapterId: String?,
        title: String,
        description: String,
        type: AssessmentType,
        questions: List<AssessmentQuestion>,
        timeLimit: Int?,
        passingScore: Int
    ) {
        val courseId = _uiState.value.courseId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.createAssessment(
                courseId = courseId,
                chapterId = chapterId,
                title = title,
                description = description,
                type = type,
                questions = questions,
                timeLimit = timeLimit,
                passingScore = passingScore
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showCreateAssessmentDialog = false
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "创建评估失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 更新评估
     */
    private fun updateAssessment(
        assessmentId: String,
        title: String,
        description: String,
        questions: List<AssessmentQuestion>,
        timeLimit: Int?,
        passingScore: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.updateAssessment(
                assessmentId = assessmentId,
                title = title,
                description = description,
                questions = questions,
                timeLimit = timeLimit,
                passingScore = passingScore
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showEditAssessmentDialog = false,
                            selectedAssessment = null
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "更新评估失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 删除评估
     */
    private fun deleteAssessment(assessmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.deleteAssessment(assessmentId)
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showDeleteConfirmDialog = false,
                            selectedAssessment = null
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "删除评估失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 导入内容
     */
    private fun importContent(importData: CourseContentImportData) {
        val courseId = _uiState.value.courseId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.importCourseContent(courseId, importData)
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showImportDialog = false
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "导入内容失败"
                        ) 
                    }
                }
            )
        }
    }
    
    /**
     * 导出内容
     */
    private fun exportContent() {
        val courseId = _uiState.value.courseId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = courseContentUseCase.exportCourseContent(courseId)
            
            result.fold(
                onSuccess = { exportData ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            exportData = exportData,
                            showExportDialog = false
                        ) 
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = exception.message ?: "导出内容失败"
                        ) 
                    }
                }
            )
        }
    }
    
    // Dialog 显示/隐藏方法
    private fun showCreateChapterDialog() {
        _uiState.update { it.copy(showCreateChapterDialog = true) }
    }
    
    private fun hideCreateChapterDialog() {
        _uiState.update { it.copy(showCreateChapterDialog = false) }
    }
    
    private fun showEditChapterDialog(chapter: Chapter) {
        _uiState.update { 
            it.copy(
                showEditChapterDialog = true,
                selectedChapter = chapter
            ) 
        }
    }
    
    private fun hideEditChapterDialog() {
        _uiState.update { 
            it.copy(
                showEditChapterDialog = false,
                selectedChapter = null
            ) 
        }
    }
    
    private fun showCreateAssessmentDialog(chapterId: String?) {
        _uiState.update { 
            it.copy(
                showCreateAssessmentDialog = true,
                selectedChapterId = chapterId
            ) 
        }
    }
    
    private fun hideCreateAssessmentDialog() {
        _uiState.update { 
            it.copy(
                showCreateAssessmentDialog = false,
                selectedChapterId = null
            ) 
        }
    }
    
    private fun showEditAssessmentDialog(assessment: Assessment) {
        _uiState.update { 
            it.copy(
                showEditAssessmentDialog = true,
                selectedAssessment = assessment
            ) 
        }
    }
    
    private fun hideEditAssessmentDialog() {
        _uiState.update { 
            it.copy(
                showEditAssessmentDialog = false,
                selectedAssessment = null
            ) 
        }
    }
    
    private fun showDeleteConfirmDialog(itemType: String, itemId: String, itemTitle: String) {
        _uiState.update { 
            it.copy(
                showDeleteConfirmDialog = true,
                deleteItemType = itemType,
                deleteItemId = itemId,
                deleteItemTitle = itemTitle
            ) 
        }
    }
    
    private fun hideDeleteConfirmDialog() {
        _uiState.update { 
            it.copy(
                showDeleteConfirmDialog = false,
                deleteItemType = null,
                deleteItemId = null,
                deleteItemTitle = null
            ) 
        }
    }
    
    private fun showImportDialog() {
        _uiState.update { it.copy(showImportDialog = true) }
    }
    
    private fun hideImportDialog() {
        _uiState.update { it.copy(showImportDialog = false) }
    }
    
    private fun showExportDialog() {
        _uiState.update { it.copy(showExportDialog = true) }
    }
    
    private fun hideExportDialog() {
        _uiState.update { it.copy(showExportDialog = false) }
    }
    
    private fun showStatisticsDialog() {
        _uiState.update { it.copy(showStatisticsDialog = true) }
    }
    
    private fun hideStatisticsDialog() {
        _uiState.update { it.copy(showStatisticsDialog = false) }
    }
    
    private fun selectChapter(chapter: Chapter) {
        _uiState.update { it.copy(selectedChapter = chapter) }
    }
    
    private fun selectAssessment(assessment: Assessment) {
        _uiState.update { it.copy(selectedAssessment = assessment) }
    }
    
    private fun clearSelection() {
        _uiState.update { 
            it.copy(
                selectedChapter = null,
                selectedAssessment = null
            ) 
        }
    }
    
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    private fun retryLastAction() {
        // 重试上次操作的逻辑
        val currentCourseId = _uiState.value.courseId
        if (currentCourseId != null) {
            loadCourseContent(currentCourseId)
        }
    }
}

/**
 * UI状态
 */
data class CourseContentUiState(
    val isLoading: Boolean = false,
    val courseId: String? = null,
    val courseContentDetail: CourseContentDetail? = null,
    val statistics: ContentStatistics? = null,
    val error: String? = null,
    val exportData: CourseContentExportData? = null,
    
    // 选中状态
    val selectedChapter: Chapter? = null,
    val selectedAssessment: Assessment? = null,
    val selectedChapterId: String? = null,
    
    // 对话框状态
    val showCreateChapterDialog: Boolean = false,
    val showEditChapterDialog: Boolean = false,
    val showCreateAssessmentDialog: Boolean = false,
    val showEditAssessmentDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showImportDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showStatisticsDialog: Boolean = false,
    
    // 删除确认对话框相关
    val deleteItemType: String? = null,
    val deleteItemId: String? = null,
    val deleteItemTitle: String? = null
)

/**
 * 用户意图
 */
sealed class CourseContentIntent {
    data class LoadCourseContent(val courseId: String) : CourseContentIntent()
    object RefreshContent : CourseContentIntent()
    
    // 章节操作
    data class CreateChapter(val title: String, val content: String, val description: String) : CourseContentIntent()
    data class UpdateChapter(val chapterId: String, val title: String, val content: String, val description: String) : CourseContentIntent()
    data class DeleteChapter(val chapterId: String) : CourseContentIntent()
    data class ReorderChapters(val chapterOrders: List<Pair<String, Int>>) : CourseContentIntent()
    data class DuplicateChapter(val chapterId: String, val newTitle: String?) : CourseContentIntent()
    
    // 评估操作
    data class CreateAssessment(
        val chapterId: String?,
        val title: String,
        val description: String,
        val type: AssessmentType,
        val questions: List<AssessmentQuestion>,
        val timeLimit: Int?,
        val passingScore: Int
    ) : CourseContentIntent()
    
    data class UpdateAssessment(
        val assessmentId: String,
        val title: String,
        val description: String,
        val questions: List<AssessmentQuestion>,
        val timeLimit: Int?,
        val passingScore: Int
    ) : CourseContentIntent()
    
    data class DeleteAssessment(val assessmentId: String) : CourseContentIntent()
    
    // 导入导出
    data class ImportContent(val importData: CourseContentImportData) : CourseContentIntent()
    object ExportContent : CourseContentIntent()
    
    // 对话框控制
    object ShowCreateChapterDialog : CourseContentIntent()
    object HideCreateChapterDialog : CourseContentIntent()
    data class ShowEditChapterDialog(val chapter: Chapter) : CourseContentIntent()
    object HideEditChapterDialog : CourseContentIntent()
    data class ShowCreateAssessmentDialog(val chapterId: String?) : CourseContentIntent()
    object HideCreateAssessmentDialog : CourseContentIntent()
    data class ShowEditAssessmentDialog(val assessment: Assessment) : CourseContentIntent()
    object HideEditAssessmentDialog : CourseContentIntent()
    data class ShowDeleteConfirmDialog(val itemType: String, val itemId: String, val itemTitle: String) : CourseContentIntent()
    object HideDeleteConfirmDialog : CourseContentIntent()
    object ShowImportDialog : CourseContentIntent()
    object HideImportDialog : CourseContentIntent()
    object ShowExportDialog : CourseContentIntent()
    object HideExportDialog : CourseContentIntent()
    object ShowStatisticsDialog : CourseContentIntent()
    object HideStatisticsDialog : CourseContentIntent()
    
    // 选择操作
    data class SelectChapter(val chapter: Chapter) : CourseContentIntent()
    data class SelectAssessment(val assessment: Assessment) : CourseContentIntent()
    object ClearSelection : CourseContentIntent()
    
    // 错误处理
    object ClearError : CourseContentIntent()
    object RetryLastAction : CourseContentIntent()
}