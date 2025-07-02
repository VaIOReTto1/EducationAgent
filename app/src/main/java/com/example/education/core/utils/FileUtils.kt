package com.example.education.core.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest

/**
 * 文件处理工具类
 * 
 * 提供文件缓存、清理、加密等功能
 */
object FileUtils {
    
    private const val TAG = "FileUtils"
    
    // 缓存目录名称
    private const val CACHE_DIR_IMAGES = "images"
    private const val CACHE_DIR_AUDIO = "audio"
    private const val CACHE_DIR_DOCS = "documents"
    private const val CACHE_DIR_TEMP = "temp"
    
    // 文件大小限制
    private const val MAX_CACHE_SIZE = 100 * 1024 * 1024L // 100MB
    private const val MAX_SINGLE_FILE_SIZE = 10 * 1024 * 1024L // 10MB
    
    /**
     * 获取缓存目录
     */
    fun getCacheDir(context: Context, type: CacheType): File {
        val cacheDir = when (type) {
            CacheType.IMAGES -> File(context.cacheDir, CACHE_DIR_IMAGES)
            CacheType.AUDIO -> File(context.cacheDir, CACHE_DIR_AUDIO)
            CacheType.DOCUMENTS -> File(context.cacheDir, CACHE_DIR_DOCS)
            CacheType.TEMP -> File(context.cacheDir, CACHE_DIR_TEMP)
        }
        
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
            Log.d(TAG, "创建缓存目录: ${cacheDir.absolutePath}")
        }
        
