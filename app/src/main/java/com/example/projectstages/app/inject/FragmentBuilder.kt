package com.example.projectstages.app.inject

import com.example.projectstages.ui.projects.ProjectsFragment
import com.example.projectstages.ui.projects.inject.ProjectModule
import com.example.projectstages.ui.projects.inject.ProjectScope
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
interface FragmentBuilder {

    @ContributesAndroidInjector(modules = [ProjectModule::class])
    @ProjectScope
    fun contributeProjectsFragment() : ProjectsFragment
}