package com.example.education.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.education.core.database.entities.ChapterEntity
import kotlinx.coroutines.flow.Flow

/**
 * 章节数据访问对象
 * 
 * 提供章节相关的数据库操作方法
 */
@Dao
interface ChapterDao {
    
    @Query("SELECT * FROM chapters WHERE course_id = :courseId ORDER BY chapter_order ASC")
    fun getChaptersByCourse(courseId: String): Flow<List<ChapterEntity>>
    
    @Query("SELECT * FROM chapters WHERE course_id = :courseId ORDER BY chapter_order ASC")
    fun getChaptersByCourseForPaging(courseId: String): PagingSource<Int, ChapterEntity>
    
    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: String): ChapterEntity?
    
    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    fun getChapterByIdFlow(chapterId: String): Flow<ChapterEntity?>
    
    @Query("""
        SELECT * FROM chapters 
        WHERE course_id = :courseId AND chapter_order > :currentOrder 
        ORDER BY chapter_order ASC 
        LIMIT 1
    """)
    suspend fun getNextChapter(courseId: String, currentOrder: Int): ChapterEntity?
    
    @Query("""
        SELECT * FROM chapters 
        WHERE course_id = :courseId AND chapter_order < :currentOrder 
        ORDER BY chapter_order DESC 
        LIMIT 1
    """)
    suspend fun getPreviousChapter(courseId: String, currentOrder: Int): ChapterEntity?
    
    @Query("SELECT COUNT(*) FROM chapters WHERE course_id = :courseId")
    suspend fun getChapterCountByCourse(courseId: String): Int
    
    @Query("SELECT COUNT(*) FROM chapters WHERE course_id = :courseId")
    fun getChapterCountByCourseFlow(courseId: String): Flow<Int>
    
    @Query("""
        SELECT * FROM chapters 
        WHERE title LIKE '%' || :query || '%' 
           OR content LIKE '%' || :query || '%'
        ORDER BY chapter_order ASC
    """)
    fun searchChapters(query: String): Flow<List<ChapterEntity>>
    
    @Query("""
        SELECT * FROM chapters 
        WHERE course_id = :courseId 
          AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        ORDER BY chapter_order ASC
    """)
    fun searchChaptersInCourse(courseId: String, query: String): Flow<List<ChapterEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)
    
    @Update
    suspend fun updateChapter(chapter: ChapterEntity)
    
    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)
    
    @Query("DELETE FROM chapters WHERE id = :chapterId")
    suspend fun deleteChapterById(chapterId: String)
    
    @Query("DELETE FROM chapters WHERE course_id = :courseId")
    suspend fun deleteChaptersByCourse(courseId: String)
    
    @Query("SELECT MAX(chapter_order) FROM chapters WHERE course_id = :courseId")
    suspend fun getMaxOrderIndex(courseId: String): Int?
    
    @Query("""
        UPDATE chapters 
        SET chapter_order = chapter_order + 1 
        WHERE course_id = :courseId AND chapter_order >= :startOrder
    """)
    suspend fun shiftChaptersDown(courseId: String, startOrder: Int)
    
    @Query("""
        UPDATE chapters 
        SET chapter_order = chapter_order - 1 
        WHERE course_id = :courseId AND chapter_order > :deletedOrder
    """)
    suspend fun shiftChaptersUp(courseId: String, deletedOrder: Int)
} 