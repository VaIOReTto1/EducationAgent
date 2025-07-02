package com.example.education.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.education.core.database.entities.*
import kotlinx.coroutines.flow.Flow

/**
 * 课程数据访问对象
 */
@Dao
interface CourseDao {
    
    @Query("SELECT * FROM courses WHERE is_published = 1 ORDER BY created_at DESC")
    fun getAllActiveCourses(): Flow<List<CourseEntity>>
    
    @Query("SELECT * FROM courses WHERE teacher_id = :teacherId AND is_published = 1 ORDER BY created_at DESC")
    fun getCoursesByTeacher(teacherId: String): Flow<List<CourseEntity>>
    
    @Query("SELECT * FROM courses WHERE teacher_id = :teacherId AND is_published = 1 ORDER BY created_at DESC")
    fun getCoursesByTeacherPaging(teacherId: String): PagingSource<Int, CourseEntity>
    
    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): CourseEntity?
    
    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseByIdFlow(courseId: String): Flow<CourseEntity?>
    
    @Query("SELECT * FROM courses WHERE subject = :subject AND is_published = 1 ORDER BY created_at DESC")
    fun getCoursesBySubject(subject: String): Flow<List<CourseEntity>>
    
    @Query("SELECT * FROM courses WHERE grade_level = :grade AND is_published = 1 ORDER BY created_at DESC")
    fun getCoursesByGrade(grade: String): Flow<List<CourseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)
    
    @Update
    suspend fun updateCourse(course: CourseEntity)
    
    @Query("UPDATE courses SET updated_at = :updatedAt WHERE id = :courseId")
    suspend fun updateCourseTimestamp(courseId: String, updatedAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteCourse(course: CourseEntity)
    
    @Query("UPDATE courses SET is_published = 0 WHERE id = :courseId")
    suspend fun softDeleteCourse(courseId: String)
    
    @Query("SELECT COUNT(*) FROM courses WHERE teacher_id = :teacherId AND is_published = 1")
    suspend fun getCourseCountByTeacher(teacherId: String): Int
    
    @Query("SELECT * FROM courses ORDER BY created_at DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses ORDER BY created_at DESC")
    fun getCoursesPaged(): PagingSource<Int, CourseEntity>
    
    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourseById(courseId: String)
    
    @Query("SELECT * FROM courses WHERE title LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchCoursesByTitle(query: String): Flow<List<CourseEntity>>
}

 