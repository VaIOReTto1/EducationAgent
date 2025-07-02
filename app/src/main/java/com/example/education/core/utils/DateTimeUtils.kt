package com.example.education.core.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 日期时间工具类
 * 
 * 提供常用的时间格式化和计算功能
 */
object DateTimeUtils {
    
    // 常用日期格式
    private const val FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
    private const val FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    private const val FORMAT_HH_MM = "HH:mm"
    private const val FORMAT_MM_DD = "MM-dd"
    
    /**
     * 格式化时间戳为相对时间（如"2小时前"）
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < DateUtils.MINUTE_IN_MILLIS -> "刚刚"
            diff < DateUtils.HOUR_IN_MILLIS -> "${diff / DateUtils.MINUTE_IN_MILLIS}分钟前"
            diff < DateUtils.DAY_IN_MILLIS -> "${diff / DateUtils.HOUR_IN_MILLIS}小时前"
            diff < DateUtils.WEEK_IN_MILLIS -> "${diff / DateUtils.DAY_IN_MILLIS}天前"
            else -> formatDate(timestamp, FORMAT_YYYY_MM_DD)
        }
    }
    
    /**
     * 格式化时间戳为指定格式
     */
    fun formatDate(timestamp: Long, pattern: String = FORMAT_YYYY_MM_DD_HH_MM): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * 格式化时长（秒）为可读格式
     */
    fun formatDuration(seconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60
        
        return when {
            hours > 0 -> "${hours}小时${minutes}分钟"
            minutes > 0 -> "${minutes}分钟${secs}秒"
            else -> "${secs}秒"
        }
    }
    
    /**
     * 格式化学习时长为友好显示
     */
    fun formatStudyDuration(seconds: Long): String {
        val totalMinutes = seconds / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        
        return when {
            hours > 0 -> "${hours}小时${minutes}分钟"
            minutes > 0 -> "${minutes}分钟"
            else -> "不到1分钟"
        }
    }
    
    /**
     * 获取今日开始时间戳
     */
    fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * 获取本周开始时间戳（周一）
     */
    fun getWeekStartTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            val dayOfWeek = get(Calendar.DAY_OF_WEEK)
            val daysToSubtract = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
            add(Calendar.DAY_OF_MONTH, -daysToSubtract)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * 获取本月开始时间戳
     */
    fun getMonthStartTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * 判断时间戳是否为今天
     */
    fun isToday(timestamp: Long): Boolean {
        val today = getTodayStartTimestamp()
        val tomorrow = today + DateUtils.DAY_IN_MILLIS
        return timestamp in today until tomorrow
    }
    
    /**
     * 判断时间戳是否为本周
     */
    fun isThisWeek(timestamp: Long): Boolean {
        val weekStart = getWeekStartTimestamp()
        val weekEnd = weekStart + 7 * DateUtils.DAY_IN_MILLIS
        return timestamp in weekStart until weekEnd
    }
    
    /**
     * 计算两个时间戳之间的天数差
     */
    fun getDaysBetween(startTimestamp: Long, endTimestamp: Long): Int {
        val diffInMillis = kotlin.math.abs(endTimestamp - startTimestamp)
        return (diffInMillis / DateUtils.DAY_IN_MILLIS).toInt()
    }
    
    /**
     * 获取友好的时间显示（用于学习统计）
     */
    fun getTimeOfDayGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "早上好"
            in 12..13 -> "中午好"
            in 14..17 -> "下午好"
            in 18..23 -> "晚上好"
            else -> "夜深了"
        }
    }
    
    /**
     * 获取学习时间段建议
     */
    fun getStudyTimeRecommendation(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..9 -> "早晨是记忆的黄金时间，适合学习新知识"
            in 10..11 -> "上午思维清晰，适合处理复杂问题"
            in 14..16 -> "下午精力充沛，适合深度学习"
            in 19..21 -> "晚上适合复习和总结"
            else -> "注意劳逸结合，保证充足睡眠"
        }
    }
} 