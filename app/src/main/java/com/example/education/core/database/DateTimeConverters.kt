package com.example.education.core.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * LocalDateTime 类型转换器
 * 
 * 为Room数据库提供LocalDateTime与Long之间的转换
 */
class DateTimeConverters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
        }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }
} 