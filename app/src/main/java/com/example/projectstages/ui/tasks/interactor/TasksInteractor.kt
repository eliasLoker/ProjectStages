package com.example.projectstages.ui.tasks.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class TasksInteractor(
    private val projectDao: ProjectDao
) {

    fun getTasks(projectID: Long) : ResultWrapper<Flow<List<TaskEntity>>> {
        return try {
            ResultWrapper.Success(projectDao.getTasksByProjectId(projectID))
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    suspend fun deleteTask(id: Long) = projectDao.deleteTaskById(id)

    fun getItemType(index: Int) = if (index % 2 == 0) 0 else 1
}