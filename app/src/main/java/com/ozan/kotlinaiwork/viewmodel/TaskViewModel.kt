package com.ozan.kotlinaiwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.model.Task
import com.ozan.kotlinaiwork.service.TaskService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskService: TaskService
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentProjectId: String? = null

    fun loadTasks(projectId: String) {
        currentProjectId = projectId
        viewModelScope.launch {
            try {
                taskService.getRootTasks(projectId).collectLatest { taskList ->
                    _tasks.value = taskList
                }
            } catch (e: Exception) {
                _error.value = "Görevler yüklenirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun loadSubtasks(parentId: String) {
        viewModelScope.launch {
            try {
                taskService.getSubtasks(parentId).collectLatest { subtasks ->
                    _tasks.value = subtasks
                }
            } catch (e: Exception) {
                _error.value = "Alt görevler yüklenirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun selectTask(task: Task) {
        _selectedTask.value = task
    }


    fun clearSelectedTask() {
        _selectedTask.value = null
    }


    fun addTask(projectId: String, description: String, parentId: String? = null) {
        viewModelScope.launch {
            try {
                val task = Task(
                    projectId = projectId,
                    parentId = parentId,
                    description = description
                )
                taskService.addTask(task)
            } catch (e: Exception) {
                _error.value = "Görev eklenirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskService.updateTask(task)
            } catch (e: Exception) {
                _error.value = "Görev güncellenirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskService.deleteTask(task)
                if (_selectedTask.value?.id == task.id) {
                    _selectedTask.value = null
                }
            } catch (e: Exception) {
                _error.value = "Görev silinirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun moveTask(taskId: String, newParentId: String?, newPosition: Int) {
        viewModelScope.launch {
            try {
                taskService.moveTask(taskId, newParentId, newPosition)
            } catch (e: Exception) {
                _error.value = "Görev taşınırken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun clearError() {
        _error.value = null
    }
}
