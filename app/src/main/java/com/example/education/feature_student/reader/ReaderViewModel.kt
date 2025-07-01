package com.example.education.feature_student.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.database.entity.ChapterEntity
import com.example.education.core.database.entity.LearningProgressEntity
import com.example.education.core.network.model.StreamChatMessageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 阅读器ViewModel
 * 实现MVI架构的状态管理
 */
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val readerUseCase: ReaderUseCase
) : ViewModel() {

    // 私有状态流
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    // AI回复流
    private val _aiResponseFlow = MutableSharedFlow<StreamChatMessageResponse>()
    val aiResponseFlow: SharedFlow<StreamChatMessageResponse> = _aiResponseFlow.asSharedFlow()

    /**
     * 处理用户意图
     */
    fun handleIntent(intent: ReaderIntent) {
        when (intent) {
            is ReaderIntent.LoadChapter -> loadChapter(intent.chapterId, intent.courseId)
            is ReaderIntent.UpdateProgress -> updateProgress(intent.progress, intent.timeSpent)
            is ReaderIntent.AskQuestion -> askQuestion(intent.question)
            is ReaderIntent.RequestAdvice -> requestLearningAdvice()
            is ReaderIntent.RequestPractice -> requestPracticeQuestions()
            is ReaderIntent.NavigateToNext -> navigateToNextChapter()
            is ReaderIntent.NavigateToPrevious -> navigateToPreviousChapter()
            is ReaderIntent.ToggleTableOfContents -> toggleTableOfContents()
            is ReaderIntent.JumpToSection -> jumpToSection(intent.sectionId)
            is ReaderIntent.ToggleAiAssistant -> toggleAiAssistant()
            is ReaderIntent.MarkAsCompleted -> markChapterAsCompleted()
            is ReaderIntent.DismissError -> dismissError()
            is ReaderIntent.RetryLoad -> retryLoad()
        }
    }

    /**
     * 加载章节内容
     */
    private fun loadChapter(chapterId: String, courseId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                // 加载章节信息
                val chapter = readerUseCase.getChapterById(chapterId)
                if (chapter == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "章节不存在"
                    )
                    return@launch
                }

                // 加载学习进度
                val progress = readerUseCase.getLearningProgress(chapterId, courseId)

                // 生成目录
                val tableOfContents = ReaderUtils.generateTableOfContents(chapter.content)

                // 估算阅读时间
                val estimatedReadingTime = ReaderUtils.estimateReadingTime(chapter.content)

                // 获取下一章和上一章
                val nextChapter = readerUseCase.getNextChapter(courseId, chapter.orderIndex)
                val previousChapter = readerUseCase.getPreviousChapter(courseId, chapter.orderIndex)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentChapter = chapter,
                    courseId = courseId,
                    learningProgress = progress,
                    tableOfContents = tableOfContents,
                    estimatedReadingTime = estimatedReadingTime,
                    hasNextChapter = nextChapter != null,
                    hasPreviousChapter = previousChapter != null,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载章节失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 更新学习进度
     */
    private fun updateProgress(progress: Float, timeSpent: Int) {
        val currentState = _uiState.value
        val chapter = currentState.currentChapter ?: return
        val courseId = currentState.courseId ?: return

        viewModelScope.launch {
            try {
                readerUseCase.updateLearningProgress(
                    chapterId = chapter.id,
                    courseId = courseId,
                    progressPercentage = progress,
                    timeSpentMinutes = timeSpent
                )

                // 重新加载进度
                val updatedProgress = readerUseCase.getLearningProgress(chapter.id, courseId)
                _uiState.value = _uiState.value.copy(
                    learningProgress = updatedProgress
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "更新进度失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 提问AI助手
     */
    private fun askQuestion(question: String) {
        val currentChapter = _uiState.value.currentChapter ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isAiThinking = true,
                    aiError = null
                )

                readerUseCase.requestTutoring(
                    question = question,
                    chapterContent = currentChapter.content,
                    conversationId = _uiState.value.conversationId
                ).collect { response ->
                    _aiResponseFlow.emit(response)
                    
                    // 如果是最后一条消息，更新会话ID
                    if (response.event == "message_end") {
                        _uiState.value = _uiState.value.copy(
                            conversationId = response.conversationId,
                            isAiThinking = false
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAiThinking = false,
                    aiError = "AI助手暂时不可用: ${e.message}"
                )
            }
        }
    }

    /**
     * 请求学习建议
     */
    private fun requestLearningAdvice() {
        val currentState = _uiState.value
        val chapter = currentState.currentChapter ?: return
        val progress = currentState.learningProgress

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isAiThinking = true,
                    aiError = null
                )

                readerUseCase.requestLearningAdvice(chapter, progress)
                    .collect { response ->
                        _aiResponseFlow.emit(response)
                        
                        if (response.event == "message_end") {
                            _uiState.value = _uiState.value.copy(
                                conversationId = response.conversationId,
                                isAiThinking = false
                            )
                        }
                    }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAiThinking = false,
                    aiError = "获取学习建议失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 请求练习题
     */
    private fun requestPracticeQuestions() {
        val chapter = _uiState.value.currentChapter ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isAiThinking = true,
                    aiError = null
                )

                readerUseCase.requestPracticeQuestions(chapter.title)
                    .collect { response ->
                        _aiResponseFlow.emit(response)
                        
                        if (response.event == "message_end") {
                            _uiState.value = _uiState.value.copy(
                                conversationId = response.conversationId,
                                isAiThinking = false
                            )
                        }
                    }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAiThinking = false,
                    aiError = "获取练习题失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 导航到下一章
     */
    private fun navigateToNextChapter() {
        val currentState = _uiState.value
        val chapter = currentState.currentChapter ?: return
        val courseId = currentState.courseId ?: return

        viewModelScope.launch {
            val nextChapter = readerUseCase.getNextChapter(courseId, chapter.orderIndex)
            nextChapter?.let {
                loadChapter(it.id, courseId)
            }
        }
    }

    /**
     * 导航到上一章
     */
    private fun navigateToPreviousChapter() {
        val currentState = _uiState.value
        val chapter = currentState.currentChapter ?: return
        val courseId = currentState.courseId ?: return

        viewModelScope.launch {
            val previousChapter = readerUseCase.getPreviousChapter(courseId, chapter.orderIndex)
            previousChapter?.let {
                loadChapter(it.id, courseId)
            }
        }
    }

    /**
     * 切换目录显示
     */
    private fun toggleTableOfContents() {
        _uiState.value = _uiState.value.copy(
            showTableOfContents = !_uiState.value.showTableOfContents
        )
    }

    /**
     * 跳转到指定章节
     */
    private fun jumpToSection(sectionId: String) {
        _uiState.value = _uiState.value.copy(
            jumpToSectionId = sectionId,
            showTableOfContents = false
        )
    }

    /**
     * 切换AI助手显示
     */
    private fun toggleAiAssistant() {
        _uiState.value = _uiState.value.copy(
            showAiAssistant = !_uiState.value.showAiAssistant
        )
    }

    /**
     * 标记章节为已完成
     */
    private fun markChapterAsCompleted() {
        val currentState = _uiState.value
        val chapter = currentState.currentChapter ?: return
        val courseId = currentState.courseId ?: return

        viewModelScope.launch {
            try {
                readerUseCase.markChapterAsCompleted(chapter.id, courseId)
                
                // 重新加载进度
                val updatedProgress = readerUseCase.getLearningProgress(chapter.id, courseId)
                _uiState.value = _uiState.value.copy(
                    learningProgress = updatedProgress
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "标记完成失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 清除错误状态
     */
    private fun dismissError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            aiError = null
        )
    }

    /**
     * 重试加载
     */
    private fun retryLoad() {
        val currentState = _uiState.value
        val chapter = currentState.currentChapter
        val courseId = currentState.courseId
        
        if (chapter != null && courseId != null) {
            loadChapter(chapter.id, courseId)
        }
    }

    /**
     * 清除跳转标记
     */
    fun clearJumpToSection() {
        _uiState.value = _uiState.value.copy(
            jumpToSectionId = null
        )
    }
}

/**
 * 阅读器UI状态
 */
data class ReaderUiState(
    val isLoading: Boolean = false,
    val currentChapter: ChapterEntity? = null,
    val courseId: String? = null,
    val learningProgress: LearningProgressEntity? = null,
    val tableOfContents: List<TocItem> = emptyList(),
    val estimatedReadingTime: Int = 0,
    val hasNextChapter: Boolean = false,
    val hasPreviousChapter: Boolean = false,
    val showTableOfContents: Boolean = false,
    val showAiAssistant: Boolean = false,
    val isAiThinking: Boolean = false,
    val conversationId: String? = null,
    val jumpToSectionId: String? = null,
    val error: String? = null,
    val aiError: String? = null
)

/**
 * 阅读器用户意图
 */
sealed class ReaderIntent {
    data class LoadChapter(val chapterId: String, val courseId: String) : ReaderIntent()
    data class UpdateProgress(val progress: Float, val timeSpent: Int) : ReaderIntent()
    data class AskQuestion(val question: String) : ReaderIntent()
    object RequestAdvice : ReaderIntent()
    object RequestPractice : ReaderIntent()
    object NavigateToNext : ReaderIntent()
    object NavigateToPrevious : ReaderIntent()
    object ToggleTableOfContents : ReaderIntent()
    data class JumpToSection(val sectionId: String) : ReaderIntent()
    object ToggleAiAssistant : ReaderIntent()
    object MarkAsCompleted : ReaderIntent()
    object DismissError : ReaderIntent()
    object RetryLoad : ReaderIntent()
}