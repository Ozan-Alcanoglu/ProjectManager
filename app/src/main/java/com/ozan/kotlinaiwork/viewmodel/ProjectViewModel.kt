package com.ozan.kotlinaiwork.viewmodel

import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.model.Project
import com.ozan.kotlinaiwork.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Proje ekranı için ViewModel
 */
@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : BaseViewModel<ProjectState, ProjectEvent>() {
    
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    init {
        _uiState.value = ProjectState.Loading
        loadProjects()
    }

    private fun loadProjects() {
        _uiState.value = ProjectState.Loading
        viewModelScope.launch {
            try {
                projectRepository.getAll().collect { projectList ->
                    _projects.value = projectList
                    _uiState.value = ProjectState.Success(projectList)
                }
            } catch (e: Exception) {
                _uiState.value = ProjectState.Error("Projeler yüklenirken bir hata oluştu: ${e.message}")
            }
        }
    }

    fun addProject(project: Project) {
        execute(
            operation = { projectRepository.insert(project) },
            onError = { _uiState.value = ProjectState.Error("Proje eklenirken hata oluştu") }
        )
    }

    fun updateProject(project: Project) {
        execute(
            operation = { projectRepository.update(project) },
            onError = { _uiState.value = ProjectState.Error("Proje güncellenirken hata oluştu") }
        )
    }

    fun deleteProject(project: Project) {
        execute(
            operation = { projectRepository.delete(project) },
            onError = { _uiState.value = ProjectState.Error("Proje silinirken hata oluştu") }
        )
    }
}

/**
 * Proje ekranı durumları
 */
sealed class ProjectState {
    object Loading : ProjectState()
    data class Success(val projects: List<Project>) : ProjectState()
    data class Error(val message: String) : ProjectState()
}

/**
 * Proje ekranı olayları
 */
sealed class ProjectEvent {
    data class ShowMessage(val message: String) : ProjectEvent()
    object NavigateToAddProject : ProjectEvent()
    data class NavigateToProjectDetail(val projectId: String) : ProjectEvent()
}
