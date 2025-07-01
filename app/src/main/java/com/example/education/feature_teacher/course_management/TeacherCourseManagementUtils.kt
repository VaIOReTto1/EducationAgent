package com.example.education.feature_teacher.course_management

import androidx.compose.ui.graphics.Color
import com.example.education.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 教师课程管理工具类
 * 提供数据格式化和辅助功能
 */
object TeacherCourseManagementUtils {
    
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
     * 获取课程状态文本
     */
    fun getCourseStatusText(courseDetail: TeacherCourseDetail): String {
        return when {
            !courseDetail.course.isPublished -> "草稿"
            courseDetail.totalStudents == 0 -> "已发布"
            courseDetail.activeStudents > 0 -> "进行中"
            else -> "已发布"
        }
    }
    
    /**
     * 获取课程状态颜色
     */
    fun getCourseStatusColor(courseDetail: TeacherCourseDetail): Color {
        return when {
            !courseDetail.course.isPublished -> Color.Gray
            courseDetail.totalStudents == 0 -> ProgressAverage
            courseDetail.activeStudents > 0 -> ProgressGood
            else -> ProgressAverage
        }
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
     * 获取活跃度等级
     */
    fun getActivityLevel(activeRate: Float): String {
        return when {
            activeRate >= 80f -> "非常活跃"
            activeRate >= 60f -> "活跃"
            activeRate >= 40f -> "一般"
            activeRate >= 20f -> "较低"
            else -> "很低"
        }
    }
    
    /**
     * 获取活跃度颜色
     */
    fun getActivityColor(activeRate: Float): Color {
        return when {
            activeRate >= 80f -> ProgressExcellent
            activeRate >= 60f -> ProgressGood
            activeRate >= 40f -> ProgressAverage
            activeRate >= 20f -> ProgressPoor
            else -> ProgressVeryPoor
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
     * 获取学生学习状态文本
     */
    fun getStudentStatusText(studentProgress: StudentProgress): String {
        return when {
            studentProgress.isCompleted() -> "已完成"
            studentProgress.isActive() -> "学习中"
            studentProgress.averageProgress > 0f -> "暂停中"
            else -> "未开始"
        }
    }
    
    /**
     * 获取学生学习状态颜色
     */
    fun getStudentStatusColor(studentProgress: StudentProgress): Color {
        return when {
            studentProgress.isCompleted() -> ProgressExcellent
            studentProgress.isActive() -> ProgressGood
            studentProgress.averageProgress > 0f -> ProgressAverage
            else -> ProgressPoor
        }
    }
    
    /**
     * 生成课程统计摘要
     */
    fun generateCourseStatisticsSummary(courseDetail: TeacherCourseDetail): String {
        val studentText = formatStudentCount(courseDetail.totalStudents)
        val chapterText = formatChapterCount(courseDetail.chapters.size)
        val progressText = formatProgress(courseDetail.averageProgress)
        return "$studentText，$chapterText，平均进度$progressText"
    }
    
    /**
     * 生成学生进度摘要
     */
    fun generateStudentProgressSummary(studentProgress: StudentProgress): String {
        val progressText = formatProgress(studentProgress.averageProgress)
        val chapterText = "${studentProgress.completedChapters}/${studentProgress.totalChapters}章节"
        val timeText = formatLearningTime(studentProgress.totalTimeSpent)
        return "进度$progressText，完成$chapterText，学习$timeText"
    }
    
    /**
     * 验证课程数据
     */
    fun validateCourseData(
        title: String,
        description: String,
        category: String,
        difficulty: String
    ): String? {
        return when {
            title.isBlank() -> "课程标题不能为空"
            title.length > 100 -> "课程标题不能超过100个字符"
            description.isBlank() -> "课程描述不能为空"
            description.length > 1000 -> "课程描述不能超过1000个字符"
            category.isBlank() -> "课程类别不能为空"
            difficulty.isBlank() -> "课程难度不能为空"
            else -> null
        }
    }
    
    /**
     * 验证章节数据
     */
    fun validateChapterData(
        title: String,
        content: String
    ): String? {
        return when {
            title.isBlank() -> "章节标题不能为空"
            title.length > 200 -> "章节标题不能超过200个字符"
            content.isBlank() -> "章节内容不能为空"
            content.length > 50000 -> "章节内容不能超过50000个字符"
            else -> null
        }
    }
    
    /**
     * 获取课程发布建议
     */
    fun getPublishSuggestions(courseDetail: TeacherCourseDetail): List<String> {
        val suggestions = mutableListOf<String>()
        
        if (courseDetail.chapters.isEmpty()) {
            suggestions.add("添加至少一个章节")
        }
        
        if (courseDetail.chapters.size < 3) {
            suggestions.add("建议添加更多章节以丰富课程内容")
        }
        
        if (courseDetail.assessments.isEmpty()) {
            suggestions.add("添加评估以检验学习效果")
        }
        
        if (courseDetail.course.description.length < 50) {
            suggestions.add("完善课程描述以吸引更多学生")
        }
        
        return suggestions
    }
    
    /**
     * 获取课程改进建议
     */
    fun getImprovementSuggestions(courseDetail: TeacherCourseDetail): List<String> {
        val suggestions = mutableListOf<String>()
        
        if (courseDetail.totalStudents > 0) {
            if (courseDetail.averageProgress < 30f) {
                suggestions.add("学生平均进度较低，考虑简化课程内容或增加引导")
            }
            
            if (courseDetail.getActiveRate() < 50f) {
                suggestions.add("学生活跃度较低，考虑增加互动内容或提醒机制")
            }
            
            if (courseDetail.getCompletionRate() < 20f) {
                suggestions.add("完成率较低，考虑优化课程结构或增加激励机制")
            }
            
            if (courseDetail.averageLearningTime < 30) {
                suggestions.add("平均学习时间较短，考虑增加更多实践内容")
            }
        } else {
            suggestions.add("暂无学生注册，考虑推广课程或优化课程介绍")
        }
        
        return suggestions
    }
    
    /**
     * 排序课程列表
     */
    fun sortCourses(
        courses: List<TeacherCourseDetail>,
        sortBy: CourseSortOption
    ): List<TeacherCourseDetail> {
        return when (sortBy) {
            CourseSortOption.TITLE_ASC -> courses.sortedBy { it.course.title }
            CourseSortOption.TITLE_DESC -> courses.sortedByDescending { it.course.title }
            CourseSortOption.CREATED_ASC -> courses.sortedBy { it.course.createdAt }
            CourseSortOption.CREATED_DESC -> courses.sortedByDescending { it.course.createdAt }
            CourseSortOption.UPDATED_ASC -> courses.sortedBy { it.course.updatedAt }
            CourseSortOption.UPDATED_DESC -> courses.sortedByDescending { it.course.updatedAt }
            CourseSortOption.STUDENTS_ASC -> courses.sortedBy { it.totalStudents }
            CourseSortOption.STUDENTS_DESC -> courses.sortedByDescending { it.totalStudents }
            CourseSortOption.PROGRESS_ASC -> courses.sortedBy { it.averageProgress }
            CourseSortOption.PROGRESS_DESC -> courses.sortedByDescending { it.averageProgress }
            CourseSortOption.STATUS -> courses.sortedWith(
                compareBy<TeacherCourseDetail> { !it.course.isPublished }
                    .thenByDescending { it.totalStudents }
            )
        }
    }
    
    /**
     * 过滤课程列表
     */
    fun filterCourses(
        courses: List<TeacherCourseDetail>,
        searchQuery: String,
        selectedCategory: String,
        selectedDifficulty: String,
        selectedStatus: String
    ): List<TeacherCourseDetail> {
        return courses.filter { courseDetail ->
            val course = courseDetail.course
            
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
            
            // 状态过滤
            val matchesStatus = when (selectedStatus) {
                "全部" -> true
                "草稿" -> !course.isPublished
                "已发布" -> course.isPublished
                "进行中" -> course.isPublished && courseDetail.activeStudents > 0
                else -> true
            }
            
            matchesSearch && matchesCategory && matchesDifficulty && matchesStatus
        }
    }
    
    /**
     * 生成课程分享文本
     */
    fun generateShareText(courseDetail: TeacherCourseDetail): String {
        val course = courseDetail.course
        return "我创建了课程《${course.title}》，这是一门${getDifficultyText(course.difficulty)}的${course.category}课程。${course.description}"
    }
    
    /**
     * 估算课程完成时间
     */
    fun estimateCourseCompletionTime(chapterCount: Int): String {
        val estimatedMinutes = chapterCount * 30 // 假设每章节30分钟
        return formatLearningTime(estimatedMinutes.toLong())
    }
    
    /**
     * 获取课程健康度评分
     */
    fun getCourseHealthScore(courseDetail: TeacherCourseDetail): Int {
        var score = 0
        
        // 基础内容评分 (40分)
        if (courseDetail.chapters.isNotEmpty()) score += 20
        if (courseDetail.chapters.size >= 3) score += 10
        if (courseDetail.assessments.isNotEmpty()) score += 10
        
        // 学生参与评分 (30分)
        if (courseDetail.totalStudents > 0) {
            score += 10
            if (courseDetail.getActiveRate() >= 50f) score += 10
            if (courseDetail.getCompletionRate() >= 30f) score += 10
        }
        
        // 内容质量评分 (30分)
        if (courseDetail.course.description.length >= 100) score += 10
        if (courseDetail.averageProgress >= 50f) score += 10
        if (courseDetail.averageLearningTime >= 60) score += 10
        
        return score
    }
    
    /**
     * 获取健康度等级
     */
    fun getHealthGrade(score: Int): String {
        return when {
            score >= 90 -> "优秀"
            score >= 70 -> "良好"
            score >= 50 -> "一般"
            score >= 30 -> "需改进"
            else -> "较差"
        }
    }
    
    /**
     * 获取健康度颜色
     */
    fun getHealthColor(score: Int): Color {
        return when {
            score >= 90 -> ProgressExcellent
            score >= 70 -> ProgressGood
            score >= 50 -> ProgressAverage
            score >= 30 -> ProgressPoor
            else -> ProgressVeryPoor
        }
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
    UPDATED_ASC("更新时间 ↑"),
    UPDATED_DESC("更新时间 ↓"),
    STUDENTS_ASC("学生数 ↑"),
    STUDENTS_DESC("学生数 ↓"),
    PROGRESS_ASC("进度 ↑"),
    PROGRESS_DESC("进度 ↓"),
    STATUS("状态")
}