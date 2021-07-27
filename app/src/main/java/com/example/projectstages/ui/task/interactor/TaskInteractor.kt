package com.example.projectstages.ui.task.interactor

import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper

interface TaskInteractor {

    suspend fun getTaskByTaskId(taskID: Long) : TaskEntity

    suspend fun insertTask(taskEntity: TaskEntity) : Long

    suspend fun updateTask(id: Long, description: String, type: Int, timestamp: Long) : Int

    suspend fun deleteTask(id: Long) : Int
}