package com.example.education.feature_student.courses

import androidx.compose.ui.graphics.Color
import com.example.education.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 学生课程工具类
 * 提供数据格式化和辅助功能
 */
object StudentCoursesUtils {
    
    /**
     * 格式化学习时间
     */
    fun formatLearningTime(minutes: Long): String {
        return when {
            minutes < 60 -> "${minutes}分钟"
            minutes < 1440 -> {
                val hours = minutes / 60
                val remainingMinutes = minutes % 60
                if (remainingMinutes == 0L) "${hours}小时" else "${hours}小时${remainingMinutes}分钟"
            }
            else -> {
                val days = minutes / 1440
                val remainingHours = (minutes % 1440) / 60
                if (remainingHours == 0L) "${days}天" else "${days}天${remainingHours}小时"
            }
        }
    }
    
    /**
     * 格式化进度百分比
     */
    fun formatProgress(progress: Float): String {
        return "${progress.roundToInt()}%"
    }
    
    /**
     * 获取进度颜色
     */
    fun getProgressColor(progress: Float): Color {
        return when {
            progress >= 80f -> ProgressExcellent
            progress >= 60f -> ProgressGood
            progress >= 40f -> ProgressAverage
            progress >= 20f -> ProgressPoor
            else -> ProgressVeryPoor
        }
    }
    
