package com.ozan.kotlinaiwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozan.kotlinaiwork.model.Branch
import com.ozan.kotlinaiwork.service.BranchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BranchViewModel @Inject constructor(
    private val branchService: BranchService
) : ViewModel() {

    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    val branches: StateFlow<List<Branch>> = _branches

    private val _selectedBranch = MutableStateFlow<Branch?>(null)
    val selectedBranch: StateFlow<Branch?> = _selectedBranch

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentProjectId: String? = null

    fun loadBranches(projectId: String) {
        currentProjectId = projectId
        viewModelScope.launch {
            try {
                branchService.getBranches(projectId).collectLatest { branchList ->
                    _branches.value = branchList
                }
            } catch (e: Exception) {
                _error.value = "Dallar yüklenirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun selectBranch(branch: Branch) {
        _selectedBranch.value = branch
    }


    fun clearSelectedBranch() {
        _selectedBranch.value = null
    }


    fun addBranch(projectId: String, name: String, description: String? = null) {
        viewModelScope.launch {
            try {
                branchService.addBranch(projectId, name, description)
            } catch (e: Exception) {
                _error.value = "Dal eklenirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun updateBranch(branch: Branch) {
        viewModelScope.launch {
            try {
                branchService.updateBranch(branch)
            } catch (e: Exception) {
                _error.value = "Dal güncellenirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun deleteBranch(branch: Branch) {
        viewModelScope.launch {
            try {
                // Önce bu dala ait görev sayısını kontrol et
                val taskCount = branchService.getTaskCountForBranch(branch.id)
                if (taskCount > 0) {
                    _error.value = "Bu dala ait $taskCount görev bulunuyor. Önce bu görevleri başka bir dala taşıyın veya silin."
                    return@launch
                }
                
                branchService.deleteBranch(branch)
                if (_selectedBranch.value?.id == branch.id) {
                    _selectedBranch.value = null
                }
            } catch (e: Exception) {
                _error.value = "Dal silinirken bir hata oluştu: ${e.message}"
            }
        }
    }


    fun clearError() {
        _error.value = null
    }
}
