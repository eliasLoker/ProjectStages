package com.example.projectstages.ui.projects.interactor

import android.util.Log
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.ProjectsWithTasks
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProjectsInteractor(
    private val projectDao: ProjectDao
) {

    init {
        Log.d("DependecyInject", "init ProjectsInteractor")
    }

    suspend fun getProjects() : ResultWrapper<Flow<List<ProjectsWithTasks>>> {
        return try {
            withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.getProjects()) }
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    suspend fun insertProject(projectEntity: ProjectEntity)
    = withContext(Dispatchers.IO) { projectDao.insertProject(projectEntity) }

    suspend fun deleteProjectById(projectId: Long)
    = withContext(Dispatchers.IO) { projectDao.deleteProjectById(projectId) }

    suspend fun updateProjectById(projectId: Long, name: String, type: Int)
    = withContext(Dispatchers.IO) { projectDao.updateProjectById(projectId, name, type) }

}