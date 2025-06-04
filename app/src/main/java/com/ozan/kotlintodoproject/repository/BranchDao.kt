package com.ozan.kotlintodoproject.repository

import androidx.room.*
import com.ozan.kotlintodoproject.model.Branch

@Dao
interface BranchDao {

    @Query("SELECT * FROM branches WHERE id = :branchId")
    suspend fun getBranchById(branchId: String): Branch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch)

    @Update
    suspend fun updateBranch(branch: Branch)


    @Delete
    suspend fun deleteBranch(branch: Branch)





    @Query("SELECT COUNT(*) FROM project_items WHERE branchId = :branchId")
    suspend fun getTaskCountForBranch(branchId: String): Int
}
