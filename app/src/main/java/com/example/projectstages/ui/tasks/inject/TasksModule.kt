package com.example.projectstages.ui.tasks.inject

import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.tasks.interactor.TasksInteractor
import com.example.projectstages.ui.tasks.viewmodel.TasksFactory
import dagger.Module
import dagger.Provides

@Module
class TasksModule {

    @Provides
    @TasksScope
    fun providesTasksInteractor(projectDao: ProjectDao) : TasksInteractor {
        return TasksInteractor(projectDao)
    }

    @Provides
    @TasksScope
    fun providesTasksFragment(tasksInteractor: TasksInteractor) : ViewModelProvider.Factory {
        return TasksFactory(0L, "Name", tasksInteractor)
    }
}