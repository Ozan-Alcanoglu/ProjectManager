package com.ozan.kotlinaiwork.service

import com.ozan.kotlinaiwork.model.Task
import com.ozan.kotlinaiwork.repository.TaskDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskService @Inject constructor(
    private val taskDao: TaskDao
) {





    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)
    }


    suspend fun addTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }


    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun getTasksByProject(projectId: String): List<Task> {
        return taskDao.getTasksByProject(projectId)
    }

    suspend fun deleteAllTasksForProject(projectId: String){
        return taskDao.deleteAllTasksForProject(projectId)
    }

    suspend fun loadTasksByProejctId(id:String):List<Task>{
        return taskDao.loadTasksByProejctId(id)
    }


}
