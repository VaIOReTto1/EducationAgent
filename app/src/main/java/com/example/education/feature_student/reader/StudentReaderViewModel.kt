package com.example.education.feature_student.reader

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.agent.AgentRepository
import com.example.education.agent.StudentContext
import com.example.education.core.database.dao.ChapterDao
import com.example.education.core.database.dao.CourseDao
import com.example.education.core.database.dao.LearningProgressDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 学生端阅读ViewModel
 * 
 * 管理章节内容和学习进度
 */
@HiltViewModel
class StudentReaderViewModel @Inject constructor(
    private val courseDao: CourseDao,
    private val chapterDao: ChapterDao,
    private val learningProgressDao: LearningProgressDao,
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "StudentReaderVM"
    }
    
    private val _uiState = MutableStateFlow(StudentReaderUiState())
    val uiState: StateFlow<StudentReaderUiState> = _uiState.asStateFlow()
    
    private var currentUserId = "user_001" // TODO: 从认证系统获取
    private var startTime = 0L
    
    /**
     * 加载章节内容
     */
    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            Log.d(TAG, "加载章节: $chapterId")
            
            _uiState.value = _uiState.value.copy(isLoading = true)
            startTime = System.currentTimeMillis()
            
            try {
                // 从数据库加载章节信息
                val chapterEntity = chapterDao.getChapterById(chapterId)
                if (chapterEntity != null) {
                    val chapterInfo = ChapterInfo(
                        id = chapterEntity.id,
                        title = chapterEntity.title,
                        content = chapterEntity.content ?: "",
                        duration = chapterEntity.durationMinutes ?: 0,
                        courseId = chapterEntity.courseId
                    )
                    
                    // 加载学习进度
                    val progressEntity = learningProgressDao.getProgress(currentUserId, chapterId)
                    val progress = progressEntity?.progressPercent ?: 0f
                    val timeSpent = progressEntity?.timeSpent ?: 0L
                    
                    // 生成AI推荐资源
                    val recommendedResources = generateRecommendedResources(chapterInfo)
                    
                    _uiState.value = _uiState.value.copy(
                        chapter = chapterInfo,
                        progress = progress / 100f,
                        timeSpent = timeSpent,
                        recommendedResources = recommendedResources,
                        isLoading = false
                    )
                    
                    Log.d(TAG, "章节加载完成: ${chapterInfo.title}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "章节不存在",
                        isLoading = false
                    )
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "加载章节失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "加载失败: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * 更新阅读进度
     */
    fun updateReadingProgress(progress: Float) {
        viewModelScope.launch {
            val chapter = _uiState.value.chapter ?: return@launch
            val currentTime = System.currentTimeMillis()
            val timeSpent = _uiState.value.timeSpent + (currentTime - startTime) / 1000
            
            Log.d(TAG, "更新阅读进度: ${(progress * 100).toInt()}%")
            
            try {
                // 更新数据库中的学习进度
                learningProgressDao.updateProgressByParams(
                    userId = currentUserId,
                    chapterId = chapter.id,
                    progressPercent = progress * 100,
                    timeSpent = timeSpent,
                    isCompleted = progress >= 0.9f
                )
                
                _uiState.value = _uiState.value.copy(
                    progress = progress,
                    timeSpent = timeSpent
                )
                
                // 重置开始时间
                startTime = currentTime
                
            } catch (e: Exception) {
                Log.e(TAG, "更新进度失败", e)
            }
        }
    }
    
    /**
     * 打开AI辅导
     */
    fun openAITutor() {
        Log.d(TAG, "打开AI辅导")
        // TODO: 导航到AI辅导页面
    }
    
    /**
     * 向AI提问
     */
    fun askAIQuestion(question: String) {
        viewModelScope.launch {
            val chapter = _uiState.value.chapter ?: return@launch
            
            Log.d(TAG, "向AI提问: $question")
            
            try {
                val context = StudentContext(
                    userId = currentUserId,
                    currentCourse = chapter.courseId,
                    currentChapter = chapter.id,
                    learningProgress = _uiState.value.progress,
                    difficultyLevel = "beginner",
                    learningStyle = "visual",
                    academicLevel = "beginner",
                    learningGoals = emptyList(),
                    completedChapters = emptyList(),
                    difficultyPreference = "medium",
                    studyTimeAvailable = 60,
                    currentCourseId = chapter.courseId,
                    currentChapterId = chapter.id,
                    preferredStyle = "visual"
                )
                
                // 调用辅导智能体
                agentRepository.startTutoring(
                    userId = currentUserId,
                    question = question,
                    conversationId = "${currentUserId}_tutoring",
                    studentLevel = context.difficultyLevel
                ).collect { event ->
                    // 处理AI回复
                    Log.d(TAG, "AI回复事件: $event")
                    // TODO: 显示AI回复或导航到聊天界面
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "AI提问失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "AI提问失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 打开笔记
     */
    fun openNotes() {
        Log.d(TAG, "打开笔记功能")
        // TODO: 实现笔记功能
    }
    
    /**
     * 开始练习测验
     */
    fun startPracticeQuiz() {
        viewModelScope.launch {
            val chapter = _uiState.value.chapter ?: return@launch
            
            Log.d(TAG, "开始练习测验")
            
            try {
                // 调用评估智能体生成练习题
                agentRepository.generateAssessment(
                    userId = currentUserId,
                    topic = chapter.title,
                    difficulty = "easy",
                    questionTypes = listOf("choice", "fill"),
                    conversationId = "${currentUserId}_assessment"
                ).collect { event ->
                    Log.d(TAG, "练习题生成事件: $event")
                    // TODO: 处理生成的练习题
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "生成练习题失败", e)
                _uiState.value = _uiState.value.copy(
                    error = "生成练习题失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 打开资源
     */
    fun openResource(resource: LearningResource) {
        Log.d(TAG, "打开资源: ${resource.title}")
        // TODO: 根据资源类型处理打开逻辑
    }
    
    /**
     * 导航返回
     */
    fun navigateBack() {
        Log.d(TAG, "返回上一页")
        // TODO: 实现导航返回
    }
    
    /**
     * 重试操作
     */
    fun retry() {
        val chapter = _uiState.value.chapter
        if (chapter != null) {
            loadChapter(chapter.id)
        }
    }
    
    /**
     * 生成AI推荐资源
     */
    private suspend fun generateRecommendedResources(chapter: ChapterInfo): List<LearningResource> {
        // 这里可以调用知识库智能体获取推荐资源
        // 目前返回模拟数据
        return listOf(
            LearningResource(
                id = "res_001",
                title = "深入理解${chapter.title}",
                description = "专业讲解视频，帮助加深理解",
                type = "video",
                url = "https://example.com/video1"
            ),
            LearningResource(
                id = "res_002", 
                title = "${chapter.title}练习题集",
                description = "配套练习题，巩固所学知识",
                type = "exercise",
                url = "https://example.com/quiz1"
            ),
            LearningResource(
                id = "res_003",
                title = "相关技术文档",
                description = "官方文档和最佳实践",
                type = "article",
                url = "https://example.com/doc1"
            )
        )
    }
}

/**
 * 学生阅读UI状态
 */
data class StudentReaderUiState(
    val chapter: ChapterInfo? = null,
    val progress: Float = 0f,
    val timeSpent: Long = 0L,
    val recommendedResources: List<LearningResource> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 章节信息
 */
data class ChapterInfo(
    val id: String,
    val title: String,
    val content: String,
    val duration: Int,
    val courseId: String
)

/**
 * 学习资源
 */
data class LearningResource(
    val id: String,
    val title: String,
    val description: String,
    val type: String, // video, article, exercise, link
    val url: String
) 