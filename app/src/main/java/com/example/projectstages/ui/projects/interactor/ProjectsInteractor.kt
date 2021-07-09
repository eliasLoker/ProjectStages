package com.example.projectstages.ui.projects.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.ProjectsWithTasks
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

class ProjectsInteractor(
    private val projectDao: ProjectDao
) {

    fun getProjects2() : ResultWrapper<Flow<List<ProjectsWithTasks>>> {
        return try {
            ResultWrapper.Success(projectDao.getProjects2())
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    suspend fun insertProject(projectEntity: ProjectEntity) : Long {
        return projectDao.insertProject(projectEntity)
    }
}