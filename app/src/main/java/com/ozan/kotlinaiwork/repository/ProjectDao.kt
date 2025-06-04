package com.ozan.kotlinaiwork.repository

import androidx.room.*
import com.ozan.kotlinaiwork.model.Project
import kotlinx.coroutines.flow.Flow

/**
 * Proje veritabanı işlemleri için DAO arayüzü
 */
@Dao
interface ProjectDao {


    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getById(id: String): Project?

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getByIdAsFlow(id: String): Flow<Project?>

    @Query("SELECT * FROM projects")
    suspend fun getAll(): List<Project>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: Project)

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)

    @Query("DELETE FROM projects")
    suspend fun deleteAll()

    @Query("DELETE FROM projects WHERE id= :id")
    suspend fun deleteById(id:String)

    @Query("SELECT * FROM projects WHERE title LIKE :query OR description LIKE :query")
    fun search(query: String): Flow<List<Project>>

    @Query("UPDATE projects SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}
