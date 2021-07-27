package com.example.projectstages.ui.tasks.interactor

import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface TasksInteractor {

    suspend fun getTasks(projectID: Long) : Flow<List<TaskEntity>>
}