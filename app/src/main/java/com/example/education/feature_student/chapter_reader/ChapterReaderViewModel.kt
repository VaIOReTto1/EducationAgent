package com.example.education.feature_student.chapter_reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * 章节阅读页面的ViewModel
 * 实现MVI架构模式
 */
@HiltViewModel
class ChapterReaderViewModel @Inject constructor(
    private val chapterReaderUseCase: ChapterReaderUseCase,
    private val utils: ChapterReaderUtils
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(ChapterReaderUiState())
    val uiState: StateFlow<ChapterReaderUiState> = _uiState.asStateFlow()
    
    // 当前学生ID（应该从用户会话中获取）
    private var currentStudentId: String = "student_001" // 临时硬编码
    
    // 阅读进度更新任务
    private var progressUpdateJob: Job? = null
    
    // 阅读时间计时器
    private var readingTimer: Job? = null
    private var sessionStartTime: Long = 0L
    private var totalSessionTime: Long = 0L
    
    /**
     * 处理用户意图
     */
    fun handleIntent(intent: ChapterReaderIntent) {
        when (intent) {
            is ChapterReaderIntent.LoadChapter -> loadChapter(intent.chapterId)
            is ChapterReaderIntent.RefreshChapter -> refreshChapter()
            is ChapterReaderIntent.UpdateReadingProgress -> updateReadingProgress(intent.progress)
            is ChapterReaderIntent.MarkChapterCompleted -> markChapterCompleted()
            is ChapterReaderIntent.NavigateToPrevious -> navigateToPrevious()
            is ChapterReaderIntent.NavigateToNext -> navigateToNext()
            is ChapterReaderIntent.ToggleOutline -> toggleOutline()
            is ChapterReaderIntent.SearchInChapter -> searchInChapter(intent.query)
            is ChapterReaderIntent.ClearSearch -> clearSearch()
            is ChapterReaderIntent.AddNote -> addNote(intent.content, intent.position)
            is ChapterReaderIntent.LoadNotes -> loadNotes()
            is ChapterReaderIntent.ToggleNotes -> toggleNotes()
            is ChapterReaderIntent.StartReading -> startReading()
            is ChapterReaderIntent.PauseReading -> pauseReading()
            is ChapterReaderIntent.ResumeReading -> resumeReading()
            is ChapterReaderIntent.ToggleSettings -> toggleSettings()
            is ChapterReaderIntent.UpdateFontSize -> updateFontSize(intent.fontSize)
            is ChapterReaderIntent.UpdateTheme -> updateTheme(intent.isDarkTheme)
            is ChapterReaderIntent.ClearError -> clearError()
            is ChapterReaderIntent.RetryLastAction -> retryLastAction()
        }
    }
    
    /**
     * 加载章节
     */
    private fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // 获取章节详情
                val result = chapterReaderUseCase.getChapterDetail(chapterId, currentStudentId)
                
                result.fold(
                    onSuccess = { chapterDetail ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                chapterDetail = chapterDetail,
                                currentChapterId = chapterId,
                                readingProgress = chapterDetail.readingProgress,
                                isCompleted = chapterDetail.isCompleted,
                                lastAction = ChapterReaderIntent.LoadChapter(chapterId)
                            )
                        }
                        
                        // 加载大纲
                        loadOutline(chapterId)
                        
                        // 加载笔记
                        loadNotes()
                        
                        // 开始阅读计时
                        startReading()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "加载章节失败",
                                lastAction = ChapterReaderIntent.LoadChapter(chapterId)
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载章节失败",
                        lastAction = ChapterReaderIntent.LoadChapter(chapterId)
                    )
                }
            }
        }
    }
    
    /**
     * 刷新章节
     */
    private fun refreshChapter() {
        val currentChapterId = _uiState.value.currentChapterId
        if (currentChapterId.isNotEmpty()) {
            loadChapter(currentChapterId)
        }
    }
    
    /**
     * 更新阅读进度
     */
    private fun updateReadingProgress(progress: Float) {
        val currentState = _uiState.value
        val chapterDetail = currentState.chapterDetail ?: return
        
        // 立即更新UI状态
        _uiState.update {
            it.copy(
                readingProgress = progress,
                isCompleted = progress >= 1.0f
            )
        }
        
        // 取消之前的更新任务
        progressUpdateJob?.cancel()
        
        // 延迟更新数据库（避免频繁写入）
        progressUpdateJob = viewModelScope.launch {
            delay(1000) // 延迟1秒
            
            try {
                chapterReaderUseCase.updateReadingProgress(
                    studentId = currentStudentId,
                    courseId = chapterDetail.course.id,
                    chapterId = chapterDetail.chapter.id,
                    progress = progress,
                    timeSpent = totalSessionTime
                )
            } catch (e: Exception) {
                // 静默处理错误，不影响用户体验
            }
        }
    }
    
    /**
     * 标记章节为已完成
     */
    private fun markChapterCompleted() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val chapterDetail = currentState.chapterDetail ?: return@launch
            
            _uiState.update { it.copy(isUpdating = true) }
            
            try {
                val result = chapterReaderUseCase.markChapterCompleted(
                    studentId = currentStudentId,
                    courseId = chapterDetail.course.id,
                    chapterId = chapterDetail.chapter.id,
                    totalTimeSpent = chapterDetail.timeSpent + totalSessionTime
                )
                
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                readingProgress = 1.0f,
                                isCompleted = true
                            )
                        }
                        
                        // 暂停阅读计时
                        pauseReading()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                error = error.message ?: "标记完成失败"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        error = e.message ?: "标记完成失败"
                    )
                }
            }
        }
    }
    
    /**
     * 导航到上一章节
     */
    private fun navigateToPrevious() {
        val chapterDetail = _uiState.value.chapterDetail
        val previousChapter = chapterDetail?.previousChapter
        
        if (previousChapter != null) {
            // 保存当前进度
            saveCurrentProgress()
            
            // 加载上一章节
            loadChapter(previousChapter.id)
        }
    }
    
    /**
     * 导航到下一章节
     */
    private fun navigateToNext() {
        val chapterDetail = _uiState.value.chapterDetail
        val nextChapter = chapterDetail?.nextChapter
        
        if (nextChapter != null) {
            // 保存当前进度
            saveCurrentProgress()
            
            // 加载下一章节
            loadChapter(nextChapter.id)
        }
    }
    
    /**
     * 切换大纲显示
     */
    private fun toggleOutline() {
        _uiState.update {
            it.copy(isOutlineVisible = !it.isOutlineVisible)
        }
    }
    
    /**
     * 加载大纲
     */
    private fun loadOutline(chapterId: String) {
        viewModelScope.launch {
            try {
                val result = chapterReaderUseCase.getChapterOutline(chapterId)
                result.fold(
                    onSuccess = { outline ->
                        _uiState.update { it.copy(outline = outline) }
                    },
                    onFailure = {
                        // 静默处理错误
                    }
                )
            } catch (e: Exception) {
                // 静默处理错误
            }
        }
    }
    
    /**
     * 在章节中搜索
     */
    private fun searchInChapter(query: String) {
        if (query.isBlank()) {
            clearSearch()
            return
        }
        
        viewModelScope.launch {
            val chapterId = _uiState.value.currentChapterId
            if (chapterId.isEmpty()) return@launch
            
            _uiState.update { it.copy(isSearching = true) }
            
            try {
                val result = chapterReaderUseCase.searchInChapter(chapterId, query)
                result.fold(
                    onSuccess = { searchResults ->
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                searchQuery = query,
                                searchResults = searchResults
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                error = error.message ?: "搜索失败"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        error = e.message ?: "搜索失败"
                    )
                }
            }
        }
    }
    
    /**
     * 清除搜索
     */
    private fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }
    
    /**
     * 添加笔记
     */
    private fun addNote(content: String, position: Int?) {
        if (!utils.validateNoteContent(content)) {
            _uiState.update { it.copy(error = "笔记内容不能为空且不能超过1000字符") }
            return
        }
        
        viewModelScope.launch {
            val chapterId = _uiState.value.currentChapterId
            if (chapterId.isEmpty()) return@launch
            
            _uiState.update { it.copy(isAddingNote = true) }
            
            try {
                val result = chapterReaderUseCase.addChapterNote(
                    studentId = currentStudentId,
                    chapterId = chapterId,
                    content = content,
                    position = position
                )
                
                result.fold(
                    onSuccess = { note ->
                        _uiState.update {
                            it.copy(
                                isAddingNote = false,
                                notes = it.notes + note
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isAddingNote = false,
                                error = error.message ?: "添加笔记失败"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAddingNote = false,
                        error = e.message ?: "添加笔记失败"
                    )
                }
            }
        }
    }
    
    /**
     * 加载笔记
     */
    private fun loadNotes() {
        viewModelScope.launch {
            val chapterId = _uiState.value.currentChapterId
            if (chapterId.isEmpty()) return@launch
            
            try {
                val result = chapterReaderUseCase.getChapterNotes(currentStudentId, chapterId)
                result.fold(
                    onSuccess = { notes ->
                        _uiState.update { it.copy(notes = notes) }
                    },
                    onFailure = {
                        // 静默处理错误
                    }
                )
            } catch (e: Exception) {
                // 静默处理错误
            }
        }
    }
    
    /**
     * 切换笔记显示
     */
    private fun toggleNotes() {
        _uiState.update {
            it.copy(isNotesVisible = !it.isNotesVisible)
        }
    }
    
    /**
     * 开始阅读计时
     */
    private fun startReading() {
        if (_uiState.value.isReading) return
        
        sessionStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isReading = true) }
        
        readingTimer = viewModelScope.launch {
            while (_uiState.value.isReading) {
                delay(1000) // 每秒更新一次
                totalSessionTime = System.currentTimeMillis() - sessionStartTime
            }
        }
    }
    
    /**
     * 暂停阅读计时
     */
    private fun pauseReading() {
        _uiState.update { it.copy(isReading = false) }
        readingTimer?.cancel()
        
        // 保存当前进度
        saveCurrentProgress()
    }
    
    /**
     * 恢复阅读计时
     */
    private fun resumeReading() {
        startReading()
    }
    
    /**
     * 切换设置显示
     */
    private fun toggleSettings() {
        _uiState.update {
            it.copy(isSettingsVisible = !it.isSettingsVisible)
        }
    }
    
    /**
     * 更新字体大小
     */
    private fun updateFontSize(fontSize: Float) {
        _uiState.update {
            it.copy(fontSize = fontSize)
        }
    }
    
    /**
     * 更新主题
     */
    private fun updateTheme(isDarkTheme: Boolean) {
        _uiState.update {
            it.copy(isDarkTheme = isDarkTheme)
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
        val lastAction = _uiState.value.lastAction
        if (lastAction != null) {
            handleIntent(lastAction)
        }
    }
    
    /**
     * 保存当前进度
     */
    private fun saveCurrentProgress() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val chapterDetail = currentState.chapterDetail ?: return@launch
            
            try {
                chapterReaderUseCase.updateReadingProgress(
                    studentId = currentStudentId,
                    courseId = chapterDetail.course.id,
                    chapterId = chapterDetail.chapter.id,
                    progress = currentState.readingProgress,
                    timeSpent = totalSessionTime
                )
            } catch (e: Exception) {
                // 静默处理错误
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // 清理资源
        progressUpdateJob?.cancel()
        readingTimer?.cancel()
        
        // 保存最终进度
        saveCurrentProgress()
    }
}

