package com.ozan.kotlinaiwork.repository

import androidx.room.*
import com.ozan.kotlinaiwork.model.Branch
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {
    @Query("SELECT * FROM branches WHERE projectId = :projectId ORDER BY name ASC")
    fun getBranchesByProject(projectId: String): Flow<List<Branch>>

    @Query("SELECT * FROM branches WHERE id = :branchId")
    suspend fun getBranchById(branchId: String): Branch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch)

    @Update
    suspend fun updateBranch(branch: Branch)


    @Delete
    suspend fun deleteBranch(branch: Branch)


    @Query("DELETE FROM branches WHERE projectId = :projectId")
    suspend fun deleteAllBranchesForProject(projectId: String)


    @Query("SELECT COUNT(*) FROM project_items WHERE branchId = :branchId")
    suspend fun getTaskCountForBranch(branchId: String): Int
}
