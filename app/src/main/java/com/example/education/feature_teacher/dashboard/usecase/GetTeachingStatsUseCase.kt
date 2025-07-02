package com.example.education.feature_teacher.dashboard.usecase

import android.util.Log
import com.example.education.core.repository.CourseRepository
import com.example.education.core.repository.UserRepository
import com.example.education.core.user.RoleManager
import com.example.education.core.user.UserRole
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取教学统计数据用例
 * 
 * 计算教师的教学效率指数和学生学习效果统计
 */
@Singleton
class GetTeachingStatsUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val roleManager: RoleManager
) {
    
    companion object {
        private const val TAG = "GetTeachingStatsUseCase"
    }
    
    /**
     * 获取教学统计数据
     */
    suspend fun execute(): Result<TeachingStats> {
        return try {
            Log.d(TAG, "开始获取教学统计数据")
            
            // 获取当前教师ID
            val teacherId = roleManager.getCurrentUserId()
                ?: return Result.failure(Exception("教师未登录"))
            
            // 验证当前用户是否为教师
            val currentRole = roleManager.getCurrentRole()
            if (currentRole != UserRole.TEACHER) {
                return Result.failure(Exception("当前用户不是教师"))
            }
            
            // 获取教师的课程列表
            val courses = courseRepository.getCoursesByTeacher(teacherId).first()
            Log.d(TAG, "教师课程数量: ${courses.size}")
            
            // 计算课程统计数据
            val totalCourses = courses.size
            val publishedCourses = courses.count { it.isPublished }
            
            // 计算学生统计数据
            var totalStudents = 0
            var totalLearningProgress = 0f
            var totalCompletionRate = 0f
            
            courses.forEach { course ->
                val studentCount = courseRepository.getStudentCountByCourse(course.id)
                val averageProgress = courseRepository.getAverageProgressByCourse(course.id)
                val completionRate = courseRepository.getCompletionRateByCourse(course.id)
                
                totalStudents += studentCount
                totalLearningProgress += averageProgress
                totalCompletionRate += completionRate
                
                Log.d(TAG, "课程${course.title}: 学生${studentCount}人, 平均进度${averageProgress}%, 完成率${completionRate}%")
            }
            
            // 计算平均值
            val averageProgress = if (totalCourses > 0) totalLearningProgress / totalCourses else 0f
            val averageCompletionRate = if (totalCourses > 0) totalCompletionRate / totalCourses else 0f
            
            // 计算教学效率指数
            val teachingEfficiencyIndex = calculateTeachingEfficiencyIndex(
                totalCourses = totalCourses,
                publishedCourses = publishedCourses,
                totalStudents = totalStudents,
                averageProgress = averageProgress,
                averageCompletionRate = averageCompletionRate
            )
            
            // 获取趋势数据（简化版，实际应该基于历史数据）
            val progressTrend = if (averageProgress > 70f) "上升" else "平稳"
            val completionTrend = if (averageCompletionRate > 80f) "上升" else "平稳"
            
            val stats = TeachingStats(
                totalCourses = totalCourses,
                publishedCourses = publishedCourses,
                totalStudents = totalStudents,
                averageProgress = averageProgress,
                averageCompletionRate = averageCompletionRate,
                teachingEfficiencyIndex = teachingEfficiencyIndex,
                progressTrend = progressTrend,
                completionTrend = completionTrend,
                lastUpdated = System.currentTimeMillis()
            )
            
            Log.d(TAG, "教学统计数据获取成功: $stats")
            Result.success(stats)
            
        } catch (e: Exception) {
            Log.e(TAG, "获取教学统计数据失败", e)
            Result.failure(e)
        }
    }
    
    /**
     * 计算教学效率指数
     * 
     * 基于课程数量、学生数量、平均进度和完成率的综合评分
     */
    private fun calculateTeachingEfficiencyIndex(
        totalCourses: Int,
        publishedCourses: Int,
        totalStudents: Int,
        averageProgress: Float,
        averageCompletionRate: Float
    ): Float {
        
        // 课程发布率权重 (0.2)
        val coursePublishRate = if (totalCourses > 0) publishedCourses.toFloat() / totalCourses else 0f
        val courseScore = coursePublishRate * 20f
        
        // 学生参与度权重 (0.3)
        val studentEngagement = minOf(totalStudents / 50f, 1f) // 假设50个学生为满分
        val studentScore = studentEngagement * 30f
        
        // 学习进度权重 (0.3)
        val progressScore = (averageProgress / 100f) * 30f
        
        // 完成率权重 (0.2)
        val completionScore = (averageCompletionRate / 100f) * 20f
        
        val totalScore = courseScore + studentScore + progressScore + completionScore
        
        Log.d(TAG, "教学效率指数计算: 课程${courseScore}, 学生${studentScore}, 进度${progressScore}, 完成${completionScore}, 总分${totalScore}")
        
        return totalScore
    }
    
    /**
     * 获取课程详细统计
     */
    suspend fun getCourseDetailStats(courseId: String): Result<CourseStats> {
        return try {
            Log.d(TAG, "获取课程详细统计: $courseId")
            
            val course = courseRepository.getCourseById(courseId)
                ?: return Result.failure(Exception("课程不存在"))
            
            val studentCount = courseRepository.getStudentCountByCourse(courseId)
            val averageProgress = courseRepository.getAverageProgressByCourse(courseId)
            val completionRate = courseRepository.getCompletionRateByCourse(courseId)
            
            // 获取章节统计
            val chapters = courseRepository.getChaptersByCourse(courseId).first()
            val totalChapters = chapters.size
            val publishedChapters = chapters.count { it.isPublished }
            
            val courseStats = CourseStats(
                courseId = courseId,
                courseName = course.title,
                studentCount = studentCount,
                totalChapters = totalChapters,
                publishedChapters = publishedChapters,
                averageProgress = averageProgress,
                completionRate = completionRate,
                lastUpdated = System.currentTimeMillis()
            )
            
            Log.d(TAG, "课程统计获取成功: $courseStats")
            Result.success(courseStats)
            
        } catch (e: Exception) {
            Log.e(TAG, "获取课程统计失败", e)
            Result.failure(e)
        }
    }
}

/**
 * 教学统计数据
 */
data class TeachingStats(
    val totalCourses: Int,
    val publishedCourses: Int,
    val totalStudents: Int,
    val averageProgress: Float,
    val averageCompletionRate: Float,
    val teachingEfficiencyIndex: Float,
    val progressTrend: String, // "上升", "下降", "平稳"
    val completionTrend: String,
    val lastUpdated: Long
)

/**
 * 课程统计数据
 */
data class CourseStats(
    val courseId: String,
    val courseName: String,
    val studentCount: Int,
    val totalChapters: Int,
    val publishedChapters: Int,
    val averageProgress: Float,
    val completionRate: Float,
    val lastUpdated: Long
) 