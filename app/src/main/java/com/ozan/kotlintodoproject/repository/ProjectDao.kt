package com.ozan.kotlintodoproject.repository

import androidx.room.*
import com.ozan.kotlintodoproject.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {


    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getById(id: String): Project?

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getByIdAsFlow(id: String): Flow<Project?>

    @Query("SELECT * FROM projects")
    suspend fun getAll(): List<Project>

    @Query("SELECT * FROM projects ORDER BY priority DESC")
    suspend fun getAllByPriorityDesc(): List<Project>

    @Query("SELECT * FROM projects ORDER BY priority ASC")
    suspend fun getAllByPriorityAsc(): List<Project>

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    suspend fun getAllByDateDesc(): List<Project>

    @Query("SELECT * FROM projects ORDER BY createdAt ASC")
    suspend fun getAllByDateAsc(): List<Project>

    @Query("SELECT * FROM projects ORDER BY priority DESC, createdAt DESC")
    suspend fun getByPriorityDescDateDesc(): List<Project>

    @Query("SELECT * FROM projects ORDER BY priority DESC, createdAt ASC")
    suspend fun getByPriorityDescDateAsc(): List<Project>

    @Query("SELECT * FROM projects ORDER BY priority ASC, createdAt DESC")
    suspend fun getByPriorityAscDateDesc(): List<Project>

    @Query("SELECT * FROM projects ORDER BY priority ASC, createdAt ASC")
    suspend fun getByPriorityAscDateAsc(): List<Project>

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


    @Query("SELECT * FROM projects WHERE priority = 0")
    suspend fun getLowPriorityProjects(): List<Project>

    @Query("SELECT * FROM projects WHERE priority = 1")
    suspend fun getMediumPriorityProjects(): List<Project>

    @Query("SELECT * FROM projects WHERE priority = 2")
    suspend fun getHighPriorityProjects(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY completion DESC
    """)
    suspend fun getAllByCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY completion ASC
    """)
    suspend fun getAllByCompletionAsc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority DESC, completion DESC
    """)
    suspend fun getByPriorityDescCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority DESC, completion ASC
    """)
    suspend fun getByPriorityDescCompletionAsc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority ASC, completion DESC
    """)
    suspend fun getByPriorityAscCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority ASC, completion ASC
    """)
    suspend fun getByPriorityAscCompletionAsc(): List<Project>


    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY createdAt DESC, completion DESC
    """)
    suspend fun getByDateDescCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY createdAt DESC, completion ASC
    """)
    suspend fun getByDateDescCompletionAsc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY createdAt ASC, completion DESC
    """)
    suspend fun getByDateAscCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY createdAt ASC, completion ASC
    """)
    suspend fun getByDateAscCompletionAsc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority DESC, createdAt DESC, completion DESC
    """)
    suspend fun getByPriorityDescDateDescCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority DESC, createdAt DESC, completion ASC
    """)
    suspend fun getByPriorityDescDateDescCompletionAsc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority ASC, createdAt ASC, completion DESC
    """)
    suspend fun getByPriorityAscDateAscCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority ASC, createdAt ASC, completion ASC
    """)
    suspend fun getByPriorityAscDateAscCompletionAsc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority DESC, createdAt ASC, completion DESC
    """)
    suspend fun getByPriorityDescDateAscCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority ASC, createdAt DESC, completion DESC
    """)
    suspend fun getByPriorityAscDateDescCompletionDesc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority DESC, createdAt ASC, completion ASC
    """)
    suspend fun getByPriorityDescDateAscCompletionAsc(): List<Project>

    @Query("""
        SELECT p.*,
               (SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id AND pi.isDone = 1) * 100.0 /
               NULLIF((SELECT COUNT(*) FROM project_items pi WHERE pi.projectId = p.id), 0) as completion
        FROM projects p
        ORDER BY priority ASC, createdAt DESC, completion ASC
    """)
    suspend fun getByPriorityAscDateDescCompletionAsc(): List<Project>

}