    /**
     * 格式化相对时间
     */
    fun formatRelativeTime(timestamp: Long?): String {
        if (timestamp == null) return "从未学习"
        
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "刚刚学习"
            diff < 3_600_000 -> "${diff / 60_000}分钟前"
            diff < 86_400_000 -> "${diff / 3_600_000}小时前"
            diff < 604_800_000 -> "${diff / 86_400_000}天前"
            else -> {
                val formatter = SimpleDateFormat("MM月dd日", Locale.getDefault())
                formatter.format(Date(timestamp))
            }
        }
    }
    
    /**
     * 格式化日期时间
     */
    fun formatDateTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * 格式化日期
     */
    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("MM月dd日", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * 获取课程难度颜色
     */
    fun getDifficultyColor(difficulty: String): Color {
        return when (difficulty.lowercase()) {
            "beginner", "初级" -> DifficultyBeginner
            "intermediate", "中级" -> DifficultyIntermediate
            "advanced", "高级" -> DifficultyAdvanced
            "expert", "专家" -> DifficultyExpert
            else -> DifficultyBeginner
        }
    }
    
    /**
     * 获取课程难度文本
     */
    fun getDifficultyText(difficulty: String): String {
        return when (difficulty.lowercase()) {
            "beginner" -> "初级"
            "intermediate" -> "中级"
            "advanced" -> "高级"
            "expert" -> "专家"
            else -> difficulty
        }
    }
    
    /**
     * 获取课程类别颜色
     */
    fun getCategoryColor(category: String): Color {
        return when (category.lowercase()) {
            "math", "数学" -> CategoryMath
            "science", "科学" -> CategoryScience
            "language", "语言" -> CategoryLanguage
            "history", "历史" -> CategoryHistory
            "art", "艺术" -> CategoryArt
            "technology", "技术" -> CategoryTechnology
            else -> CategoryOther
        }
    }
    
    /**
     * 获取评分颜色
     */
    fun getScoreColor(score: Float): Color {
        return when {
            score >= 90f -> ScoreExcellent
            score >= 80f -> ScoreGood
            score >= 70f -> ScoreAverage
            score >= 60f -> ScorePoor
            else -> ScoreVeryPoor
        }
    }
    
    /**
     * 获取评分等级
     */
    fun getScoreGrade(score: Float): String {
        return when {
            score >= 90f -> "优秀"
            score >= 80f -> "良好"
            score >= 70f -> "中等"
            score >= 60f -> "及格"
            else -> "不及格"
        }
    }
    
    /**
     * 计算完成率百分比
     */
    fun calculateCompletionRate(completed: Int, total: Int): Float {
        return if (total > 0) (completed.toFloat() / total.toFloat()) * 100f else 0f
    }
    
    /**
     * 格式化章节数量
     */
    fun formatChapterCount(count: Int): String {
        return when {
            count == 0 -> "暂无章节"
            count == 1 -> "1个章节"
            else -> "${count}个章节"
        }
    }
    
    /**
     * 格式化评估数量
     */
    fun formatAssessmentCount(count: Int): String {
        return when {
            count == 0 -> "暂无评估"
            count == 1 -> "1个评估"
            else -> "${count}个评估"
        }
    }
    
    /**
     * 获取学习状态文本
     */
    fun getLearningStatusText(courseWithProgress: CourseWithProgress): String {
        return when {
            !courseWithProgress.isEnrolled -> "未注册"
            courseWithProgress.isCompleted() -> "已完成"
            courseWithProgress.averageProgress > 0f -> "学习中"
            else -> "未开始"
        }
    }
    
    /**
     * 获取学习状态颜色
     */
    fun getLearningStatusColor(courseWithProgress: CourseWithProgress): Color {
        return when {
            !courseWithProgress.isEnrolled -> Color.Gray
            courseWithProgress.isCompleted() -> ProgressExcellent
            courseWithProgress.averageProgress > 0f -> ProgressGood
            else -> ProgressPoor
        }
    }
    
    /**
     * 生成课程进度摘要
     */
    fun generateProgressSummary(courseWithProgress: CourseWithProgress): String {
        return if (courseWithProgress.isEnrolled) {
            val progressText = formatProgress(courseWithProgress.averageProgress)
            val chapterText = "${courseWithProgress.completedChapters}/${courseWithProgress.totalChapters}章节"
            val timeText = formatLearningTime(courseWithProgress.totalTimeSpent)
            "进度$progressText，完成$chapterText，学习$timeText"
        } else {
            "点击注册开始学习"
        }
    }
    
    /**
     * 生成学习统计摘要
     */
    fun generateStatisticsSummary(statistics: LearningStatistics): String {
        val courseText = "注册${statistics.enrolledCourses}门课程"
        val completedText = "完成${statistics.completedCourses}门"
        val timeText = "学习${formatLearningTime(statistics.totalTimeSpent)}"
        return "$courseText，$completedText，$timeText"
    }
    
    /**
     * 获取推荐理由
     */
    fun getRecommendationReason(course: com.example.education.core.database.entity.CourseEntity): String {
        return when {
            course.category == "数学" -> "基于您的数学学习历史推荐"
            course.category == "科学" -> "科学爱好者的热门选择"
            course.category == "语言" -> "提升语言技能的优质课程"
            course.category == "历史" -> "历史文化知识拓展"
            course.category == "艺术" -> "培养艺术修养和创造力"
            course.category == "技术" -> "掌握前沿技术技能"
            else -> "为您精心推荐"
        }
    }
    
    /**
     * 估算课程学习时间
     */
    fun estimateLearningTime(chapterCount: Int): String {
        val estimatedMinutes = chapterCount * 30 // 假设每章节30分钟
        return formatLearningTime(estimatedMinutes.toLong())
    }
    
    /**
     * 获取课程标签
     */
    fun getCourseTags(course: com.example.education.core.database.entity.CourseEntity): List<String> {
        val tags = mutableListOf<String>()
        
        // 添加难度标签
        tags.add(getDifficultyText(course.difficulty))
        
        // 添加类别标签
        tags.add(course.category)
        
        // 根据创建时间添加标签
        val daysSinceCreated = (System.currentTimeMillis() - course.createdAt) / (24 * 60 * 60 * 1000)
        if (daysSinceCreated <= 7) {
            tags.add("新课程")
        }
        
        // 根据更新时间添加标签
        val daysSinceUpdated = (System.currentTimeMillis() - course.updatedAt) / (24 * 60 * 60 * 1000)
        if (daysSinceUpdated <= 3) {
            tags.add("最近更新")
        }
        
        return tags
    }
    
    /**
     * 过滤课程列表
     */
    fun filterCourses(
        courses: List<CourseWithProgress>,
        searchQuery: String,
        selectedCategory: String,
        selectedDifficulty: String,
        showOnlyEnrolled: Boolean
    ): List<CourseWithProgress> {
        return courses.filter { courseWithProgress ->
            val course = courseWithProgress.course
            
            // 搜索过滤
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                course.title.contains(searchQuery, ignoreCase = true) ||
                course.description.contains(searchQuery, ignoreCase = true) ||
                course.category.contains(searchQuery, ignoreCase = true)
            }
            
            // 类别过滤
            val matchesCategory = selectedCategory == "全部" || course.category == selectedCategory
            
            // 难度过滤
            val matchesDifficulty = selectedDifficulty == "全部" || course.difficulty == selectedDifficulty
            
            // 注册状态过滤
            val matchesEnrollment = !showOnlyEnrolled || courseWithProgress.isEnrolled
            
            matchesSearch && matchesCategory && matchesDifficulty && matchesEnrollment
        }
    }
    
    /**
     * 排序课程列表
     */
    fun sortCourses(
        courses: List<CourseWithProgress>,
        sortBy: CourseSortOption
    ): List<CourseWithProgress> {
        return when (sortBy) {
            CourseSortOption.TITLE_ASC -> courses.sortedBy { it.course.title }
            CourseSortOption.TITLE_DESC -> courses.sortedByDescending { it.course.title }
            CourseSortOption.CREATED_ASC -> courses.sortedBy { it.course.createdAt }
            CourseSortOption.CREATED_DESC -> courses.sortedByDescending { it.course.createdAt }
            CourseSortOption.PROGRESS_ASC -> courses.sortedBy { it.averageProgress }
            CourseSortOption.PROGRESS_DESC -> courses.sortedByDescending { it.averageProgress }
            CourseSortOption.LAST_ACCESS_ASC -> courses.sortedBy { it.lastAccessTime ?: 0L }
            CourseSortOption.LAST_ACCESS_DESC -> courses.sortedByDescending { it.lastAccessTime ?: 0L }
        }
    }
    
    /**
     * 验证搜索查询
     */
    fun validateSearchQuery(query: String): String? {
        return when {
            query.length > 100 -> "搜索关键词不能超过100个字符"
            query.contains(Regex("[<>\"'&]")) -> "搜索关键词包含非法字符"
            else -> null
        }
    }
    
    /**
     * 生成课程分享文本
     */
    fun generateShareText(course: com.example.education.core.database.entity.CourseEntity): String {
        return "我正在学习《${course.title}》，这是一门${getDifficultyText(course.difficulty)}的${course.category}课程。${course.description}"
    }
}

/**
 * 课程排序选项
 */
enum class CourseSortOption(val displayName: String) {
    TITLE_ASC("标题 A-Z"),
    TITLE_DESC("标题 Z-A"),
    CREATED_ASC("创建时间 ↑"),
    CREATED_DESC("创建时间 ↓"),
    PROGRESS_ASC("进度 ↑"),
    PROGRESS_DESC("进度 ↓"),
    LAST_ACCESS_ASC("最近访问 ↑"),
    LAST_ACCESS_DESC("最近访问 ↓")
}