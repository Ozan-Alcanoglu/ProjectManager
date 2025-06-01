package com.ozan.kotlinaiwork.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.model.Branch
import com.ozan.kotlinaiwork.model.Project
import com.ozan.kotlinaiwork.model.Task
import com.ozan.kotlinaiwork.screens.NestedTextField
import com.ozan.kotlinaiwork.service.BranchService
import com.ozan.kotlinaiwork.service.ProjectRepositoryImpl
import com.ozan.kotlinaiwork.service.TaskService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val taskService: TaskService,
    private val branchService: BranchService,
    private val projectService: ProjectRepositoryImpl
) : ViewModel() {


    // SharedViewModel.kt içine eklenecek
    private val _nestedTextFields = MutableStateFlow<List<NestedTextField>>(emptyList())
    val nestedTextFields: StateFlow<List<NestedTextField>> = _nestedTextFields

    fun updateItems(newItems: List<NestedTextField>) {
        _nestedTextFields.value = newItems
    }

    // items property'si için
    val items: List<NestedTextField>
        get() = _nestedTextFields.value


    private val _project = MutableStateFlow<Project?>(null)
    val project: StateFlow<Project?> = _project

    // Branch ve Task geçici verileri
    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    val branches: StateFlow<List<Branch>> = _branches

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun setProject(project: Project) {
        _project.value = project
    }

    fun setBranches(branchList: List<Branch>) {
        _branches.value = branchList
    }

    fun setTasks(taskList: List<Task>) {
        _tasks.value = taskList
    }

    fun addBranch(branch: Branch) {
        _branches.value = _branches.value + branch
    }

    fun addTask(task: Task) {
        _tasks.value = _tasks.value + task
    }

    fun clearAll() {
        _project.value = null
        _branches.value = emptyList()
        _tasks.value = emptyList()
    }

    var titleD by mutableStateOf("")
    var descriptionD by mutableStateOf("")
    var priorityD by mutableStateOf(0)

    fun saveProjectData(title: String, description: String,  priority: Int) {
        titleD = title
        descriptionD = description
        priorityD = priority
    }


    fun addProject(title: String, description: String? = null, priority: Int = 2) {
        viewModelScope.launch {
            try {
                val project = Project(
                    title = title,
                    description = description,
                    priority = priority
                )
                projectService.insert(project)
                _project.value = project
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun addTask(
        projectId: String,
        description: String,
        parentId: String? = null,
        branchId: String? = null
    ) {
        viewModelScope.launch {
            try {
                val task = Task(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    description = description,
                    parentId = parentId,
                    branchId = branchId
                )
                taskService.addTask(task)
                _tasks.value = _tasks.value + task
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun addBranch(name: String, description: String? = null) {
        viewModelScope.launch {
            try {
                branchService.addBranch(
                    name = name,
                    description = description
                )
                val branch = Branch(
                    id = UUID.randomUUID().toString(),
                    name = name
                )
                _branches.value = _branches.value + branch
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
