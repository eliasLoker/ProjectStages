package com.example.projectstages.ui.task.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskInteractor(
    private val projectDao: ProjectDao
) {

    suspend fun getTaskDescriptionByTaskId(taskID: Long)
    = withContext(Dispatchers.IO) { projectDao.getTaskDescriptionByTaskId(taskID) }

    suspend fun getTaskStateByTaskId(taskID: Long)
    = withContext(Dispatchers.IO) { projectDao.getTaskStateByTaskId(taskID) }

    suspend fun insertTask(taskEntity: TaskEntity)
    = withContext(Dispatchers.IO) { projectDao.insertTask(taskEntity) }

    suspend fun updateTask(id: Long, description: String, type: Int, timestamp: Long)
    = withContext(Dispatchers.IO) { projectDao.updateTaskById(id, description, type, timestamp) }

    suspend fun deleteTask(id: Long)
    = withContext(Dispatchers.IO) { projectDao.deleteTaskById(id) }
}