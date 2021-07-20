package com.example.projectstages.ui.tasks.inject

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.tasks.interactor.TasksInteractor
import dagger.Module
import dagger.Provides

@Module
class TasksModule {

    @Provides
    @TasksScope
    fun provideTasksInteractor(projectDao: ProjectDao) : TasksInteractor {
        return TasksInteractor(projectDao)
    }
}