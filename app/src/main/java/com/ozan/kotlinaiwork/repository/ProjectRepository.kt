package com.ozan.kotlinaiwork.repository

import com.ozan.kotlinaiwork.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Proje verileri için repository arayüzü
 */
interface ProjectRepository {
    suspend fun insert(project: Project)
    suspend fun update(project: Project)
    suspend fun delete(project: Project)
    suspend fun getById(id: String): Project?
    fun getByIdAsFlow(id: String): Flow<Project?>
    fun getAll(): Flow<List<Project>>
    fun getByStatus(status: String): Flow<List<Project>>
    fun search(query: String): Flow<List<Project>>
    suspend fun updateStatus(projectId: String, status: String)
    fun getProgress(projectId: String): Flow<Float>
}

/**
 * ProjectRepository'nin Room veritabanı ile çalışan implementasyonu
 */
@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao
) : ProjectRepository {

    override suspend fun insert(project: Project) {
        projectDao.insert(project)
    }

    override suspend fun update(project: Project) {
        projectDao.update(project)
    }

    override suspend fun delete(project: Project) {
        // İlk olarak projeye ait tüm görevleri sil
        taskDao.deleteByProjectId(project.id)
        // Sonra projeyi sil
        projectDao.delete(project)
    }

    override suspend fun getById(id: String): Project? {
        return projectDao.getById(id)
    }

    override fun getByIdAsFlow(id: String): Flow<Project?> {
        return projectDao.getByIdAsFlow(id)
    }

    override fun getAll(): Flow<List<Project>> {
        return projectDao.getAll()
    }

    override fun getByStatus(status: String): Flow<List<Project>> {
        return projectDao.getByStatus(status)
    }
    
    override fun search(query: String): Flow<List<Project>> {
        return projectDao.search("%$query%")
    }
    
    override suspend fun updateStatus(projectId: String, status: String) {
        projectDao.updateStatus(projectId, status)
    }

    override fun getProgress(projectId: String): Flow<Float> {
        return taskDao.getTotalCount(projectId).map { total ->
            if (total == 0) 0f
            else {
                val completed = taskDao.getCompletedCount(projectId).first()
                completed.toFloat() / total.toFloat()
            }
        }
    }
}

// ProjectDao'ya ek metodlar
fun ProjectDao.getProjectByIdAsFlow(projectId: String): Flow<Project?> = getByIdAsFlow(projectId)
