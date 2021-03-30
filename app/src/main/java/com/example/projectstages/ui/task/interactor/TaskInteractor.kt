package com.example.projectstages.ui.task.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity

class TaskInteractor(
    private val projectDao: ProjectDao
) {

    suspend fun getTaskDescriptionByTaskId(taskID: Long)
    = projectDao.getTaskDescriptionByTaskId(taskID)

    suspend fun getTaskStateByTaskId(taskID: Long)
    = projectDao.getTaskStateByTaskId(taskID)

    suspend fun insertTask(taskEntity: TaskEntity) = projectDao.insertTask(taskEntity)
}