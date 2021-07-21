package com.example.projectstages.ui.task.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskInteractorImpl @Inject constructor(
    private val projectDao: ProjectDao
) : TaskInteractor {

    override suspend fun getTaskByTaskId(taskID: Long): ResultWrapper<TaskEntity>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.getDescriptionAndStateByTaskId(taskID)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }


    override suspend fun insertTask(taskEntity: TaskEntity) : ResultWrapper<Long>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.insertTask(taskEntity)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }

    override suspend fun updateTask(id: Long, description: String, type: Int, timestamp: Long): ResultWrapper<Int>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.updateTaskById(id, description, type, timestamp)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }

    override suspend fun deleteTask(id: Long): ResultWrapper<Int>
    = try {
        withContext(Dispatchers.IO) { ResultWrapper.Success(projectDao.deleteTaskById(id)) }
    } catch (e: Exception) {
        ResultWrapper.Error(e)
    }
}