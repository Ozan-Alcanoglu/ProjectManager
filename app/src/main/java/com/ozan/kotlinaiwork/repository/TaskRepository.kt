package com.ozan.kotlinaiwork.repository

import com.ozan.kotlinaiwork.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Görev verileri için repository arayüzü
 */
interface TaskRepository {
    suspend fun insert(task: Task)
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun getById(id: String): Task?
    fun getByIdAsFlow(id: String): Flow<Task?>
    fun getByProjectId(projectId: String): Flow<List<Task>>
    suspend fun updateCompletion(taskId: String, isCompleted: Boolean)
    fun getCompletedCount(projectId: String): Flow<Int>
    fun getTotalCount(projectId: String): Flow<Int>
    suspend fun deleteByProjectId(projectId: String)
}

/**
 * TaskRepository'nin Room veritabanı ile çalışan implementasyonu
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    override suspend fun update(task: Task) {
        taskDao.update(task)
    }

    override suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    override suspend fun getById(id: String): Task? {
        return taskDao.getById(id)
    }

    override fun getByIdAsFlow(id: String): Flow<Task?> {
        return taskDao.getByIdAsFlow(id)
    }

    override fun getByProjectId(projectId: String): Flow<List<Task>> {
        return taskDao.getByProjectId(projectId)
    }

    override suspend fun updateCompletion(taskId: String, isCompleted: Boolean) {
        taskDao.updateCompletion(taskId, isCompleted)
    }

    override fun getCompletedCount(projectId: String): Flow<Int> {
        return taskDao.getCompletedCount(projectId)
    }

    override fun getTotalCount(projectId: String): Flow<Int> {
        return taskDao.getTotalCount(projectId)
    }
    
    override suspend fun deleteByProjectId(projectId: String) {
        taskDao.deleteByProjectId(projectId)
    }
}

// TaskDao'ya ek metodlar
fun TaskDao.getTaskByIdAsFlow(taskId: String): Flow<Task?> = getByIdAsFlow(taskId)
