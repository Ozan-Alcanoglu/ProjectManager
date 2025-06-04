package com.ozan.kotlintodoproject.service

import com.ozan.kotlintodoproject.model.Task
import com.ozan.kotlintodoproject.repository.TaskDao
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

    suspend fun updateTaskIsDone(taskId: String, isDone: Boolean){
        taskDao.updateTaskIsDone(taskId, isDone)
    }


}
