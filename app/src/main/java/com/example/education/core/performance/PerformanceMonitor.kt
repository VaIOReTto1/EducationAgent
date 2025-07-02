package com.example.education.core.performance

import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 性能监控器
 * 
 * 监控应用关键操作的性能指标
 */
@Singleton
class PerformanceMonitor @Inject constructor() {
    
    companion object {
        private const val TAG = "PerformanceMonitor"
        private const val SLOW_OPERATION_THRESHOLD_MS = 1000L
        private const val VERY_SLOW_OPERATION_THRESHOLD_MS = 3000L
    }
    
    private val activeOperations = ConcurrentHashMap<String, Long>()
    private val performanceMetrics = ConcurrentHashMap<String, PerformanceMetric>()
    
    private val _performanceEvents = MutableSharedFlow<PerformanceEvent>()
    val performanceEvents: SharedFlow<PerformanceEvent> = _performanceEvents.asSharedFlow()
    
    private val monitoringScope = CoroutineScope(Dispatchers.IO)
    
    /**
     * 开始监控操作
     */
    fun startOperation(operationName: String, metadata: Map<String, Any> = emptyMap()) {
        val startTime = SystemClock.elapsedRealtime()
        activeOperations[operationName] = startTime
        
        Log.d(TAG, "开始监控操作: $operationName")
        
        monitoringScope.launch {
            _performanceEvents.emit(
                PerformanceEvent.OperationStarted(
                    operationName = operationName,
                    timestamp = startTime,
                    metadata = metadata
                )
            )
        }
    }
    
    /**
     * 结束监控操作
     */
    fun endOperation(operationName: String, metadata: Map<String, Any> = emptyMap()) {
        val endTime = SystemClock.elapsedRealtime()
        val startTime = activeOperations.remove(operationName)
        
        if (startTime != null) {
            val duration = endTime - startTime
            recordMetric(operationName, duration, metadata)
            
            Log.d(TAG, "结束监控操作: $operationName, 耗时: ${duration}ms")
            
            // 检查是否为慢操作
            when {
                duration > VERY_SLOW_OPERATION_THRESHOLD_MS -> {
                    Log.w(TAG, "⚠️ 非常慢的操作: $operationName (${duration}ms)")
                    
                    monitoringScope.launch {
                        _performanceEvents.emit(
                            PerformanceEvent.VerySlowOperation(
                                operationName = operationName,
                                duration = duration,
                                timestamp = endTime,
                                metadata = metadata
                            )
                        )
                    }
                }
                duration > SLOW_OPERATION_THRESHOLD_MS -> {
                    Log.w(TAG, "⚠️ 慢操作: $operationName (${duration}ms)")
                    
                    monitoringScope.launch {
                        _performanceEvents.emit(
                            PerformanceEvent.SlowOperation(
                                operationName = operationName,
                                duration = duration,
                                timestamp = endTime,
                                metadata = metadata
                            )
                        )
                    }
                }
                else -> {
                    monitoringScope.launch {
                        _performanceEvents.emit(
                            PerformanceEvent.OperationCompleted(
                                operationName = operationName,
                                duration = duration,
                                timestamp = endTime,
                                metadata = metadata
                            )
                        )
                    }
                }
            }
        } else {
            Log.w(TAG, "尝试结束未开始的操作: $operationName")
        }
    }
    
    /**
     * 记录性能指标
     */
    private fun recordMetric(operationName: String, duration: Long, metadata: Map<String, Any>) {
        val existingMetric = performanceMetrics[operationName]
        
        val newMetric = if (existingMetric != null) {
            existingMetric.copy(
                totalCalls = existingMetric.totalCalls + 1,
                totalDuration = existingMetric.totalDuration + duration,
                minDuration = minOf(existingMetric.minDuration, duration),
                maxDuration = maxOf(existingMetric.maxDuration, duration),
                lastCall = SystemClock.elapsedRealtime()
            )
        } else {
            PerformanceMetric(
                operationName = operationName,
                totalCalls = 1,
                totalDuration = duration,
                minDuration = duration,
                maxDuration = duration,
                firstCall = SystemClock.elapsedRealtime(),
                lastCall = SystemClock.elapsedRealtime()
            )
        }
        
        performanceMetrics[operationName] = newMetric
    }
    
