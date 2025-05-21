package com.ozan.kotlinaiwork.repository

import androidx.room.*
import com.ozan.kotlinaiwork.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Görev veritabanı işlemleri için DAO arayüzü
 */
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY `order` ASC")
    fun getByProjectId(projectId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: String): Task?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getByIdAsFlow(id: String): Flow<Task?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)
    @Delete
    suspend fun delete(task: Task)
    
    @Query("DELETE FROM tasks WHERE projectId = :projectId")
    suspend fun deleteByProjectId(projectId: String)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletion(id: String, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM tasks WHERE projectId = :projectId AND isCompleted = 1")
    fun getCompletedCount(projectId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE projectId = :projectId")
    fun getTotalCount(projectId: String): Flow<Int>
}
