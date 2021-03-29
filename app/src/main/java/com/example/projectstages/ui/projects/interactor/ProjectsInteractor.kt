package com.example.projectstages.ui.projects.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

class ProjectsInteractor(
    private val projectDao: ProjectDao
) {

    suspend fun getProjects() : ResultWrapper<Flow<List<ProjectEntity>>> {
//    suspend fun getProjects() : ResultWrapper<List<ProjectEntity>> {
//    suspend fun getProjects() : ResultWrapper<List<Project>> {
        return try {
            ResultWrapper.Success(projectDao.getProjects())
//            ResultWrapper.Success(projectDao.getProjects().toListProject())
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    suspend fun insertProject(projectEntity: ProjectEntity) : Long {
        return projectDao.insertProject(projectEntity)
    }

//    suspend fun insertProject(projectEntity: ProjectEntity) {
//
//    }

    fun getItemType(index: Int) = if (index % 2 == 0) 0 else 1

    suspend fun countTasksByProjectId(projectID: Long)
    = projectDao.countTasksByProjectId(projectID)

    suspend fun getLastTaskTimestampsByProjectId(projectID: Long)
    = projectDao.getLastTaskTimestampsByProjectId(projectID)

    suspend fun countStatesTasksByProjectId(projectID: Long, taskState: Int)
    = projectDao.countStatesTasksByProjectId(projectID, taskState)
}