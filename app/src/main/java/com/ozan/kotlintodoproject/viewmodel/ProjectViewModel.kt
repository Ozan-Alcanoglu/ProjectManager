package com.ozan.kotlintodoproject.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozan.kotlintodoproject.model.Project
import com.ozan.kotlintodoproject.model.Task
import com.ozan.kotlintodoproject.service.ProjectService
import com.ozan.kotlintodoproject.service.TaskService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectService: ProjectService,
    private val taskService: TaskService
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    var currentProject by mutableStateOf<Project?>(null)
        private set

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _projectTaskCompletion = MutableStateFlow<Map<String, Pair<Int, Int>>>(emptyMap())
    val projectTaskCompletion: StateFlow<Map<String, Pair<Int, Int>>> = _projectTaskCompletion

    private var priorityDesc: Boolean = true
    private var dateDesc: Boolean = true


    fun loadProjects() {
        viewModelScope.launch {
            try {
                val allProjects = projectService.getAll()
                _projects.value = allProjects
                loadTaskCompletionForAllProjects()
            } catch (e: Exception) {
                Log.e("ProjectViewModel", "Projeler yüklenirken hata oluştu: ${e.message}", e)
            }
        }
    }

    fun showProject(id: String) {
        viewModelScope.launch {
            try {
                val project = projectService.getById(id)
                currentProject = project
            } catch (e: Exception) {
                Log.e("ProjectViewModel", "Proje yüklenirken hata oluştu: ${e.message}", e)
            }
        }
    }

    fun loadTasksByProjectId(id: String) {
        viewModelScope.launch {
            val loadedTasks = try {
                taskService.loadTasksByProejctId(id)
            } catch (e: Exception) {
                emptyList<Task>()
            }
            _tasks.value = loadedTasks
        }
    }

    fun deleteProjectById(id: String) {
        viewModelScope.launch {
            try {
                projectService.deleteById(id)
            } catch (e: Exception) {
                Log.e("ProjectViewmodel", "projeyi silerken hata oldu: {e}", e)
            }
        }
    }

    fun deleteAllTasksForProject(id: String) {
        viewModelScope.launch {
            try {
                taskService.deleteAllTasksForProject(id)
            } catch (e: Exception) {
                Log.e("ProjectViewmodel", "taskları silerken hata oldu: {e}", e)
            }
        }
    }

    fun updateTaskIsDone(taskId: String, isDone: Boolean) {
        viewModelScope.launch {
            try {
                taskService.updateTaskIsDone(taskId = taskId, isDone = isDone)
            } catch (e: Exception) {
                Log.e("ProjectViewmodel", "taskları güncellerken hata oldu: {e}", e)
            }
        }
    }

    fun loadTaskCompletionForAllProjects() {
        viewModelScope.launch {
            val completionMap = mutableMapOf<String, Pair<Int, Int>>()
            projects.value.forEach { project ->
                try {
                    val tasks = taskService.loadTasksByProejctId(project.id)
                    val doneCount = tasks.count { it.isDone }
                    val totalCount = tasks.size
                    completionMap[project.id] = Pair(doneCount, totalCount)
                } catch (e: Exception) {
                    completionMap[project.id] = Pair(0, 0)
                }
            }
            _projectTaskCompletion.value = completionMap
        }
    }


    fun loadProjectsByPriorityDesc() {
        viewModelScope.launch {
            try {
                val sortedProjects = projectService.getAllByPriorityDesc()
                _projects.value = sortedProjects
                loadTaskCompletionForAllProjects()
            } catch (e: Exception) {
                Log.e("ProjectViewModel", "Projeler önceliğe göre azalan sıralamada yüklenirken hata: ${e.message}", e)
            }
        }
    }

    fun loadProjectsByPriorityAsc() {
        viewModelScope.launch {
            try {
                val sortedProjects = projectService.getAllByPriorityAsc()
                _projects.value = sortedProjects
                loadTaskCompletionForAllProjects()
            } catch (e: Exception) {
                Log.e("ProjectViewModel", "Projeler önceliğe göre artan sıralamada yüklenirken hata: ${e.message}", e)
            }
        }
    }

    fun loadProjectsByDateDesc() {
        viewModelScope.launch {
            try {
                val sortedProjects = projectService.getAllByDateDesc()
                _projects.value = sortedProjects
                loadTaskCompletionForAllProjects()
            } catch (e: Exception) {
                Log.e("ProjectViewModel", "Projeler tarihe göre azalan sıralamada yüklenirken hata: ${e.message}", e)
            }
        }
    }

    fun loadProjectsByDateAsc() {
        viewModelScope.launch {
            try {
                val sortedProjects = projectService.getAllByDateAsc()
                _projects.value = sortedProjects
                loadTaskCompletionForAllProjects()
            } catch (e: Exception) {
                Log.e("ProjectViewModel", "Projeler tarihe göre artan sıralamada yüklenirken hata: ${e.message}", e)
            }
        }
    }

    fun loadProjectsSorted(priorityDesc: Boolean, dateDesc: Boolean) {
        this.priorityDesc = priorityDesc
        this.dateDesc = dateDesc

        viewModelScope.launch {
            val result = projectService.getProjectsSorted(priorityDesc, dateDesc)
            _projects.value = result
        }
    }

}
