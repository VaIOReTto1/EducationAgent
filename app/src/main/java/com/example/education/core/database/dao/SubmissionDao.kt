package com.example.education.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.education.core.database.entities.SubmissionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 作业提交数据访问对象
 * 
 * 提供作业提交相关的数据库操作方法
 */
@Dao
interface SubmissionDao {
    
    @Query("SELECT * FROM submissions WHERE student_id = :studentId ORDER BY submitted_at DESC")
    fun getSubmissionsByStudent(studentId: String): Flow<List<SubmissionEntity>>
    
    @Query("SELECT * FROM submissions WHERE student_id = :studentId ORDER BY submitted_at DESC")
    fun getSubmissionsByStudentForPaging(studentId: String): PagingSource<Int, SubmissionEntity>
    
    @Query("SELECT * FROM submissions WHERE assignment_id = :assignmentId ORDER BY submitted_at DESC")
    fun getSubmissionsByAssignment(assignmentId: String): Flow<List<SubmissionEntity>>
    
    @Query("SELECT * FROM submissions WHERE assignment_id = :assignmentId ORDER BY submitted_at DESC")
    fun getSubmissionsByAssignmentForPaging(assignmentId: String): PagingSource<Int, SubmissionEntity>
    
    @Query("SELECT * FROM submissions WHERE student_id = :studentId AND assignment_id = :assignmentId")
    suspend fun getSubmissionByStudentAndAssignment(studentId: String, assignmentId: String): SubmissionEntity?
    
    @Query("SELECT * FROM submissions WHERE student_id = :studentId AND assignment_id = :assignmentId")
    fun getSubmissionByStudentAndAssignmentFlow(studentId: String, assignmentId: String): Flow<SubmissionEntity?>
    
    @Query("SELECT * FROM submissions WHERE id = :submissionId")
    suspend fun getSubmissionById(submissionId: String): SubmissionEntity?
    
    @Query("SELECT * FROM submissions WHERE id = :submissionId")
    fun getSubmissionByIdFlow(submissionId: String): Flow<SubmissionEntity?>
    
    @Query("""
        SELECT * FROM submissions 
        WHERE status = :status 
        ORDER BY submitted_at DESC
    """)
    fun getSubmissionsByStatus(status: String): Flow<List<SubmissionEntity>>
    
    @Query("""
        SELECT * FROM submissions 
        WHERE assignment_id = :assignmentId AND status = :status 
        ORDER BY submitted_at DESC
    """)
    fun getSubmissionsByAssignmentAndStatus(assignmentId: String, status: String): Flow<List<SubmissionEntity>>
    
    @Query("""
        SELECT * FROM submissions 
        WHERE graded_by = :teacherId 
        ORDER BY graded_at DESC
    """)
    fun getSubmissionsGradedByTeacher(teacherId: String): Flow<List<SubmissionEntity>>
    
    @Query("""
        SELECT * FROM submissions 
        WHERE status = 'submitted' 
        ORDER BY submitted_at ASC
    """)
    fun getPendingSubmissions(): Flow<List<SubmissionEntity>>
    
    @Query("""
        SELECT * FROM submissions 
        WHERE assignment_id = :assignmentId AND status = 'submitted' 
        ORDER BY submitted_at ASC
    """)
    fun getPendingSubmissionsByAssignment(assignmentId: String): Flow<List<SubmissionEntity>>
    
    @Query("""
        SELECT * FROM submissions 
        WHERE is_late = 1 
        ORDER BY submitted_at DESC
    """)
    fun getLateSubmissions(): Flow<List<SubmissionEntity>>
    
    @Query("""
        SELECT * FROM submissions 
        WHERE assignment_id = :assignmentId AND is_late = 1 
        ORDER BY submitted_at DESC
    """)
    fun getLateSubmissionsByAssignment(assignmentId: String): Flow<List<SubmissionEntity>>
    
    @Query("SELECT COUNT(*) FROM submissions WHERE student_id = :studentId")
    suspend fun getSubmissionCountByStudent(studentId: String): Int
    
    @Query("SELECT COUNT(*) FROM submissions WHERE student_id = :studentId")
    fun getSubmissionCountByStudentFlow(studentId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM submissions WHERE assignment_id = :assignmentId")
    suspend fun getSubmissionCountByAssignment(assignmentId: String): Int
    
    @Query("SELECT COUNT(*) FROM submissions WHERE assignment_id = :assignmentId")
    fun getSubmissionCountByAssignmentFlow(assignmentId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM submissions WHERE assignment_id = :assignmentId AND status = :status")
    suspend fun getSubmissionCountByAssignmentAndStatus(assignmentId: String, status: String): Int
    
    @Query("SELECT AVG(score) FROM submissions WHERE assignment_id = :assignmentId AND score IS NOT NULL")
    suspend fun getAverageScoreByAssignment(assignmentId: String): Float?
    
    @Query("SELECT AVG(score) FROM submissions WHERE assignment_id = :assignmentId AND score IS NOT NULL")
    fun getAverageScoreByAssignmentFlow(assignmentId: String): Flow<Float?>
    
    @Query("SELECT AVG(score) FROM submissions WHERE student_id = :studentId AND score IS NOT NULL")
    suspend fun getAverageScoreByStudent(studentId: String): Float?
    
    @Query("SELECT AVG(score) FROM submissions WHERE student_id = :studentId AND score IS NOT NULL")
    fun getAverageScoreByStudentFlow(studentId: String): Flow<Float?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<SubmissionEntity>)
    
    @Update
    suspend fun updateSubmission(submission: SubmissionEntity)
    
    @Delete
    suspend fun deleteSubmission(submission: SubmissionEntity)
    
    @Query("DELETE FROM submissions WHERE id = :submissionId")
    suspend fun deleteSubmissionById(submissionId: String)
    
    @Query("DELETE FROM submissions WHERE assignment_id = :assignmentId")
    suspend fun deleteSubmissionsByAssignment(assignmentId: String)
    
    @Query("DELETE FROM submissions WHERE student_id = :studentId")
    suspend fun deleteSubmissionsByStudent(studentId: String)
} 