/**
 * UI状态数据类
 */
data class ChapterReaderUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isSearching: Boolean = false,
    val isAddingNote: Boolean = false,
    val isReading: Boolean = false,
    val chapterDetail: ChapterDetail? = null,
    val currentChapterId: String = "",
    val readingProgress: Float = 0f,
    val isCompleted: Boolean = false,
    val outline: List<OutlineItem> = emptyList(),
    val isOutlineVisible: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val notes: List<ChapterNote> = emptyList(),
    val isNotesVisible: Boolean = false,
    val isSettingsVisible: Boolean = false,
    val fontSize: Float = 16f,
    val isDarkTheme: Boolean = false,
    val error: String? = null,
    val lastAction: ChapterReaderIntent? = null
)

/**
 * 用户意图密封类
 */
sealed class ChapterReaderIntent {
    data class LoadChapter(val chapterId: String) : ChapterReaderIntent()
    object RefreshChapter : ChapterReaderIntent()
    data class UpdateReadingProgress(val progress: Float) : ChapterReaderIntent()
    object MarkChapterCompleted : ChapterReaderIntent()
    object NavigateToPrevious : ChapterReaderIntent()
    object NavigateToNext : ChapterReaderIntent()
    object ToggleOutline : ChapterReaderIntent()
    data class SearchInChapter(val query: String) : ChapterReaderIntent()
    object ClearSearch : ChapterReaderIntent()
    data class AddNote(val content: String, val position: Int? = null) : ChapterReaderIntent()
    object LoadNotes : ChapterReaderIntent()
    object ToggleNotes : ChapterReaderIntent()
    object StartReading : ChapterReaderIntent()
    object PauseReading : ChapterReaderIntent()
    object ResumeReading : ChapterReaderIntent()
    object ToggleSettings : ChapterReaderIntent()
    data class UpdateFontSize(val fontSize: Float) : ChapterReaderIntent()
    data class UpdateTheme(val isDarkTheme: Boolean) : ChapterReaderIntent()
    object ClearError : ChapterReaderIntent()
    object RetryLastAction : ChapterReaderIntent()
}