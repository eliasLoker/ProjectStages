package com.example.projectstages.ui.tasks.inject

import com.example.projectstages.ui.tasks.TasksFragment
import dagger.Subcomponent

@TasksScope
@Subcomponent(modules = [TasksModule::class])
interface TasksComponent {

    fun inject(tasksFragment: TasksFragment)
}