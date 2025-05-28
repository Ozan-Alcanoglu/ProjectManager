package com.ozan.kotlinaiwork.service

import com.ozan.kotlinaiwork.model.Task
import com.ozan.kotlinaiwork.repository.TaskDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskService @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getRootTasks(projectId: String): Flow<List<Task>> {
        return taskDao.getRootTasksByProject(projectId)
    }


    fun getSubtasks(parentId: String): Flow<List<Task>> {
        return taskDao.getSubtasks(parentId)
    }


    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)
    }


    suspend fun addTask(task: Task) {
        // Sıralama için bir sonraki sırayı al
        val nextOrder = taskDao.getNextSortOrder(task.projectId, task.parentId)
        val newTask = task.copy(sortOrder = nextOrder)
        taskDao.insertTask(newTask)
    }


    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }


    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        // Alt görevleri de sil
        deleteSubtasks(task.id)
    }



    suspend fun moveTask(taskId: String, newParentId: String?, newPosition: Int) {
        val task = taskDao.getTaskById(taskId) ?: return
        
        // Eğer ebeveyn değiştiyse, yeni ebeveynin altına ekle
        if (task.parentId != newParentId) {
            // Eski konumdaki sıralamayı güncelle
            taskDao.incrementTaskOrder(task.projectId, task.parentId, task.sortOrder + 1)
            
            // Yeni konum için sıralamayı güncelle
            taskDao.incrementTaskOrder(task.projectId, newParentId, newPosition)
            
            // Görevi güncelle
            val newTask = task.copy(
                parentId = newParentId,
                sortOrder = newPosition
            )
            taskDao.updateTask(newTask)
        } else {
            // Aynı ebeveyn içinde taşıma
            if (task.sortOrder < newPosition) {
                // Aşağı taşıma
                taskDao.incrementTaskOrder(task.projectId, task.parentId, task.sortOrder + 1)
                taskDao.incrementTaskOrder(task.projectId, task.parentId, newPosition)
            } else {
                // Yukarı taşıma
                taskDao.incrementTaskOrder(task.projectId, task.parentId, newPosition)
                taskDao.incrementTaskOrder(task.projectId, task.parentId, task.sortOrder + 1)
            }
            
            val updatedTask = task.copy(sortOrder = newPosition)
            taskDao.updateTask(updatedTask)
        }
    }


    private suspend fun deleteSubtasks(parentId: String) {
        val subtasks = taskDao.getSubtasks(parentId)
        subtasks.collect { tasks ->
            tasks.forEach { task ->
                deleteTask(task)
            }
        }
    }
}
