package com.example.education.feature_student.chapter_reader

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 章节阅读模块的工具类
 */
object ChapterReaderUtils {
    
    // 时间格式化器
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("MM-dd", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    /**
     * 格式化学习时间
     */
    fun formatLearningTime(timeInMillis: Long): String {
        val totalMinutes = (timeInMillis / (1000 * 60)).toInt()
        return when {
            totalMinutes < 1 -> "< 1分钟"
            totalMinutes < 60 -> "${totalMinutes}分钟"
            totalMinutes < 1440 -> {
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                if (minutes == 0) "${hours}小时" else "${hours}小时${minutes}分钟"
            }
            else -> {
                val days = totalMinutes / 1440
                val hours = (totalMinutes % 1440) / 60
                if (hours == 0) "${days}天" else "${days}天${hours}小时"
            }
        }
    }
    
    /**
     * 格式化阅读进度百分比
     */
    fun formatProgress(progress: Float): String {
        return "${(progress * 100).roundToInt()}%"
    }
    
    /**
     * 格式化相对时间
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}天前"
            else -> formatDate(timestamp)
        }
    }
    
    /**
     * 格式化日期时间
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormatter.format(Date(timestamp))
    }
    
    /**
     * 格式化日期
     */
    fun formatDate(timestamp: Long): String {
        return dateFormatter.format(Date(timestamp))
    }
    
    /**
     * 格式化时间
     */
    fun formatTime(timestamp: Long): String {
        return timeFormatter.format(Date(timestamp))
    }
    
    /**
     * 获取进度颜色
     */
    fun getProgressColor(progress: Float): Color {
        return when {
            progress >= 1.0f -> Color(0xFF4CAF50) // 绿色 - 已完成
            progress >= 0.7f -> Color(0xFF2196F3) // 蓝色 - 进度良好
            progress >= 0.3f -> Color(0xFFFF9800) // 橙色 - 进度一般
            else -> Color(0xFFF44336) // 红色 - 进度较慢
        }
    }
    
    /**
     * 获取阅读状态文本
     */
    fun getReadingStatusText(chapterDetail: ChapterDetail): String {
        return when {
            chapterDetail.isCompleted -> "已完成"
            chapterDetail.readingProgress > 0f -> "阅读中"
            else -> "未开始"
        }
    }
    
    /**
     * 获取阅读状态颜色
     */
    fun getReadingStatusColor(chapterDetail: ChapterDetail): Color {
        return when {
            chapterDetail.isCompleted -> Color(0xFF4CAF50) // 绿色
            chapterDetail.readingProgress > 0f -> Color(0xFF2196F3) // 蓝色
            else -> Color(0xFF9E9E9E) // 灰色
        }
    }
    
    /**
     * 获取章节难度颜色
     */
    fun getDifficultyColor(difficulty: String): Color {
        return when (difficulty) {
            "初级" -> Color(0xFF4CAF50) // 绿色
            "中级" -> Color(0xFFFF9800) // 橙色
            "高级" -> Color(0xFFF44336) // 红色
            "专家" -> Color(0xFF9C27B0) // 紫色
            else -> Color(0xFF9E9E9E) // 灰色
        }
    }
    
    /**
     * 获取课程类别颜色
     */
    fun getCategoryColor(category: String): Color {
        return when (category) {
            "数学" -> Color(0xFF2196F3) // 蓝色
            "科学" -> Color(0xFF4CAF50) // 绿色
            "语言" -> Color(0xFFFF9800) // 橙色
            "历史" -> Color(0xFF795548) // 棕色
            "艺术" -> Color(0xFFE91E63) // 粉色
            "技术" -> Color(0xFF607D8B) // 蓝灰色
            else -> Color(0xFF9E9E9E) // 灰色
        }
    }
    
    /**
     * 计算完成率
     */
    fun calculateCompletionRate(completed: Int, total: Int): Float {
        return if (total > 0) completed.toFloat() / total else 0f
    }
    
    /**
     * 格式化章节数量
     */
    fun formatChapterCount(count: Int): String {
        return when {
            count == 0 -> "无章节"
            count == 1 -> "1个章节"
            else -> "${count}个章节"
        }
    }
    
    /**
     * 格式化评估数量
     */
    fun formatAssessmentCount(count: Int): String {
        return when {
            count == 0 -> "无评估"
            count == 1 -> "1个评估"
            else -> "${count}个评估"
        }
    }
    
    /**
     * 获取学习建议
     */
    fun getLearningTips(chapterDetail: ChapterDetail): List<String> {
        val tips = mutableListOf<String>()
        
        // 基于阅读进度给出建议
        when {
            chapterDetail.readingProgress == 0f -> {
                tips.add("开始阅读这个章节，预计需要${chapterDetail.estimatedReadingTime}分钟")
                tips.add("建议在安静的环境中专心阅读")
            }
            chapterDetail.readingProgress < 0.5f -> {
                tips.add("继续阅读，你已经完成了${formatProgress(chapterDetail.readingProgress)}")
                tips.add("可以做一些笔记来帮助理解")
            }
            chapterDetail.readingProgress < 1.0f -> {
                tips.add("快要完成了！还剩${formatProgress(1f - chapterDetail.readingProgress)}")
                tips.add("完成后可以尝试章节评估来检验学习效果")
            }
            else -> {
                tips.add("恭喜完成这个章节！")
                if (chapterDetail.nextChapter != null) {
                    tips.add("可以继续学习下一章节：${chapterDetail.nextChapter.title}")
                }
                if (chapterDetail.assessments.isNotEmpty()) {
                    tips.add("尝试完成相关评估来巩固知识")
                }
            }
        }
        
        // 基于学习时间给出建议
        val sessionTime = 30 * 60 * 1000L // 30分钟
        if (chapterDetail.timeSpent > sessionTime) {
            tips.add("已经学习了${formatLearningTime(chapterDetail.timeSpent)}，建议适当休息")
        }
        
        return tips
    }
    
    /**
     * 生成学习进度摘要
     */
    fun generateProgressSummary(statistics: LearningStatistics): String {
        val completionRate = calculateCompletionRate(statistics.completedChapters, statistics.totalChapters)
        return when {
            completionRate >= 1.0f -> "恭喜！已完成所有章节"
            completionRate >= 0.8f -> "即将完成课程，加油！"
            completionRate >= 0.5f -> "学习进度过半，继续努力"
            completionRate >= 0.2f -> "已有良好开端，保持学习节奏"
            else -> "刚刚开始学习，建议制定学习计划"
        }
    }
    
    /**
     * 获取学习效率评级
     */
    fun getLearningEfficiencyGrade(timeSpent: Long, progress: Float): String {
        if (progress == 0f) return "未开始"
        
        val timePerProgress = timeSpent / progress // 每1%进度所需时间
        val averageTime = 30 * 60 * 1000L // 30分钟的毫秒数
        
        return when {
            timePerProgress <= averageTime * 0.5f -> "高效"
            timePerProgress <= averageTime -> "良好"
            timePerProgress <= averageTime * 1.5f -> "一般"
            else -> "需要改进"
        }
    }
    
    /**
     * 获取学习效率颜色
     */
    fun getLearningEfficiencyColor(grade: String): Color {
        return when (grade) {
            "高效" -> Color(0xFF4CAF50) // 绿色
            "良好" -> Color(0xFF2196F3) // 蓝色
            "一般" -> Color(0xFFFF9800) // 橙色
            "需要改进" -> Color(0xFFF44336) // 红色
            else -> Color(0xFF9E9E9E) // 灰色
        }
    }
    
    /**
     * 格式化搜索结果高亮
     */
    fun formatSearchHighlight(snippet: String, start: Int, end: Int): Pair<String, String> {
        val before = snippet.substring(0, start)
        val highlight = snippet.substring(start, end)
        val after = snippet.substring(end)
        return Pair("$before**$highlight**$after", highlight)
    }
    
    /**
     * 获取大纲层级缩进
     */
    fun getOutlineIndent(level: Int): Int {
        return (level - 1) * 16 // 每级缩进16dp
    }
    
    /**
     * 验证笔记内容
     */
    fun validateNoteContent(content: String): Boolean {
        return content.trim().isNotEmpty() && content.length <= 1000
    }
    
    /**
     * 格式化笔记预览
     */
    fun formatNotePreview(content: String, maxLength: Int = 50): String {
        return if (content.length <= maxLength) {
            content
        } else {
            content.take(maxLength) + "..."
        }
    }
    
    /**
     * 获取阅读速度文本
     */
    fun getReadingSpeedText(timeSpent: Long, contentLength: Int): String {
        if (timeSpent == 0L || contentLength == 0) return "未知"
        
        val minutes = timeSpent / (60 * 1000L)
        if (minutes == 0L) return "很快"
        
        val wordsPerMinute = contentLength / minutes.toInt()
        return when {
            wordsPerMinute >= 300 -> "很快"
            wordsPerMinute >= 200 -> "正常"
            wordsPerMinute >= 100 -> "较慢"
            else -> "很慢"
        }
    }
    
    /**
     * 计算预计剩余时间
     */
    fun calculateEstimatedRemainingTime(
        totalEstimatedTime: Int, // 分钟
        currentProgress: Float
    ): String {
        val remainingMinutes = (totalEstimatedTime * (1f - currentProgress)).toInt()
        return formatLearningTime(remainingMinutes * 60 * 1000L)
    }
    
    /**
     * 生成章节导航提示
     */
    fun generateNavigationHint(chapterDetail: ChapterDetail): String {
        return buildString {
            append("第${chapterDetail.currentChapterIndex}章")
            append(" / ")
            append("共${chapterDetail.totalChapters}章")
            
            if (chapterDetail.previousChapter != null) {
                append(" • 上一章：${chapterDetail.previousChapter.title}")
            }
            
            if (chapterDetail.nextChapter != null) {
                append(" • 下一章：${chapterDetail.nextChapter.title}")
            }
        }
    }
    
    /**
     * 获取章节标签
     */
    fun getChapterTags(chapterDetail: ChapterDetail): List<String> {
        val tags = mutableListOf<String>()
        
        // 添加状态标签
        tags.add(getReadingStatusText(chapterDetail))
        
        // 添加时长标签
        tags.add("${chapterDetail.estimatedReadingTime}分钟")
        
        // 添加评估标签
        if (chapterDetail.assessments.isNotEmpty()) {
            tags.add("${chapterDetail.assessments.size}个评估")
        }
        
        // 添加难度标签（如果有的话）
        // tags.add(chapterDetail.course.difficulty)
        
        return tags
    }
    
    /**
     * 格式化章节统计信息
     */
    fun formatChapterStats(chapterDetail: ChapterDetail): String {
        return buildString {
            append("进度：${formatProgress(chapterDetail.readingProgress)}")
            
            if (chapterDetail.timeSpent > 0) {
                append(" • 用时：${formatLearningTime(chapterDetail.timeSpent)}")
            }
            
            if (chapterDetail.lastAccessTime != null) {
                append(" • 最后阅读：${formatRelativeTime(chapterDetail.lastAccessTime)}")
            }
        }
    }
    
    /**
     * 生成学习报告
     */
    fun generateLearningReport(statistics: LearningStatistics): String {
        return buildString {
            appendLine("📊 学习报告")
            appendLine()
            appendLine("📚 章节进度：${statistics.completedChapters}/${statistics.totalChapters}")
            appendLine("⏱️ 学习时长：${formatLearningTime(statistics.totalTimeSpent)}")
            appendLine("📈 平均进度：${formatProgress(statistics.averageProgress)}")
            
            if (statistics.estimatedTimeToComplete > 0) {
                appendLine("⏰ 预计剩余：${formatLearningTime(statistics.estimatedTimeToComplete)}")
            }
            
            if (statistics.currentChapter != null) {
                appendLine("📖 当前章节：${statistics.currentChapter.title}")
            }
            
            appendLine()
            appendLine(generateProgressSummary(statistics))
        }
    }
}