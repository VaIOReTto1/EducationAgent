package com.example.education.feature_teacher.dashboard

import androidx.compose.ui.graphics.Color
import com.example.education.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 教师仪表盘工具类
 * 提供数据格式化和辅助功能
 */
object TeacherDashboardUtils {
    
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
        if (timestamp == null) return "从未访问"
        
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "刚刚"
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
     * 格式化学生数量
     */
    fun formatStudentCount(count: Int): String {
        return when {
            count == 0 -> "暂无学生"
            count == 1 -> "1名学生"
            else -> "${count}名学生"
        }
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
     * 获取参与度等级
     */
    fun getEngagementLevel(score: Float): String {
        return when {
            score >= 80f -> "非常活跃"
            score >= 60f -> "活跃"
            score >= 40f -> "一般"
            score >= 20f -> "较低"
            else -> "很低"
        }
    }
    
    /**
     * 获取参与度颜色
     */
    fun getEngagementColor(score: Float): Color {
        return when {
            score >= 80f -> ProgressExcellent
            score >= 60f -> ProgressGood
            score >= 40f -> ProgressAverage
            score >= 20f -> ProgressPoor
            else -> ProgressVeryPoor
        }
    }
    
    /**
     * 生成课程统计摘要
     */
    fun generateCourseSummary(
        totalStudents: Int,
        averageProgress: Float,
        completionRate: Float
    ): String {
        val studentText = formatStudentCount(totalStudents)
        val progressText = formatProgress(averageProgress)
        val completionText = formatProgress(completionRate)
        
        return "$studentText，平均进度$progressText，完成率$completionText"
    }
    
    /**
     * 生成学生进度摘要
     */
    fun generateStudentSummary(
        completedChapters: Int,
        totalTimeSpent: Long,
        lastActiveTime: Long?
    ): String {
        val chapterText = "完成${completedChapters}个章节"
        val timeText = "学习${formatLearningTime(totalTimeSpent)}"
        val activeText = "最后活跃：${formatRelativeTime(lastActiveTime)}"
        
        return "$chapterText，$timeText，$activeText"
    }
    
    /**
     * 验证课程数据
     */
    fun validateCourseData(
        title: String,
        description: String,
        category: String,
        difficulty: String
    ): List<String> {
        val errors = mutableListOf<String>()
        
        if (title.isBlank()) {
            errors.add("课程标题不能为空")
        } else if (title.length < 2) {
            errors.add("课程标题至少需要2个字符")
        } else if (title.length > 100) {
            errors.add("课程标题不能超过100个字符")
        }
        
        if (description.isBlank()) {
            errors.add("课程描述不能为空")
        } else if (description.length < 10) {
            errors.add("课程描述至少需要10个字符")
        } else if (description.length > 1000) {
            errors.add("课程描述不能超过1000个字符")
        }
        
        if (category.isBlank()) {
            errors.add("请选择课程类别")
        }
        
        if (difficulty.isBlank()) {
            errors.add("请选择课程难度")
        }
        
        return errors
    }
    
    /**
     * 生成课程ID
     */
    fun generateCourseId(): String {
        return "course_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    /**
     * 生成章节ID
     */
    fun generateChapterId(courseId: String, chapterIndex: Int): String {
        return "${courseId}_chapter_${chapterIndex}"
    }
    
    /**
     * 生成评估ID
     */
    fun generateAssessmentId(courseId: String): String {
        return "${courseId}_assessment_${System.currentTimeMillis()}"
    }
}