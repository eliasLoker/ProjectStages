package com.example.projectstages.ui.task.inject

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.interactor.TaskInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TaskModule {

    @Provides
    fun providesTaskInteractor(projectDao: ProjectDao) : TaskInteractor
    = TaskInteractorImpl(projectDao)
}