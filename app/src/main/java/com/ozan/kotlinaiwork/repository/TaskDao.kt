package com.ozan.kotlinaiwork.repository

import androidx.room.*
import com.ozan.kotlinaiwork.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM project_items WHERE projectId = :projectId AND parentId IS NULL ORDER BY sortOrder ASC")
    fun getRootTasksByProject(projectId: String): Flow<List<Task>>

    @Query("SELECT * FROM project_items WHERE parentId = :parentId ORDER BY sortOrder ASC")
    fun getSubtasks(parentId: String): Flow<List<Task>>

    @Query("SELECT * FROM project_items WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)


    @Delete
    suspend fun deleteTask(task: Task)


    @Query("DELETE FROM project_items WHERE projectId = :projectId")
    suspend fun deleteAllTasksForProject(projectId: String)

    @Query("UPDATE project_items SET sortOrder = sortOrder + 1 WHERE projectId = :projectId AND parentId = :parentId AND sortOrder >= :position")
    suspend fun incrementTaskOrder(projectId: String, parentId: String?, position: Int)

    @Query("SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM project_items WHERE projectId = :projectId AND parentId = :parentId")
    suspend fun getNextSortOrder(projectId: String, parentId: String?): Int
}