        return cacheDir
    }
    
    /**
     * 保存文件到缓存
     */
    suspend fun saveToCache(
        context: Context,
        data: ByteArray,
        fileName: String,
        type: CacheType
    ): File? = withContext(Dispatchers.IO) {
        try {
            if (data.size > MAX_SINGLE_FILE_SIZE) {
                Log.w(TAG, "文件大小超出限制: ${data.size} bytes")
                return@withContext null
            }
            
            val cacheDir = getCacheDir(context, type)
            val file = File(cacheDir, fileName)
            
            // 检查缓存空间
            checkAndCleanCache(context, type)
            
            FileOutputStream(file).use { output ->
                output.write(data)
                output.flush()
            }
            
            Log.d(TAG, "文件保存成功: ${file.absolutePath}")
            file
            
        } catch (e: IOException) {
            Log.e(TAG, "保存文件失败: $fileName", e)
            null
        }
    }
    
    /**
     * 从缓存读取文件
     */
    suspend fun readFromCache(
        context: Context,
        fileName: String,
        type: CacheType
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val cacheDir = getCacheDir(context, type)
            val file = File(cacheDir, fileName)
            
            if (!file.exists()) {
                Log.d(TAG, "缓存文件不存在: $fileName")
                return@withContext null
            }
            
            // 更新访问时间
            file.setLastModified(System.currentTimeMillis())
            
            FileInputStream(file).use { input ->
                input.readBytes()
            }
            
        } catch (e: IOException) {
            Log.e(TAG, "读取缓存文件失败: $fileName", e)
            null
        }
    }
    
    /**
     * 检查文件是否在缓存中
     */
    fun isFileInCache(context: Context, fileName: String, type: CacheType): Boolean {
        val cacheDir = getCacheDir(context, type)
        val file = File(cacheDir, fileName)
        return file.exists() && file.isFile
    }
    
    /**
     * 删除缓存文件
     */
    suspend fun deleteFromCache(
        context: Context,
        fileName: String,
        type: CacheType
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val cacheDir = getCacheDir(context, type)
            val file = File(cacheDir, fileName)
            
            if (file.exists()) {
                val deleted = file.delete()
                Log.d(TAG, "删除缓存文件: $fileName, 结果: $deleted")
                deleted
            } else {
                true // 文件不存在，视为删除成功
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "删除缓存文件失败: $fileName", e)
            false
        }
    }
    
    /**
     * 清理过期缓存
     */
    suspend fun cleanExpiredCache(context: Context, type: CacheType, maxAgeMillis: Long = 7 * 24 * 60 * 60 * 1000L) = withContext(Dispatchers.IO) {
        try {
            val cacheDir = getCacheDir(context, type)
            val currentTime = System.currentTimeMillis()
            var deletedCount = 0
            var deletedSize = 0L
            
            cacheDir.listFiles()?.forEach { file ->
                if (file.isFile && (currentTime - file.lastModified()) > maxAgeMillis) {
                    deletedSize += file.length()
                    if (file.delete()) {
                        deletedCount++
                    }
                }
            }
            
            Log.d(TAG, "清理过期缓存完成: 删除${deletedCount}个文件，释放${formatFileSize(deletedSize)}")
            
        } catch (e: Exception) {
            Log.e(TAG, "清理过期缓存失败", e)
        }
    }
    
    /**
     * 检查并清理缓存空间
     */
    private suspend fun checkAndCleanCache(context: Context, type: CacheType) = withContext(Dispatchers.IO) {
        val cacheDir = getCacheDir(context, type)
        val currentSize = calculateDirectorySize(cacheDir)
        
        if (currentSize > MAX_CACHE_SIZE) {
            Log.d(TAG, "缓存空间不足，开始清理: ${formatFileSize(currentSize)}")
            
            // 按最后修改时间排序，删除最旧的文件
            val files = cacheDir.listFiles()?.sortedBy { it.lastModified() } ?: return@withContext
            var deletedSize = 0L
            
            for (file in files) {
                if (currentSize - deletedSize < MAX_CACHE_SIZE * 0.8) break
                
                deletedSize += file.length()
                file.delete()
            }
            
            Log.d(TAG, "缓存清理完成，释放: ${formatFileSize(deletedSize)}")
        }
    }
    
    /**
     * 计算目录大小
     */
    private fun calculateDirectorySize(directory: File): Long {
        var size = 0L
        directory.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateDirectorySize(file)
            } else {
                file.length()
            }
        }
        return size
    }
    
    /**
     * 格式化文件大小
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "${bytes}B"
            bytes < 1024 * 1024 -> "${bytes / 1024}KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)}MB"
            else -> "${bytes / (1024 * 1024 * 1024)}GB"
        }
    }
    
    /**
     * 计算文件MD5
     */
    suspend fun calculateMD5(file: File): String? = withContext(Dispatchers.IO) {
        try {
            val md5 = MessageDigest.getInstance("MD5")
            FileInputStream(file).use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    md5.update(buffer, 0, bytesRead)
                }
            }
            
            md5.digest().joinToString("") { "%02x".format(it) }
            
        } catch (e: Exception) {
            Log.e(TAG, "计算文件MD5失败", e)
            null
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    suspend fun getCacheStats(context: Context): CacheStats = withContext(Dispatchers.IO) {
        val stats = CacheStats()
        
        CacheType.values().forEach { type ->
            val cacheDir = getCacheDir(context, type)
            val size = calculateDirectorySize(cacheDir)
            val fileCount = cacheDir.listFiles()?.size ?: 0
            
            when (type) {
                CacheType.IMAGES -> {
                    stats.imagesSize = size
                    stats.imagesCount = fileCount
                }
                CacheType.AUDIO -> {
                    stats.audioSize = size
                    stats.audioCount = fileCount
                }
                CacheType.DOCUMENTS -> {
                    stats.documentsSize = size
                    stats.documentsCount = fileCount
                }
                CacheType.TEMP -> {
                    stats.tempSize = size
                    stats.tempCount = fileCount
                }
            }
        }
        
        stats
    }
}

/**
 * 缓存类型枚举
 */
enum class CacheType {
    IMAGES,     // 图片缓存
    AUDIO,      // 音频缓存
    DOCUMENTS,  // 文档缓存
    TEMP        // 临时文件
}

/**
 * 缓存统计信息
 */
data class CacheStats(
    var imagesSize: Long = 0,
    var imagesCount: Int = 0,
    var audioSize: Long = 0,
    var audioCount: Int = 0,
    var documentsSize: Long = 0,
    var documentsCount: Int = 0,
    var tempSize: Long = 0,
    var tempCount: Int = 0
) {
    val totalSize: Long
        get() = imagesSize + audioSize + documentsSize + tempSize
    
    val totalCount: Int
        get() = imagesCount + audioCount + documentsCount + tempCount
} 