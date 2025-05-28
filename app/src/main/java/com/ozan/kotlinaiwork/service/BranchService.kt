package com.ozan.kotlinaiwork.service

import com.ozan.kotlinaiwork.model.Branch
import com.ozan.kotlinaiwork.repository.BranchDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BranchService @Inject constructor(
    private val branchDao: BranchDao
) {
    fun getBranches(projectId: String): Flow<List<Branch>> {
        return branchDao.getBranchesByProject(projectId)
    }


    suspend fun getBranchById(branchId: String): Branch? {
        return branchDao.getBranchById(branchId)
    }


    suspend fun addBranch(projectId: String, name: String, description: String? = null) {
        val branch = Branch(
            projectId = projectId,
            name = name,
            description = description
        )
        branchDao.insertBranch(branch)
    }


    suspend fun updateBranch(branch: Branch) {
        branchDao.updateBranch(branch)
    }


    suspend fun deleteBranch(branch: Branch) {
        // Bu dala ait tüm görevlerin branchId'sini null yap
        // Bu işlem TaskService üzerinden yapılacak
        branchDao.deleteBranch(branch)
    }


    suspend fun getTaskCountForBranch(branchId: String): Int {
        return branchDao.getTaskCountForBranch(branchId)
    }


    suspend fun deleteAllBranchesForProject(projectId: String) {
        branchDao.deleteAllBranchesForProject(projectId)
    }
}
