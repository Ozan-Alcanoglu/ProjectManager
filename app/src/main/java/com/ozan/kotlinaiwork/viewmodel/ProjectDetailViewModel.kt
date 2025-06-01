package com.ozan.kotlinaiwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.model.Branch
import com.ozan.kotlinaiwork.model.Task
import com.ozan.kotlinaiwork.service.BranchService
import com.ozan.kotlinaiwork.service.TaskService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
    object NavigateBack : UiEvent()
    data class Navigate(val route: String) : UiEvent()
}

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val taskService: TaskService,
    private val branchService: BranchService
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    val branches: StateFlow<List<Branch>> = _branches.asStateFlow()

    private val _selectedBranch = MutableStateFlow<Branch?>(null)
    val selectedBranch: StateFlow<Branch?> = _selectedBranch.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var currentProjectId: String? = null

    fun loadProjectData(projectId: String) {
        currentProjectId = projectId
        loadTasks(projectId)
        loadBranches(projectId)
    }

    private fun loadTasks(projectId: String) {
        viewModelScope.launch {
            try {
                val branchId = _selectedBranch.value?.id
                // Get all tasks for the project first
                val allTasks = taskService.getTasksByProject(projectId)
                
                // Filter by branch if a branch is selected
                val filteredTasks = if (branchId != null) {
                    allTasks.filter { task -> task.branchId == branchId }
                } else {
                    allTasks
                }
                
                _tasks.value = filteredTasks.sortedBy { task -> task.sortOrder }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Failed to load tasks: ${e.message}"))
            }
        }
    }

    private fun loadBranches(projectId: String) {
        viewModelScope.launch {
            try {
                branchService.getBranches(projectId)
                    .collect { branches ->
                        _branches.value = branches
                    }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Failed to load branches: ${e.message}"))
            }
        }
    }

    fun selectBranch(branch: Branch?) {
        _selectedBranch.value = branch
        currentProjectId?.let { loadTasks(it) }
    }

    fun addTask(description: String, parentId: String? = null, branchId: String? = null) {
        currentProjectId?.let { projectId ->
            val task = Task(
                description = description,
                projectId = projectId,
                parentId = parentId,
                branchId = branchId ?: _selectedBranch.value?.id,
                sortOrder = _tasks.value.size
            )
            
            viewModelScope.launch {
                try {
                    taskService.addTask(task)
                    _uiEvent.emit(UiEvent.ShowMessage("Task added"))
                    loadTasks(projectId) // Refresh the task list
                } catch (e: Exception) {
                    _uiEvent.emit(UiEvent.ShowMessage("Failed to add task: ${e.message}"))
                }
            }
        } ?: run {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowMessage("No project selected"))
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskService.updateTask(task)
                currentProjectId?.let { loadTasks(it) } // Refresh the task list
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Failed to update task: ${e.message}"))
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskService.deleteTask(task)
                _uiEvent.emit(UiEvent.ShowMessage("Task deleted"))
                currentProjectId?.let { loadTasks(it) } // Refresh the task list
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Failed to delete task: ${e.message}"))
            }
        }
    }

    fun addBranch(name: String, description: String? = null) {
        currentProjectId?.let { projectId ->
            viewModelScope.launch {
                try {
                    branchService.addBranch(projectId, name, description)
                    _uiEvent.emit(UiEvent.ShowMessage("Branch added"))
                    loadBranches(projectId) // Refresh the branch list
                } catch (e: Exception) {
                    _uiEvent.emit(UiEvent.ShowMessage("Failed to add branch: ${e.message}"))
                }
            }
        } ?: run {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowMessage("No project selected"))
            }
        }
    }

    fun updateBranch(branch: Branch) {
        viewModelScope.launch {
            try {
                branchService.updateBranch(branch)
                currentProjectId?.let { loadBranches(it) } // Refresh the branch list
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Failed to update branch: ${e.message}"))
            }
        }
    }

    fun deleteBranch(branch: Branch) {
        viewModelScope.launch {
            try {
                // Check if there are tasks associated with this branch
                val taskCount = branchService.getTaskCountForBranch(branch.id)
                if (taskCount > 0) {
                    _uiEvent.emit(UiEvent.ShowMessage("Cannot delete branch with existing tasks"))
                    return@launch
                }
                
                branchService.deleteBranch(branch)
                _uiEvent.emit(UiEvent.ShowMessage("Branch deleted"))
                
                // If deleted branch was selected, clear selection
                if (_selectedBranch.value?.id == branch.id) {
                    _selectedBranch.value = null
                }
                
                currentProjectId?.let { loadBranches(it) } // Refresh the branch list
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Failed to delete branch: ${e.message}"))
            }
        }
    }

    fun reorderTasks(updatedTasks: List<Task>) {
        viewModelScope.launch {
            try {
                val updatedTasksWithOrder = updatedTasks.mapIndexed { index, task ->
                    task.copy(sortOrder = index)
                }
                // Update each task individually since we don't have a bulk update in the service
                updatedTasksWithOrder.forEach { task ->
                    taskService.updateTask(task)
                }
                currentProjectId?.let { loadTasks(it) } // Refresh the task list
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Failed to reorder tasks: ${e.message}"))
            }
        }
    }
}
