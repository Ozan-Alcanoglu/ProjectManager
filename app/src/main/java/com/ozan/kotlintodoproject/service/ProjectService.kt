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

    fun getProgress(): Flow<Float> {
        TODO()
    }


    suspend fun getProjectsSortedByPriorityAndDate(priorityDesc: Boolean, dateDesc: Boolean): List<Project> {
        return when {
            priorityDesc && dateDesc -> projectDao.getByPriorityDescDateDesc()
            priorityDesc && !dateDesc -> projectDao.getByPriorityDescDateAsc()
            !priorityDesc && dateDesc -> projectDao.getByPriorityAscDateDesc()
            else -> projectDao.getByPriorityAscDateAsc()
        }
    }

    suspend fun getProjectsSortedByPriorityAndCompletion(priorityDesc: Boolean, completionDesc: Boolean): List<Project> {
        return when {
            priorityDesc && completionDesc -> projectDao.getByPriorityDescCompletionDesc()
            priorityDesc && !completionDesc -> projectDao.getByPriorityDescCompletionAsc()
            !priorityDesc && completionDesc -> projectDao.getByPriorityAscCompletionDesc()
            else -> projectDao.getByPriorityAscCompletionAsc()
        }
    }

    suspend fun getProjectsSortedByDateAndCompletion(dateDesc: Boolean, completionDesc: Boolean): List<Project> {
        return when {
            dateDesc && completionDesc -> projectDao.getByDateDescCompletionDesc()
            dateDesc && !completionDesc -> projectDao.getByDateDescCompletionAsc()
            !dateDesc && completionDesc -> projectDao.getByDateAscCompletionDesc()
            else -> projectDao.getByDateAscCompletionAsc()
        }
    }

    suspend fun getProjectsSorted(
        priorityDesc: Boolean,
        dateDesc: Boolean,
        completionDesc: Boolean
    ): List<Project> {
        return when {
            priorityDesc && dateDesc && completionDesc -> projectDao.getByPriorityDescDateDescCompletionDesc()
            priorityDesc && dateDesc && !completionDesc -> projectDao.getByPriorityDescDateDescCompletionAsc()
            priorityDesc && !dateDesc && completionDesc -> projectDao.getByPriorityDescDateAscCompletionDesc()
            priorityDesc && !dateDesc && !completionDesc -> projectDao.getByPriorityDescDateAscCompletionAsc()
            !priorityDesc && dateDesc && completionDesc -> projectDao.getByPriorityAscDateDescCompletionDesc()
            !priorityDesc && dateDesc && !completionDesc -> projectDao.getByPriorityAscDateDescCompletionAsc()
            !priorityDesc && !dateDesc && completionDesc -> projectDao.getByPriorityAscDateAscCompletionDesc()
            else -> projectDao.getByPriorityAscDateAscCompletionAsc()
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

    suspend fun getAllByCompletionDesc(): List<Project> {
        return projectDao.getAllByCompletionDesc()
    }

    suspend fun getAllByCompletionAsc(): List<Project> {
        return projectDao.getAllByCompletionAsc()
    }

    suspend fun getByPriorityDescCompletionDesc(): List<Project> {
        return projectDao.getByPriorityDescCompletionDesc()
    }

    suspend fun getByPriorityDescCompletionAsc(): List<Project> {
        return projectDao.getByPriorityDescCompletionAsc()
    }

    suspend fun getByPriorityAscCompletionDesc(): List<Project> {
        return projectDao.getByPriorityAscCompletionDesc()
    }

    suspend fun getByPriorityAscCompletionAsc(): List<Project> {
        return projectDao.getByPriorityAscCompletionAsc()
    }

    suspend fun getByDateDescCompletionDesc(): List<Project> {
        return projectDao.getByDateDescCompletionDesc()
    }

    suspend fun getByDateDescCompletionAsc(): List<Project> {
        return projectDao.getByDateDescCompletionAsc()
    }

    suspend fun getByDateAscCompletionDesc(): List<Project> {
        return projectDao.getByDateAscCompletionDesc()
    }

    suspend fun getByDateAscCompletionAsc(): List<Project> {
        return projectDao.getByDateAscCompletionAsc()
    }

    suspend fun getByPriorityDescDateDescCompletionDesc(): List<Project> {
        return projectDao.getByPriorityDescDateDescCompletionDesc()
    }

    suspend fun getByPriorityDescDateDescCompletionAsc(): List<Project> {
        return projectDao.getByPriorityDescDateDescCompletionAsc()
    }

    suspend fun getByPriorityDescDateAscCompletionDesc(): List<Project> {
        return projectDao.getByPriorityDescDateAscCompletionDesc()
    }

    suspend fun getByPriorityDescDateAscCompletionAsc(): List<Project> {
        return projectDao.getByPriorityDescDateAscCompletionAsc()
    }

    suspend fun getByPriorityAscDateDescCompletionDesc(): List<Project> {
        return projectDao.getByPriorityAscDateDescCompletionDesc()
    }

    suspend fun getByPriorityAscDateDescCompletionAsc(): List<Project> {
        return projectDao.getByPriorityAscDateDescCompletionAsc()
    }

    suspend fun getByPriorityAscDateAscCompletionDesc(): List<Project> {
        return projectDao.getByPriorityAscDateAscCompletionDesc()
    }

    suspend fun getByPriorityAscDateAscCompletionAsc(): List<Project> {
        return projectDao.getByPriorityAscDateAscCompletionAsc()
    }

}


