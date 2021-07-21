package com.example.projectstages.ui.tasks.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TasksInteractorImpl @Inject constructor(
    private val projectDao: ProjectDao
) : TasksInteractor {

    override suspend fun getTasks(projectID: Long) : ResultWrapper<Flow<List<TaskEntity>>>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.getTasksByProjectId(projectID)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }
}