package com.example.projectstages.ui.tasks.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TasksInteractor(
    private val projectDao: ProjectDao
) {

    suspend fun getTasks(projectID: Long) : ResultWrapper<Flow<List<TaskEntity>>> {
        return try {
            withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.getTasksByProjectId(projectID)) }
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }
}