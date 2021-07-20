package com.example.projectstages.ui.task.interactor

import com.example.projectstages.data.entity.TaskEntity

interface TaskInteractor {

    suspend fun getTaskDescriptionByTaskId(taskID: Long) : String

    suspend fun getTaskStateByTaskId(taskID: Long) : Int

    suspend fun insertTask(taskEntity: TaskEntity) : Long

    suspend fun updateTask(id: Long, description: String, type: Int, timestamp: Long) : Int

    suspend fun deleteTask(id: Long) : Int
}