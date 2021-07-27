package com.example.projectstages.ui.task.interactor

import com.example.projectstages.base.viewmodel.BaseInteractor
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskInteractorImpl @Inject constructor(
    private val projectDao: ProjectDao
) : BaseInteractor(), TaskInteractor {

    override suspend fun getTaskByTaskId(taskID: Long): TaskEntity
    = withContext(defaultInteractorDispatcher) { projectDao.getDescriptionAndStateByTaskId(taskID) }

    override suspend fun insertTask(taskEntity: TaskEntity) : Long
    = withContext(defaultInteractorDispatcher) { projectDao.insertTask(taskEntity) }

    override suspend fun updateTask(id: Long, description: String, type: Int, timestamp: Long): Int
    = withContext(defaultInteractorDispatcher) { projectDao.updateTaskById(id, description, type, timestamp) }

    override suspend fun deleteTask(id: Long): Int
    = withContext(defaultInteractorDispatcher) { projectDao.deleteTaskById(id) }
}