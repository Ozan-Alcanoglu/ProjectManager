package com.ozan.kotlintodoproject.repository

import androidx.room.*
import com.ozan.kotlintodoproject.model.Task

@Dao
interface TaskDao {


    @Query("SELECT * FROM project_items WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)


    @Query("SELECT * FROM project_items WHERE projectId= :id")
    suspend fun loadTasksByProejctId(id:String):List<Task>

    @Delete
    suspend fun deleteTask(task: Task)


    @Query("DELETE FROM project_items WHERE projectId = :projectId")
    suspend fun deleteAllTasksForProject(projectId: String)

    @Query("SELECT * FROM project_items WHERE projectId = :projectId")
    suspend fun getTasksByProject(projectId: String): List<Task>

    @Query("UPDATE project_items SET isDone = :isDone WHERE id = :taskId")
    suspend fun updateTaskIsDone(taskId: String, isDone: Boolean)


}
