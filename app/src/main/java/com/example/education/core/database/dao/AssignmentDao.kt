package com.example.education.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.education.core.database.entities.AssignmentEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * 作业数据访问对象
 * 
 * 提供作业相关的数据库操作方法
 */
@Dao
interface AssignmentDao {
    
    @Query("SELECT * FROM assignments WHERE teacher_id = :teacherId ORDER BY created_at DESC")
    fun getAssignmentsByTeacher(teacherId: String): Flow<List<AssignmentEntity>>
    
    @Query("SELECT * FROM assignments WHERE teacher_id = :teacherId ORDER BY created_at DESC")
    fun getAssignmentsByTeacherForPaging(teacherId: String): PagingSource<Int, AssignmentEntity>
    
    @Query("SELECT * FROM assignments WHERE course_id = :courseId ORDER BY due_date ASC")
    fun getAssignmentsByCourse(courseId: String): Flow<List<AssignmentEntity>>
    
    @Query("SELECT * FROM assignments WHERE course_id = :courseId ORDER BY due_date ASC")
    fun getAssignmentsByCourseForPaging(courseId: String): PagingSource<Int, AssignmentEntity>
    
    @Query("SELECT * FROM assignments WHERE chapter_id = :chapterId ORDER BY due_date ASC")
    fun getAssignmentsByChapter(chapterId: String): Flow<List<AssignmentEntity>>
    
    @Query("SELECT * FROM assignments WHERE id = :assignmentId")
    suspend fun getAssignmentById(assignmentId: String): AssignmentEntity?
    
    @Query("SELECT * FROM assignments WHERE id = :assignmentId")
    fun getAssignmentByIdFlow(assignmentId: String): Flow<AssignmentEntity?>
    
    @Query("""
        SELECT * FROM assignments 
        WHERE is_published = 1 
          AND due_date > :currentTime 
        ORDER BY due_date ASC
    """)
    fun getUpcomingAssignments(currentTime: LocalDateTime = LocalDateTime.now()): Flow<List<AssignmentEntity>>
    
    @Query("""
        SELECT * FROM assignments 
        WHERE course_id = :courseId 
          AND is_published = 1 
          AND due_date > :currentTime 
        ORDER BY due_date ASC
    """)
    fun getUpcomingAssignmentsByCourse(
        courseId: String, 
        currentTime: LocalDateTime = LocalDateTime.now()
    ): Flow<List<AssignmentEntity>>
    
    @Query("""
        SELECT * FROM assignments 
        WHERE due_date < :currentTime 
          AND is_published = 1
        ORDER BY due_date DESC
    """)
    fun getOverdueAssignments(currentTime: LocalDateTime = LocalDateTime.now()): Flow<List<AssignmentEntity>>
    
    @Query("""
        SELECT * FROM assignments 
        WHERE teacher_id = :teacherId 
          AND assignment_type = :type 
        ORDER BY created_at DESC
    """)
    fun getAssignmentsByType(teacherId: String, type: String): Flow<List<AssignmentEntity>>
    
    @Query("""
        SELECT * FROM assignments 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
        ORDER BY due_date ASC
    """)
    fun searchAssignments(query: String): Flow<List<AssignmentEntity>>
    
    @Query("""
        SELECT * FROM assignments 
        WHERE course_id = :courseId 
          AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY due_date ASC
    """)
    fun searchAssignmentsInCourse(courseId: String, query: String): Flow<List<AssignmentEntity>>
    
    @Query("SELECT COUNT(*) FROM assignments WHERE teacher_id = :teacherId")
    suspend fun getAssignmentCountByTeacher(teacherId: String): Int
    
    @Query("SELECT COUNT(*) FROM assignments WHERE teacher_id = :teacherId")
    fun getAssignmentCountByTeacherFlow(teacherId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM assignments WHERE course_id = :courseId")
    suspend fun getAssignmentCountByCourse(courseId: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)
    
    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)
    
    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)
    
    @Query("DELETE FROM assignments WHERE id = :assignmentId")
    suspend fun deleteAssignmentById(assignmentId: String)
    
    @Query("DELETE FROM assignments WHERE course_id = :courseId")
    suspend fun deleteAssignmentsByCourse(courseId: String)
    
    @Query("DELETE FROM assignments WHERE teacher_id = :teacherId")
    suspend fun deleteAssignmentsByTeacher(teacherId: String)
} 