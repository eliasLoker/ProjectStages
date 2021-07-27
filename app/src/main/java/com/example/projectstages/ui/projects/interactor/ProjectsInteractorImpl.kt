package com.example.projectstages.ui.projects.interactor

import com.example.projectstages.base.viewmodel.BaseInteractor
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
) : BaseInteractor(), ProjectsInteractor {

    override suspend fun getProjects() : Flow<List<ProjectsWithTasks>>
    = withContext(defaultInteractorDispatcher) { projectDao.getProjects() }

    override suspend fun insertProject(projectEntity: ProjectEntity) : Long
    = withContext(defaultInteractorDispatcher) { projectDao.insertProject(projectEntity) }

    override suspend fun deleteProjectById(projectId: Long) : Int
    = withContext(defaultInteractorDispatcher){ projectDao.deleteProjectById(projectId) }

    override suspend fun updateProjectById(projectId: Long, name: String, type: Int) : Int
    = withContext(defaultInteractorDispatcher){ projectDao.updateProjectById(projectId, name, type) }
}