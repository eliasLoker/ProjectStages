package com.example.projectstages.ui.taskslist.interactor

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class TasksListInteractor(
    private val projectDao: ProjectDao
) {

    fun getTasks(projectID: Long) : ResultWrapper<Flow<List<TaskEntity>>> {
        return try {
            ResultWrapper.Success(projectDao.getTasksByProjectId(projectID))
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    suspend fun insertTask(taskEntity: TaskEntity) = projectDao.insertTask(taskEntity)

    suspend fun updateTask(id: Long, description: String, type: Int) = projectDao.updateTaskById(id, description, type)

    suspend fun deleteTask(id: Long) = projectDao.deleteTaskById(id)

    fun getItemType(index: Int) = if (index % 2 == 0) 0 else 1
}