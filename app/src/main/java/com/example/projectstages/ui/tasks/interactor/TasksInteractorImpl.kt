package com.example.projectstages.ui.tasks.interactor

import com.example.projectstages.base.viewmodel.BaseInteractor
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TasksInteractorImpl @Inject constructor(
    private val projectDao: ProjectDao
) : BaseInteractor(), TasksInteractor {

    override suspend fun getTasks(projectID: Long) : Flow<List<TaskEntity>>
    = withContext(defaultInteractorDispatcher) { projectDao.getTasksByProjectId(projectID) }
}