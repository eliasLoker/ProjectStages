package com.example.projectstages.ui.task.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskInteractorImpl(
    private val projectDao: ProjectDao
) : TaskInteractor {

    override suspend fun getTaskDescriptionByTaskId(taskID: Long)
    = withContext(Dispatchers.IO) { projectDao.getTaskDescriptionByTaskId(taskID) }

    override suspend fun getTaskStateByTaskId(taskID: Long)
    = withContext(Dispatchers.IO) { projectDao.getTaskStateByTaskId(taskID) }

    override suspend fun insertTask(taskEntity: TaskEntity)
    = withContext(Dispatchers.IO) { projectDao.insertTask(taskEntity) }

    override suspend fun updateTask(id: Long, description: String, type: Int, timestamp: Long)
    = withContext(Dispatchers.IO) { projectDao.updateTaskById(id, description, type, timestamp) }

    override suspend fun deleteTask(id: Long)
    = withContext(Dispatchers.IO) { projectDao.deleteTaskById(id) }
}