    /**
     * 使用高阶函数监控代码块执行
     */
    inline fun <T> measureOperation(
        operationName: String,
        metadata: Map<String, Any> = emptyMap(),
        operation: () -> T
    ): T {
        startOperation(operationName, metadata)
        return try {
            operation()
        } finally {
            endOperation(operationName, metadata)
        }
    }
    
    /**
     * 获取操作的性能指标
     */
    fun getMetric(operationName: String): PerformanceMetric? {
        return performanceMetrics[operationName]
    }
    
    /**
     * 获取所有性能指标
     */
    fun getAllMetrics(): Map<String, PerformanceMetric> {
        return performanceMetrics.toMap()
    }
    
    /**
     * 获取性能摘要报告
     */
    fun getPerformanceSummary(): PerformanceSummary {
        val metrics = performanceMetrics.values.toList()
        
        return PerformanceSummary(
            totalOperations = metrics.sumOf { it.totalCalls },
            slowOperations = metrics.count { it.averageDuration > SLOW_OPERATION_THRESHOLD_MS },
            verySlowOperations = metrics.count { it.averageDuration > VERY_SLOW_OPERATION_THRESHOLD_MS },
            topSlowOperations = metrics
                .sortedByDescending { it.averageDuration }
                .take(10),
            mostFrequentOperations = metrics
                .sortedByDescending { it.totalCalls }
                .take(10)
        )
    }
    
    /**
     * 清理性能数据
     */
    fun clearMetrics() {
        Log.d(TAG, "清理性能监控数据")
        performanceMetrics.clear()
        activeOperations.clear()
    }
    
    /**
     * 输出性能报告到日志
     */
    fun logPerformanceReport() {
        val summary = getPerformanceSummary()
        
        Log.i(TAG, "=== 性能监控报告 ===")
        Log.i(TAG, "总操作次数: ${summary.totalOperations}")
        Log.i(TAG, "慢操作数量: ${summary.slowOperations}")
        Log.i(TAG, "非常慢操作数量: ${summary.verySlowOperations}")
        
        Log.i(TAG, "=== 最慢的操作 ===")
        summary.topSlowOperations.take(5).forEach { metric ->
            Log.i(TAG, "${metric.operationName}: 平均${metric.averageDuration}ms (调用${metric.totalCalls}次)")
        }
        
        Log.i(TAG, "=== 最频繁的操作 ===")
        summary.mostFrequentOperations.take(5).forEach { metric ->
            Log.i(TAG, "${metric.operationName}: ${metric.totalCalls}次调用 (平均${metric.averageDuration}ms)")
        }
    }
}

/**
 * 性能指标数据类
 */
data class PerformanceMetric(
    val operationName: String,
    val totalCalls: Long,
    val totalDuration: Long,
    val minDuration: Long,
    val maxDuration: Long,
    val firstCall: Long,
    val lastCall: Long
) {
    val averageDuration: Long
        get() = if (totalCalls > 0) totalDuration / totalCalls else 0L
}

/**
 * 性能摘要数据类
 */
data class PerformanceSummary(
    val totalOperations: Long,
    val slowOperations: Int,
    val verySlowOperations: Int,
    val topSlowOperations: List<PerformanceMetric>,
    val mostFrequentOperations: List<PerformanceMetric>
)

/**
 * 性能事件密封类
 */
sealed class PerformanceEvent(
    open val operationName: String,
    open val timestamp: Long,
    open val metadata: Map<String, Any>
) {
    data class OperationStarted(
        override val operationName: String,
        override val timestamp: Long,
        override val metadata: Map<String, Any>
    ) : PerformanceEvent(operationName, timestamp, metadata)
    
    data class OperationCompleted(
        override val operationName: String,
        val duration: Long,
        override val timestamp: Long,
        override val metadata: Map<String, Any>
    ) : PerformanceEvent(operationName, timestamp, metadata)
    
    data class SlowOperation(
        override val operationName: String,
        val duration: Long,
        override val timestamp: Long,
        override val metadata: Map<String, Any>
    ) : PerformanceEvent(operationName, timestamp, metadata)
    
    data class VerySlowOperation(
        override val operationName: String,
        val duration: Long,
        override val timestamp: Long,
        override val metadata: Map<String, Any>
    ) : PerformanceEvent(operationName, timestamp, metadata)
} 