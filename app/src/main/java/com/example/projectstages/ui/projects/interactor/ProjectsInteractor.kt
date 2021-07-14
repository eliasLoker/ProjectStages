package com.example.projectstages.ui.projects.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.ProjectsWithTasks
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

class ProjectsInteractor(
    private val projectDao: ProjectDao
) {

    fun getProjects() : ResultWrapper<Flow<List<ProjectsWithTasks>>> {
        return try {
            ResultWrapper.Success(projectDao.getProjects())
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    suspend fun insertProject(projectEntity: ProjectEntity) : Long {
        return projectDao.insertProject(projectEntity)
    }

//    fun getProjectById(projectId: Long) : ResultWrapper<Project> {
//        return try {
//            ResultWrapper.Success(projectDao.getProjectById(projectId))
//        } catch (e: Exception) {
//            ResultWrapper.Error(e)
//        }
//    }

    fun deleteProjectById(projectId: Long) = projectDao.deleteProjectById(projectId)

    fun updateProjectById(projectId: Long, name: String, type: Int) = projectDao.updateProjectById(projectId, name, type)

//    fun deleteProjectById(projectId: Long) = 1
}