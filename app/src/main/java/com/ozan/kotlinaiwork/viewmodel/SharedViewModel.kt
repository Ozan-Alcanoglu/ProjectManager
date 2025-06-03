package com.ozan.kotlinaiwork.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ozan.kotlinaiwork.model.Branch
import com.ozan.kotlinaiwork.model.Project
import com.ozan.kotlinaiwork.model.Task
import com.ozan.kotlinaiwork.screens.NestedTextField
import com.ozan.kotlinaiwork.service.BranchService
import com.ozan.kotlinaiwork.service.ProjectService
import com.ozan.kotlinaiwork.service.TaskService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val taskService: TaskService,
    private val branchService: BranchService,
    private val projectService: ProjectService
) : ViewModel() {

    private var itemIdCounter = 0

    fun getNextItemId(): Int {
        return itemIdCounter++
    }


    private val _nestedTextFields = MutableStateFlow<List<NestedTextField>>(emptyList())
    val nestedTextFields: StateFlow<List<NestedTextField>> = _nestedTextFields

    fun updateItems(newItems: List<NestedTextField>) {
        _nestedTextFields.value = newItems
    }

    val items: List<NestedTextField>
        get() = _nestedTextFields.value

    private val _project = MutableStateFlow<Project?>(null)
    val project: StateFlow<Project?> = _project

    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    val branches: StateFlow<List<Branch>> = _branches

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(1)

    fun saveProjectData(titleD: String, descriptionD: String, priorityD: Int) {
        title = titleD
        description = descriptionD
        priority = priorityD
    }

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

    suspend fun addProject(title: String, description: String?, priority: Int): Result<Project> {
        return try {
            val project = Project(
                title = title,
                description = description,
                priority = priority
            )
            val insertedId = projectService.insert(project)
            val insertedProject = project.copy(id = insertedId)
            _project.value = insertedProject
            Result.success(insertedProject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addTask(projectId: String,branchId:String, taskname: String, parentId: String? = null): Result<Task> {
        return try {
            val task = Task(
                branchId=branchId,
                taskname = taskname,
                projectId = projectId,
                parentId = parentId
            )
            taskService.addTask(task)
            _tasks.value = _tasks.value + task
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addBranch(name: String): Result<Branch> {
        return try {
            val branchId = branchService.addBranch(name)
            val insertedBranch = Branch(
                id = branchId,
                name = name
            )
            _branches.value = _branches.value + insertedBranch
            Result.success(insertedBranch)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
