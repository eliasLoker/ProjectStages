package com.example.projectstages.app.inject

import com.example.projectstages.ui.projects.ProjectsFragment
import com.example.projectstages.ui.projects.inject.ProjectsModule
import com.example.projectstages.ui.projects.inject.ProjectsScope
import com.example.projectstages.ui.tasks.TasksFragment
import com.example.projectstages.ui.tasks.inject.TasksModule
import com.example.projectstages.ui.tasks.inject.TasksScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentBuilder {

    @ContributesAndroidInjector(modules = [ProjectsModule::class])
    @ProjectsScope
    fun contributeProjectsFragment() : ProjectsFragment

    @ContributesAndroidInjector(modules = [TasksModule::class])
    @TasksScope
    fun contributeTasksFragment() : TasksFragment
}