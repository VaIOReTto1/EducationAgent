package com.example.education.core.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room数据库类型转换器
 * 用于处理复杂数据类型的序列化和反序列化
 */
class DatabaseConverters {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * 将Map<String, String>转换为JSON字符串
     */
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    /**
     * 将JSON字符串转换为Map<String, String>
     */
    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return value?.let {
            try {
                json.decodeFromString<Map<String, String>>(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * 将List<String>转换为JSON字符串
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    /**
     * 将JSON字符串转换为List<String>
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            try {
                json.decodeFromString<List<String>>(it)
            } catch (e: Exception) {
                null
            }
        }
    }
}