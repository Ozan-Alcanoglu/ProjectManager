package com.ozan.kotlinaiwork.viewmodel

import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.service.ProjectRepository
import com.ozan.kotlinaiwork.model.Project
import com.ozan.kotlinaiwork.ui.theme.Strings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProjectEditViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _state = mutableStateOf(ProjectEditState())
    val state = _state

    private val _eventFlow = MutableSharedFlow<ProjectEditEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val project = repository.getById(projectId)
                project?.let { updateStateFromProject(it) }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Bir hata oluştu",
                    isLoading = false
                )
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun onEvent(event: ProjectEditEvent) {
        when (event) {
            is ProjectEditEvent.TitleChanged -> {
                _state.value = _state.value.copy(
                    title = event.value,
                    titleError = null
                )
            }
            is ProjectEditEvent.DescriptionChanged -> {
                _state.value = _state.value.copy(description = event.value)
            }
            is ProjectEditEvent.CategoryChanged -> {
                _state.value = _state.value.copy(category = event.value)
            }
            is ProjectEditEvent.PriorityChanged -> {
                _state.value = _state.value.copy(priority = event.value)
            }
            ProjectEditEvent.Save -> saveProject()
            is ProjectEditEvent.ShowMessage -> {
                // Snackbar için mesajı state'e kaydet
                _state.value = _state.value.copy(error = event.message)
            }
        }
    }

    private fun updateStateFromProject(project: Project) {
        _state.value = _state.value.copy(
            projectId = project.id,
            title = project.title,
            description = project.description ?: "",
            category = project.category ?: "",
            priority = project.priority.toString(),
            startDate = project.startDate,
            isLoading = false
        )
    }

    private fun saveProject() {
        val title = _state.value.title.trim()
        val description = _state.value.description.trim()
        val category = _state.value.category.trim()
        val priority = _state.value.priority.toIntOrNull() ?: 0

        // Doğrulama
        if (title.isBlank()) {
            _state.value = _state.value.copy(
                titleError = "Başlık zorunludur",
                error = null,
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val project = Project(
                    id = _state.value.projectId.ifEmpty { UUID.randomUUID().toString() },
                    ownerId = "asd",
                    title = title,
                    description = if (description.isNotBlank()) description else null,
                    category = if (category.isNotBlank()) category else null,
                    priority = try { priority.toInt() } catch (e: NumberFormatException) { 2 },
                    startDate = if (_state.value.projectId.isEmpty()) System.currentTimeMillis() else _state.value.startDate,
                    status = "active"
                )

                if (_state.value.projectId.isNotEmpty()) {
                    repository.update(project)
                } else {
                    repository.insert(project)
                }
                _eventFlow.emit(ProjectEditEvent.Save)
                _state.value = _state.value.copy(isSaved = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: Strings.ERROR_OCCURRED,
                    isLoading = false
                )
                val errorMsg = "Bir hata oluştu: ${e.message}"
                _eventFlow.emit(ProjectEditEvent.ShowMessage(errorMsg))
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}

data class ProjectEditState(
    val projectId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val priority: String = "0",
    val startDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val titleError: String? = null,
    val isSaved: Boolean = false
)

sealed class ProjectEditEvent {
    data class TitleChanged(val value: String) : ProjectEditEvent()
    data class DescriptionChanged(val value: String) : ProjectEditEvent()
    data class CategoryChanged(val value: String) : ProjectEditEvent()
    data class PriorityChanged(val value: String) : ProjectEditEvent()
    object Save : ProjectEditEvent()
    data class ShowMessage(val message: String) : ProjectEditEvent()
}
