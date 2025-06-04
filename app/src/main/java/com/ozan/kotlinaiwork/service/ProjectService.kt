package com.ozan.kotlinaiwork.service

import com.ozan.kotlinaiwork.model.Project
import com.ozan.kotlinaiwork.repository.ProjectDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ProjectService @Inject constructor(
    private val projectDao: ProjectDao,
) {

    suspend fun insert(project: Project): String {
        projectDao.insert(project)
        return project.id
    }

    suspend fun getAll(): List<Project>{
        return projectDao.getAll()
    }

    suspend fun update(project: Project) {
        projectDao.update(project)
    }

    suspend fun delete(project: Project) {

        projectDao.delete(project)
    }

    suspend fun deleteById(id: String) {

        projectDao.deleteById(id)
    }

    suspend fun getById(id: String): Project? {
        return projectDao.getById(id)
    }

    fun getByIdAsFlow(id: String): Flow<Project?> {
        return projectDao.getByIdAsFlow(id)
    }


    fun search(query: String): Flow<List<Project>> {
        return projectDao.search("%$query%")
    }

    suspend fun updateStatus(projectId: String, status: String) {
        projectDao.updateStatus(projectId, status)
    }

    fun getProgress(projectId: String): Flow<Float> {
        TODO()
    }
}

fun ProjectDao.getProjectByIdAsFlow(projectId: String): Flow<Project?> = getByIdAsFlow(projectId)
