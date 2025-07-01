package com.example.education.feature_student.reader

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.ui.graphics.Color
import com.example.education.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * 阅读器工具类
 * 提供章节内容处理、时间格式化等辅助功能
 */
object ReaderUtils {

    /**
     * 格式化学习时间
     * @param minutes 分钟数
     * @return 格式化的时间字符串
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
     * 格式化进度百分比
     * @param progress 进度值（0.0-1.0）
     * @return 格式化的百分比字符串
     */
    fun formatProgress(progress: Float): String {
        val percentage = (progress * 100).roundToInt()
        return "${percentage}%"
    }

    /**
     * 获取进度颜色
     * @param progress 进度值（0.0-1.0）
     * @return 对应的颜色
     */
    fun getProgressColor(progress: Float): Color {
        return when {
            progress < 0.3f -> ProgressLow
            progress < 0.7f -> ProgressMedium
            progress < 1.0f -> ProgressHigh
            else -> ProgressComplete
        }
    }

    /**
     * 计算阅读时间估算
     * @param content 章节内容
     * @param wordsPerMinute 每分钟阅读字数（默认200字/分钟）
     * @return 估算的阅读时间（分钟）
     */
    fun estimateReadingTime(content: String, wordsPerMinute: Int = 200): Int {
        // 移除HTML标签和多余空白字符
        val cleanContent = content
            .replace(Regex("<[^>]*>"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
        
        val wordCount = cleanContent.length // 中文按字符数计算
        val estimatedMinutes = (wordCount.toFloat() / wordsPerMinute).roundToInt()
        return maxOf(1, estimatedMinutes) // 至少1分钟
    }

    /**
     * 格式化相对时间
     * @param timestamp 时间戳
     * @param context 上下文
     * @return 相对时间字符串
     */
    fun formatRelativeTime(timestamp: Long, context: Context): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < DateUtils.MINUTE_IN_MILLIS -> "刚刚"
            diff < DateUtils.HOUR_IN_MILLIS -> {
                val minutes = (diff / DateUtils.MINUTE_IN_MILLIS).toInt()
                "${minutes}分钟前"
            }
            diff < DateUtils.DAY_IN_MILLIS -> {
                val hours = (diff / DateUtils.HOUR_IN_MILLIS).toInt()
                "${hours}小时前"
            }
            diff < DateUtils.WEEK_IN_MILLIS -> {
                val days = (diff / DateUtils.DAY_IN_MILLIS).toInt()
                "${days}天前"
            }
            else -> {
                val formatter = SimpleDateFormat("MM月dd日", Locale.getDefault())
                formatter.format(Date(timestamp))
            }
        }
    }

    /**
     * 解析章节内容中的媒体文件
     * @param content 章节内容
     * @return 媒体文件URL列表
     */
    fun extractMediaUrls(content: String): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()
        
        // 提取图片
        val imageRegex = Regex("<img[^>]+src=\"([^\"]+)\"[^>]*>", RegexOption.IGNORE_CASE)
        imageRegex.findAll(content).forEach { match ->
            val url = match.groupValues[1]
            mediaFiles.add(MediaFile(url, MediaType.IMAGE))
        }
        
        // 提取视频
        val videoRegex = Regex("<video[^>]+src=\"([^\"]+)\"[^>]*>", RegexOption.IGNORE_CASE)
        videoRegex.findAll(content).forEach { match ->
            val url = match.groupValues[1]
            mediaFiles.add(MediaFile(url, MediaType.VIDEO))
        }
        
        // 提取音频
        val audioRegex = Regex("<audio[^>]+src=\"([^\"]+)\"[^>]*>", RegexOption.IGNORE_CASE)
        audioRegex.findAll(content).forEach { match ->
            val url = match.groupValues[1]
            mediaFiles.add(MediaFile(url, MediaType.AUDIO))
        }
        
        return mediaFiles
    }

    /**
     * 清理HTML内容，保留基本格式
     * @param htmlContent HTML内容
     * @return 清理后的内容
     */
    fun cleanHtmlContent(htmlContent: String): String {
        return htmlContent
            // 保留段落标签
            .replace(Regex("<p[^>]*>"), "\n")
            .replace("</p>", "\n")
            // 保留换行标签
            .replace(Regex("<br[^>]*>"), "\n")
            // 保留标题标签
            .replace(Regex("<h[1-6][^>]*>"), "\n\n")
            .replace(Regex("</h[1-6]>"), "\n")
            // 保留列表标签
            .replace(Regex("<li[^>]*>"), "• ")
            .replace("</li>", "\n")
            // 移除其他HTML标签
            .replace(Regex("<[^>]*>"), "")
            // 清理多余的空白字符
            .replace(Regex("\n\s*\n"), "\n\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    /**
     * 生成章节目录
     * @param content 章节内容
     * @return 目录项列表
     */
    fun generateTableOfContents(content: String): List<TocItem> {
        val tocItems = mutableListOf<TocItem>()
        val headerRegex = Regex("<h([1-6])[^>]*>([^<]+)</h[1-6]>", RegexOption.IGNORE_CASE)
        
        headerRegex.findAll(content).forEach { match ->
            val level = match.groupValues[1].toInt()
            val title = match.groupValues[2].trim()
            val id = "header_${tocItems.size}"
            
            tocItems.add(TocItem(id, title, level))
        }
        
        return tocItems
    }

    /**
     * 计算阅读进度基于滚动位置
     * @param scrollOffset 当前滚动偏移
     * @param maxScrollOffset 最大滚动偏移
     * @return 进度值（0.0-1.0）
     */
    fun calculateScrollProgress(scrollOffset: Float, maxScrollOffset: Float): Float {
        if (maxScrollOffset <= 0) return 0f
        return (scrollOffset / maxScrollOffset).coerceIn(0f, 1f)
    }

    /**
     * 获取难度等级颜色
     * @param difficulty 难度等级
     * @return 对应的颜色
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
     * 获取难度等级文本
     * @param difficulty 难度等级
     * @return 本地化的难度文本
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
}

/**
 * 媒体文件数据类
 */
data class MediaFile(
    val url: String,
    val type: MediaType
)

/**
 * 媒体类型枚举
 */
enum class MediaType {
    IMAGE, VIDEO, AUDIO
}

/**
 * 目录项数据类
 */
data class TocItem(
    val id: String,
    val title: String,
    val level: Int
)