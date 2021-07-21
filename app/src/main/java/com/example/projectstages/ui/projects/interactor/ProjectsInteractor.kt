package com.example.projectstages.ui.projects.interactor

import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.ProjectsWithTasks
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface ProjectsInteractor {

    suspend fun getProjects() : ResultWrapper<Flow<List<ProjectsWithTasks>>>

    suspend fun insertProject(projectEntity: ProjectEntity) : ResultWrapper<Long>

    suspend fun deleteProjectById(projectId: Long) : ResultWrapper<Int>

    suspend fun updateProjectById(projectId: Long, name: String, type: Int) : ResultWrapper<Int>
}