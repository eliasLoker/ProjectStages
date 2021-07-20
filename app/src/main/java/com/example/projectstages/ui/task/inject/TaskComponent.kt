package com.example.projectstages.ui.task.inject

import com.example.projectstages.ui.task.TaskFragment
import dagger.Subcomponent

@TaskScope
@Subcomponent(modules = [TaskModule::class])
interface TaskComponent {

    fun inject(taskFragment: TaskFragment)
}