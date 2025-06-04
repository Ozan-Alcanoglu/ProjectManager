package com.ozan.kotlintodoproject.service

import com.ozan.kotlintodoproject.model.Branch
import com.ozan.kotlintodoproject.repository.BranchDao
import javax.inject.Inject

class BranchService @Inject constructor(
    private val branchDao: BranchDao
) {



    suspend fun getBranchById(branchId: String): Branch? {
        return branchDao.getBranchById(branchId)
    }


    suspend fun addBranch( name: String):String {
        val branch = Branch(

            name = name,
        )
        branchDao.insertBranch(branch)
        return branch.id
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



}
