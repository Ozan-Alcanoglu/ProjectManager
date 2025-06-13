package com.ozan.kotlintodoproject.service

import com.ozan.kotlintodoproject.model.Project
import com.ozan.kotlintodoproject.repository.ProjectDao
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

    suspend fun getAllByPriorityDesc() : List<Project>{
        return projectDao.getAllByPriorityDesc()
    }

    suspend fun getAllByPriorityAsc() : List<Project>{
        return projectDao.getAllByPriorityAsc()
    }

    suspend fun getAllByDateDesc(): List<Project>{
        return projectDao.getAllByDateDesc()
    }

    suspend fun getAllByDateAsc(): List<Project>{
        return projectDao.getAllByDateAsc()
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

    suspend fun getProjectsSorted(
        priorityDesc: Boolean,
        dateDesc: Boolean
    ): List<Project> {
        return when {
            priorityDesc && dateDesc -> projectDao.getByPriorityDescDateDesc()
            priorityDesc && !dateDesc -> projectDao.getByPriorityDescDateAsc()
            !priorityDesc && dateDesc -> projectDao.getByPriorityAscDateDesc()
            else -> projectDao.getByPriorityAscDateAsc()
        }
    }


    suspend fun getLowPriorityProjects(): List<Project> {
        return projectDao.getLowPriorityProjects()
    }

    suspend fun getMediumPriorityProjects(): List<Project> {
        return projectDao.getMediumPriorityProjects()
    }

    suspend fun getHighPriorityProjects(): List<Project> {
        return projectDao.getHighPriorityProjects()
    }


}


