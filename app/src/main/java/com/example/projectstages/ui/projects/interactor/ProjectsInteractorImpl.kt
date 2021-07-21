package com.example.projectstages.ui.projects.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.ProjectsWithTasks
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProjectsInteractorImpl @Inject constructor(
    private val projectDao: ProjectDao
) : ProjectsInteractor {

    override suspend fun getProjects() : ResultWrapper<Flow<List<ProjectsWithTasks>>>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.getProjects()) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }


    override suspend fun insertProject(projectEntity: ProjectEntity) : ResultWrapper<Long>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.insertProject(projectEntity)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }

    override suspend fun deleteProjectById(projectId: Long) : ResultWrapper<Int>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.deleteProjectById(projectId)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }

    override suspend fun updateProjectById(projectId: Long, name: String, type: Int) : ResultWrapper<Int>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.updateProjectById(projectId, name, type)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }
}