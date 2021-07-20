package com.example.projectstages.ui.task.inject

import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.task.TaskFragment
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.viewmodel.TaskFactory
import com.example.projectstages.utils.getBooleanFromBundleExt
import com.example.projectstages.utils.getLongFromBundleExt
import dagger.Module
import dagger.Provides

@Module
class TaskModule {

    @TaskScope
    @Provides
    fun providesInteractor(projectDao: ProjectDao) : TaskInteractor {
        return TaskInteractor(projectDao)
    }

    @Provides
    @TaskScope
    fun providesTaskFactory(tasksFragment: TaskFragment, taskInteractor: TaskInteractor) : ViewModelProvider.Factory {
        return TaskFactory(
            tasksFragment.arguments.getBooleanFromBundleExt(TaskFragment.IS_EDIT),
            tasksFragment.arguments.getLongFromBundleExt(TaskFragment.PROJECT_ID),
            tasksFragment.arguments.getLongFromBundleExt(TaskFragment.TASK_ID),
            taskInteractor
        )
    }
}