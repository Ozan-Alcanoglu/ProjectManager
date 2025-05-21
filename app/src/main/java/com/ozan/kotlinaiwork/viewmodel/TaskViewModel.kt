package com.ozan.kotlinaiwork.viewmodel

import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.model.Task
import com.ozan.kotlinaiwork.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseViewModel<TaskState, TaskEvent>() {
    
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks
    
    init {
        _uiState.value = TaskState.Loading
    }

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask

    fun loadTasks(projectId: String) {
        viewModelScope.launch {
            taskRepository.getByProjectId(projectId).collect { taskList ->
                _tasks.value = taskList
                _uiState.value = TaskState.Success(taskList)
            }
        }
    }

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            val task = taskRepository.getById(taskId)
            _currentTask.value = task
        }
    }

    fun addTask(task: Task) {
        execute(
            operation = { taskRepository.insert(task) },
            onError = { _uiState.value = TaskState.Error("Görev eklenirken hata oluştu") }
        )
    }

    fun updateTask(task: Task) {
        execute(
            operation = { taskRepository.update(task) },
            onError = { _uiState.value = TaskState.Error("Görev güncellenirken hata oluştu") }
        )
    }

    fun deleteTask(task: Task) {
        execute(
            operation = { taskRepository.delete(task) },
            onError = { _uiState.value = TaskState.Error("Görev silinirken hata oluştu") }
        )
    }

    fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.updateCompletion(taskId, isCompleted)
        }
    }
}

sealed class TaskState {
    object Loading : TaskState()
    data class Success(val tasks: List<Task>) : TaskState()
    data class Error(val message: String) : TaskState()
}

sealed class TaskEvent {
    data class ShowMessage(val message: String) : TaskEvent()
    data class NavigateToTaskDetail(val taskId: String) : TaskEvent()
}
