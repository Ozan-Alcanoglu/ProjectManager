package com.ozan.kotlinaiwork.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.model.Project
import com.ozan.kotlinaiwork.service.ProjectService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectService: ProjectService
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    var currentProject by mutableStateOf<Project?>(null)
        private set


    fun loadProjects() {
        viewModelScope.launch {
            try {
                val allProjects = projectService.getAll()
                _projects.value = allProjects
            } catch (e: Exception) {
                Log.e("ProjectViewModel", "Projeler yüklenirken hata oluştu: ${e.message}", e)
            }
        }
    }

    fun showProject(id:String){
        viewModelScope.launch {
            try {
                val project=projectService.getById(id)
                currentProject=project
            }
            catch (e:Exception){
                Log.e("ProjectViewModel", "Proje yüklenirken hata oluştu: ${e.message}", e)
            }
        }
    }

}
