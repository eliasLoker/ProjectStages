package com.example.projectstages.ui.tasks.inject

import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.tasks.TasksFragment
import com.example.projectstages.ui.tasks.interactor.TasksInteractor
import com.example.projectstages.ui.tasks.viewmodel.TasksFactory
import com.example.projectstages.utils.getLongFromBundleExt
import com.example.projectstages.utils.getStringFromBundleExt
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
    fun providesTasksFactory(tasksFragment: TasksFragment, tasksInteractor: TasksInteractor) : ViewModelProvider.Factory {
        return TasksFactory(
            tasksFragment.arguments.getLongFromBundleExt(TasksFragment.TAG_FOR_PROJECT_ID),
            tasksFragment.arguments.getStringFromBundleExt(TasksFragment.TAG_FOR_PROJECT_NAME),
            tasksInteractor
        )
    }
}