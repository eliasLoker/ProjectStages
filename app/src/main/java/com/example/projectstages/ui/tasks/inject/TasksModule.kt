package com.example.projectstages.ui.tasks.inject

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.tasks.interactor.TasksInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TasksModule {

    @Provides
    fun providesTasksInteractor(projectDao: ProjectDao) : TasksInteractor {
        return TasksInteractor(projectDao)
    }
}