package com.example.education.feature_teacher.course_content

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.education.core.database.entity.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 课程内容管理工具类
 */
object CourseContentUtils {
    
    /**
     * 格式化学习时间
     */
    fun formatLearningTime(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes}分钟"
            minutes < 1440 -> {
                val hours = minutes / 60
                val remainingMinutes = minutes % 60
                if (remainingMinutes == 0) {
                    "${hours}小时"
                } else {
                    "${hours}小时${remainingMinutes}分钟"
                }
            }
            else -> {
                val days = minutes / 1440
                val remainingHours = (minutes % 1440) / 60
                if (remainingHours == 0) {
                    "${days}天"
                } else {
                    "${days}天${remainingHours}小时"
                }
            }
        }
    }
    
    /**
     * 格式化字数
     */
    fun formatWordCount(count: Int): String {
        return when {
            count < 1000 -> "${count}字"
            count < 10000 -> {
                val thousands = count / 1000.0
                "${String.format("%.1f", thousands)}千字"
            }
            else -> {
                val tenThousands = count / 10000.0
                "${String.format("%.1f", tenThousands)}万字"
            }
        }
    }
    
    /**
     * 格式化日期时间
     */
    fun formatDateTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * 格式化相对时间
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "刚刚"
            diff < 3600_000 -> "${diff / 60_000}分钟前"
            diff < 86400_000 -> "${diff / 3600_000}小时前"
            diff < 2592000_000 -> "${diff / 86400_000}天前"
            else -> formatDateTime(timestamp)
        }
    }
    
    /**
     * 获取内容类型颜色
     */
    @Composable
    fun getContentTypeColor(type: String): Color {
        return when (type.lowercase()) {
            "chapter" -> MaterialTheme.colorScheme.primary
            "assessment" -> MaterialTheme.colorScheme.secondary
            "quiz" -> MaterialTheme.colorScheme.tertiary
            "assignment" -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.outline
        }
    }
    
    /**
     * 获取评估类型文本
     */
    fun getAssessmentTypeText(type: AssessmentType): String {
        return when (type) {
            AssessmentType.QUIZ -> "随堂测验"
            AssessmentType.ASSIGNMENT -> "作业"
            AssessmentType.EXAM -> "考试"
            AssessmentType.PROJECT -> "项目"
        }
    }
    
    /**
     * 获取评估类型颜色
     */
    @Composable
    fun getAssessmentTypeColor(type: AssessmentType): Color {
        return when (type) {
            AssessmentType.QUIZ -> MaterialTheme.colorScheme.primary
            AssessmentType.ASSIGNMENT -> MaterialTheme.colorScheme.secondary
            AssessmentType.EXAM -> MaterialTheme.colorScheme.error
            AssessmentType.PROJECT -> MaterialTheme.colorScheme.tertiary
        }
    }
    
    /**
     * 获取内容完整性评分
     */
    fun getContentCompletenessScore(courseContentDetail: CourseContentDetail): Int {
        var score = 0
        
        // 基础内容检查 (40分)
        if (courseContentDetail.course.title.isNotBlank()) score += 10
        if (courseContentDetail.course.description.isNotBlank()) score += 10
        if (courseContentDetail.totalChapters > 0) score += 20
        
        // 章节内容检查 (30分)
        val chaptersWithContent = courseContentDetail.chapters.count { it.content.length > 100 }
        if (chaptersWithContent > 0) {
            score += (30 * chaptersWithContent / courseContentDetail.totalChapters.coerceAtLeast(1))
        }
        
        // 评估内容检查 (20分)
        if (courseContentDetail.totalAssessments > 0) {
            score += 20
        }
        
        // 内容丰富度检查 (10分)
        if (courseContentDetail.totalWords > 5000) score += 5
        if (courseContentDetail.estimatedDuration > 60) score += 5
        
        return score.coerceIn(0, 100)
    }
    
    /**
     * 获取内容完整性等级
     */
    fun getContentCompletenessLevel(score: Int): String {
        return when {
            score >= 90 -> "优秀"
            score >= 80 -> "良好"
            score >= 70 -> "一般"
            score >= 60 -> "及格"
            else -> "待完善"
        }
    }
    
    /**
     * 获取内容完整性颜色
     */
    @Composable
    fun getContentCompletenessColor(score: Int): Color {
        return when {
            score >= 90 -> Color(0xFF4CAF50) // 绿色
            score >= 80 -> Color(0xFF8BC34A) // 浅绿色
            score >= 70 -> Color(0xFFFFC107) // 黄色
            score >= 60 -> Color(0xFFFF9800) // 橙色
            else -> Color(0xFFF44336) // 红色
        }
    }
    
    /**
     * 生成内容改进建议
     */
    fun generateContentImprovementSuggestions(courseContentDetail: CourseContentDetail): List<String> {
        val suggestions = mutableListOf<String>()
        
        // 检查基础信息
        if (courseContentDetail.course.description.length < 100) {
            suggestions.add("建议完善课程描述，提供更详细的课程介绍")
        }
        
        // 检查章节数量
        if (courseContentDetail.totalChapters < 3) {
            suggestions.add("建议增加更多章节，丰富课程内容")
        }
        
        // 检查章节内容
        val shortChapters = courseContentDetail.chapters.count { it.content.length < 500 }
        if (shortChapters > 0) {
            suggestions.add("有${shortChapters}个章节内容较少，建议补充更多详细内容")
        }
        
        // 检查评估
        if (courseContentDetail.totalAssessments == 0) {
            suggestions.add("建议添加评估内容，帮助学生检验学习效果")
        } else {
            val chaptersWithoutAssessment = courseContentDetail.chapters.count { chapter ->
                courseContentDetail.assessments.none { it.chapterId == chapter.id }
            }
            if (chaptersWithoutAssessment > courseContentDetail.totalChapters / 2) {
                suggestions.add("建议为更多章节添加配套评估")
            }
        }
        
        // 检查内容平衡性
        if (courseContentDetail.chapters.isNotEmpty()) {
            val avgWordsPerChapter = courseContentDetail.totalWords / courseContentDetail.totalChapters
            val imbalancedChapters = courseContentDetail.chapters.count { chapter ->
                val ratio = chapter.content.length.toDouble() / avgWordsPerChapter
                ratio < 0.5 || ratio > 2.0
            }
            if (imbalancedChapters > courseContentDetail.totalChapters / 3) {
                suggestions.add("章节内容长度差异较大，建议调整内容分布")
            }
        }
        
        // 检查学习时长
        if (courseContentDetail.estimatedDuration < 30) {
            suggestions.add("课程内容较少，建议增加更多学习材料")
        } else if (courseContentDetail.estimatedDuration > 300) {
            suggestions.add("课程内容较多，建议考虑拆分为多个模块")
        }
        
        return suggestions.ifEmpty { listOf("课程内容已经很完善了！") }
    }
    
    /**
     * 验证章节数据
     */
    fun validateChapterData(
        title: String,
        content: String,
        description: String = ""
    ): List<String> {
        val errors = mutableListOf<String>()
        
        if (title.isBlank()) {
            errors.add("章节标题不能为空")
        } else if (title.length > 100) {
            errors.add("章节标题不能超过100个字符")
        }
        
        if (content.isBlank()) {
            errors.add("章节内容不能为空")
        } else if (content.length < 50) {
            errors.add("章节内容至少需要50个字符")
        }
        
        if (description.length > 500) {
            errors.add("章节描述不能超过500个字符")
        }
        
        return errors
    }
    
    /**
     * 验证评估数据
     */
    fun validateAssessmentData(
        title: String,
        description: String,
        questions: List<AssessmentQuestion>,
        timeLimit: Int?,
        passingScore: Int
    ): List<String> {
        val errors = mutableListOf<String>()
        
        if (title.isBlank()) {
            errors.add("评估标题不能为空")
        } else if (title.length > 100) {
            errors.add("评估标题不能超过100个字符")
        }
        
        if (description.length > 500) {
            errors.add("评估描述不能超过500个字符")
        }
        
        if (questions.isEmpty()) {
            errors.add("评估至少需要包含一个问题")
        } else if (questions.size > 50) {
            errors.add("评估问题数量不能超过50个")
        }
        
        // 验证问题内容
        questions.forEachIndexed { index, question ->
            if (question.question.isBlank()) {
                errors.add("第${index + 1}题的问题内容不能为空")
            }
            if (question.options.isEmpty()) {
                errors.add("第${index + 1}题至少需要一个选项")
            }
            if (question.correctAnswer.isBlank()) {
                errors.add("第${index + 1}题必须设置正确答案")
            }
        }
        
        if (timeLimit != null && timeLimit <= 0) {
            errors.add("时间限制必须大于0")
        }
        
        if (passingScore < 0 || passingScore > 100) {
            errors.add("及格分数必须在0-100之间")
        }
        
        return errors
    }
    
    /**
     * 格式化内容统计摘要
     */
    fun formatContentStatisticsSummary(statistics: ContentStatistics): String {
        return buildString {
            append("共${statistics.totalChapters}个章节")
            if (statistics.totalAssessments > 0) {
                append("，${statistics.totalAssessments}个评估")
            }
            append("，${formatWordCount(statistics.totalWords)}")
            append("，预计学习时长${formatLearningTime(statistics.estimatedDuration)}")
        }
    }
    
    /**
     * 获取章节排序建议
     */
    fun getChapterOrderingSuggestions(chapters: List<Chapter>): List<String> {
        val suggestions = mutableListOf<String>()
        
        // 检查章节顺序是否连续
        val orders = chapters.map { it.order }.sorted()
        for (i in 1 until orders.size) {
            if (orders[i] - orders[i - 1] > 1) {
                suggestions.add("章节顺序不连续，建议重新排序")
                break
            }
        }
        
        // 检查是否有重复的顺序
        if (orders.size != orders.distinct().size) {
            suggestions.add("存在重复的章节顺序，需要调整")
        }
        
        // 检查章节长度分布
        if (chapters.size >= 3) {
            val lengths = chapters.map { it.content.length }
            val avgLength = lengths.average()
            val firstChapterLength = lengths.first()
            val lastChapterLength = lengths.last()
            
            if (firstChapterLength > avgLength * 1.5) {
                suggestions.add("第一章内容较多，建议拆分或调整顺序")
            }
            
            if (lastChapterLength < avgLength * 0.5) {
                suggestions.add("最后一章内容较少，建议合并或补充内容")
            }
        }
        
        return suggestions
    }
    
    /**
     * 生成内容导出文件名
     */
    fun generateExportFileName(courseTitle: String, exportType: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val sanitizedTitle = courseTitle.replace(Regex("[^\\w\\s-]"), "")
            .replace(Regex("\\s+"), "_")
            .take(20)
        
        return "${sanitizedTitle}_${exportType}_${timestamp}"
    }
    
    /**
     * 计算内容复杂度评分
     */
    fun calculateContentComplexity(courseContentDetail: CourseContentDetail): Int {
        var complexity = 0
        
        // 基于章节数量 (0-30分)
        complexity += (courseContentDetail.totalChapters * 3).coerceAtMost(30)
        
        // 基于总字数 (0-25分)
        val wordComplexity = when {
            courseContentDetail.totalWords > 50000 -> 25
            courseContentDetail.totalWords > 20000 -> 20
            courseContentDetail.totalWords > 10000 -> 15
            courseContentDetail.totalWords > 5000 -> 10
            courseContentDetail.totalWords > 1000 -> 5
            else -> 0
        }
        complexity += wordComplexity
        
        // 基于评估数量和类型 (0-25分)
        val assessmentComplexity = courseContentDetail.assessments.sumOf { assessment ->
            when (assessment.type) {
                AssessmentType.QUIZ -> 2
                AssessmentType.ASSIGNMENT -> 4
                AssessmentType.EXAM -> 6
                AssessmentType.PROJECT -> 8
            }
        }.coerceAtMost(25)
        complexity += assessmentComplexity
        
        // 基于问题数量 (0-20分)
        val totalQuestions = courseContentDetail.assessments.sumOf { it.questions.size }
        complexity += (totalQuestions / 2).coerceAtMost(20)
        
        return complexity.coerceIn(0, 100)
    }
    
    /**
     * 获取内容复杂度等级
     */
    fun getContentComplexityLevel(complexity: Int): String {
        return when {
            complexity >= 80 -> "高级"
            complexity >= 60 -> "中级"
            complexity >= 40 -> "初级"
            complexity >= 20 -> "入门"
            else -> "基础"
        }
    }
    
    /**
     * 格式化章节预览
     */
    fun formatChapterPreview(content: String, maxLength: Int = 100): String {
        return if (content.length <= maxLength) {
            content
        } else {
            content.take(maxLength) + "..."
        }
    }
    
    /**
     * 获取推荐的章节长度范围
     */
    fun getRecommendedChapterLength(totalChapters: Int, totalWords: Int): IntRange {
        val avgWordsPerChapter = if (totalChapters > 0) totalWords / totalChapters else 1000
        val minLength = (avgWordsPerChapter * 0.7).toInt()
        val maxLength = (avgWordsPerChapter * 1.3).toInt()
        return minLength..maxLength
    }
    
    /**
     * 检查内容更新频率
     */
    fun analyzeUpdateFrequency(courseContentDetail: CourseContentDetail): String {
        val now = System.currentTimeMillis()
        val daysSinceLastUpdate = (now - courseContentDetail.lastModified) / (24 * 60 * 60 * 1000)
        
        return when {
            daysSinceLastUpdate < 1 -> "今天有更新"
            daysSinceLastUpdate < 7 -> "本周有更新"
            daysSinceLastUpdate < 30 -> "本月有更新"
            daysSinceLastUpdate < 90 -> "近期有更新"
            else -> "很久没有更新了"
        }
    }
}