package com.example.education.feature_ai_learning

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.education.core.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AI学习助手页面的ViewModel
 */
@HiltViewModel
class AILearningViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(AILearningUiState())
    val uiState: StateFlow<AILearningUiState> = _uiState.asStateFlow()
    
    private var currentRole: UserRole = UserRole.STUDENT
    
    /**
     * 为指定角色加载数据
     */
    fun loadDataForRole(role: UserRole) {
        currentRole = role
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
                delay(800)
                
                val aiAssistants = getAIAssistantsForRole(currentRole)
                val learningTools = getLearningToolsForRole(currentRole)
                val recentActivities = getRecentActivitiesForRole(currentRole)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    aiAssistants = aiAssistants,
                    learningTools = learningTools,
                    recentActivities = recentActivities
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
     * 根据角色获取AI助手
     */
    private fun getAIAssistantsForRole(role: UserRole): List<AIAssistant> {
        return when (role) {
            UserRole.TEACHER -> getTeacherAIAssistants()
            UserRole.STUDENT -> getStudentAIAssistants()
        }
    }
    
    /**
     * 获取教师AI助手
     */
    private fun getTeacherAIAssistants(): List<AIAssistant> {
        return listOf(
            AIAssistant(
                id = "lesson_planner",
                name = "课程规划师",
                description = "智能生成教学计划和课程大纲",
                type = "lesson_planning",
                icon = Icons.Default.CalendarMonth,
                capabilities = listOf("教学计划", "课程设计", "进度安排", "资源推荐"),
                featureId = "lesson_planning"
            ),
            AIAssistant(
                id = "content_creator",
                name = "内容创作者",
                description = "协助创建教学材料和课件",
                type = "content_creation",
                icon = Icons.Default.Create,
                capabilities = listOf("课件制作", "习题生成", "素材整理", "多媒体编辑"),
                featureId = "content_creation"
            ),
            AIAssistant(
                id = "assessment_designer",
                name = "评估设计师",
                description = "设计个性化的学生评估方案",
                type = "assessment_design",
                icon = Icons.Default.Assignment,
                capabilities = listOf("试卷生成", "评分标准", "学情分析", "反馈建议"),
                featureId = "assessment_design"
            ),
            AIAssistant(
                id = "classroom_manager",
                name = "课堂管理师",
                description = "优化课堂教学和学生管理",
                type = "classroom_management",
                icon = Icons.Default.Groups,
                capabilities = listOf("课堂互动", "纪律管理", "参与度分析", "氛围营造"),
                featureId = "classroom_management"
            ),
            AIAssistant(
                id = "parent_communicator",
                name = "家校沟通师",
                description = "协助与家长的有效沟通",
                type = "parent_communication",
                icon = Icons.Default.Message,
                capabilities = listOf("沟通模板", "进度报告", "问题反馈", "建议方案"),
                featureId = "parent_communication"
            )
        )
    }
    
    /**
     * 获取学生AI助手
     */
    private fun getStudentAIAssistants(): List<AIAssistant> {
        return listOf(
            AIAssistant(
                id = "study_buddy",
                name = "学习伙伴",
                description = "陪伴你的个性化学习助手",
                type = "study_companion",
                icon = Icons.Default.School,
                capabilities = listOf("答疑解惑", "学习计划", "知识梳理", "学习激励"),
                featureId = "study_companion"
            ),
            AIAssistant(
                id = "homework_helper",
                name = "作业助手",
                description = "智能辅导作业和练习",
                type = "homework_assistance",
                icon = Icons.Default.Assignment,
                capabilities = listOf("题目解析", "步骤指导", "错误纠正", "举一反三"),
                featureId = "homework_assistance"
            ),
            AIAssistant(
                id = "reading_coach",
                name = "阅读教练",
                description = "提升阅读理解和写作能力",
                type = "reading_writing",
                icon = Icons.Default.MenuBook,
                capabilities = listOf("阅读指导", "写作辅导", "词汇扩展", "文本分析"),
                featureId = "reading_writing"
            ),
            AIAssistant(
                id = "exam_trainer",
                name = "考试训练师",
                description = "针对性的考试准备和训练",
                type = "exam_preparation",
                icon = Icons.Default.Quiz,
                capabilities = listOf("模拟考试", "弱点分析", "复习计划", "应试技巧"),
                featureId = "exam_preparation"
            ),
            AIAssistant(
                id = "motivation_coach",
                name = "动力教练",
                description = "激发学习兴趣和保持动力",
                type = "motivation",
                icon = Icons.Default.EmojiEvents,
                capabilities = listOf("目标设定", "进度跟踪", "成就激励", "习惯养成"),
                featureId = "motivation"
            )
        )
    }
    
    /**
     * 根据角色获取学习工具
     */
    private fun getLearningToolsForRole(role: UserRole): List<LearningTool> {
        return when (role) {
            UserRole.TEACHER -> getTeacherTools()
            UserRole.STUDENT -> getStudentTools()
        }
    }
    
    /**
     * 获取教师工具
     */
    private fun getTeacherTools(): List<LearningTool> {
        return listOf(
            LearningTool(
                id = "smart_whiteboard",
                name = "智能白板",
                description = "AI增强的互动式教学白板",
                icon = Icons.Default.Dashboard,
                category = "教学工具"
            ),
            LearningTool(
                id = "student_analytics",
                name = "学情分析",
                description = "深度分析学生学习数据",
                icon = Icons.Default.Analytics,
                category = "数据分析"
            ),
            LearningTool(
                id = "resource_library",
                name = "资源库",
                description = "海量教学资源智能推荐",
                icon = Icons.Default.LibraryBooks,
                category = "资源管理"
            ),
            LearningTool(
                id = "collaboration_space",
                name = "协作空间",
                description = "师生互动协作平台",
                icon = Icons.Default.GroupWork,
                category = "协作工具"
            )
        )
    }
    
    /**
     * 获取学生工具
     */
    private fun getStudentTools(): List<LearningTool> {
        return listOf(
            LearningTool(
                id = "smart_notes",
                name = "智能笔记",
                description = "AI辅助的智能笔记系统",
                icon = Icons.Default.Note,
                category = "学习工具"
            ),
            LearningTool(
                id = "practice_arena",
                name = "练习场",
                description = "个性化练习和模拟测试",
                icon = Icons.Default.FitnessCenter,
                category = "练习工具"
            ),
            LearningTool(
                id = "knowledge_map",
                name = "知识地图",
                description = "可视化知识结构和学习路径",
                icon = Icons.Default.AccountTree,
                category = "知识管理"
            ),
            LearningTool(
                id = "study_timer",
                name = "学习计时器",
                description = "番茄工作法和专注力训练",
                icon = Icons.Default.Timer,
                category = "时间管理"
            ),
            LearningTool(
                id = "progress_tracker",
                name = "进度跟踪",
                description = "学习进度可视化和目标管理",
                icon = Icons.Default.TrendingUp,
                category = "进度管理"
            )
        )
    }
    
    /**
     * 根据角色获取最近活动
     */
    private fun getRecentActivitiesForRole(role: UserRole): List<RecentActivity> {
        return when (role) {
            UserRole.TEACHER -> getTeacherRecentActivities()
            UserRole.STUDENT -> getStudentRecentActivities()
        }
    }
    
    /**
     * 获取教师最近活动
     */
    private fun getTeacherRecentActivities(): List<RecentActivity> {
        return listOf(
            RecentActivity(
                id = "lesson_plan_math",
                title = "数学课程计划",
                description = "小学三年级数学第五单元教学计划",
                timestamp = "2小时前",
                featureId = "lesson_planning",
                type = "lesson_plan"
            ),
            RecentActivity(
                id = "assessment_chinese",
                title = "语文测评设计",
                description = "阅读理解能力评估方案",
                timestamp = "昨天",
                featureId = "assessment_design",
                type = "assessment"
            ),
            RecentActivity(
                id = "content_science",
                title = "科学课件制作",
                description = "植物生长观察实验课件",
                timestamp = "3天前",
                featureId = "content_creation",
                type = "content"
            )
        )
    }
    
    /**
     * 获取学生最近活动
     */
    private fun getStudentRecentActivities(): List<RecentActivity> {
        return listOf(
            RecentActivity(
                id = "math_practice",
                title = "数学练习",
                description = "分数运算专项练习 - 进度75%",
                timestamp = "30分钟前",
                featureId = "practice_arena",
                type = "practice"
            ),
            RecentActivity(
                id = "reading_notes",
                title = "阅读笔记",
                description = "《小王子》读书笔记整理",
                timestamp = "2小时前",
                featureId = "smart_notes",
                type = "notes"
            ),
            RecentActivity(
                id = "english_vocab",
                title = "英语单词",
                description = "第三单元词汇复习计划",
                timestamp = "昨天",
                featureId = "study_companion",
                type = "vocabulary"
            )
        )
    }
}

/**
 * AI学习助手UI状态
 */
data class AILearningUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiAssistants: List<AIAssistant> = emptyList(),
    val learningTools: List<LearningTool> = emptyList(),
    val recentActivities: List<RecentActivity> = emptyList()
)

/**
 * AI助手数据模型
 */
data class AIAssistant(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val icon: ImageVector,
    val capabilities: List<String>,
    val featureId: String
)

/**
 * 学习工具数据模型
 */
data class LearningTool(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val category: String
)

/**
 * 最近活动数据模型
 */
data class RecentActivity(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val featureId: String,
    val type: String
)