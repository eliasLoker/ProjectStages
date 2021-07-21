package com.example.projectstages.ui.task.interactor

import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper

interface TaskInteractor {

    suspend fun getTaskByTaskId(taskID: Long) : ResultWrapper<TaskEntity>

    suspend fun insertTask(taskEntity: TaskEntity) : ResultWrapper<Long>

    suspend fun updateTask(id: Long, description: String, type: Int, timestamp: Long) : ResultWrapper<Int>

    suspend fun deleteTask(id: Long) : ResultWrapper<Int>
}