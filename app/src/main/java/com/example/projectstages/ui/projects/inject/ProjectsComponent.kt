package com.example.projectstages.ui.projects.inject

import com.example.projectstages.ui.projects.ProjectsFragment
import dagger.Subcomponent

@ProjectsScope
@Subcomponent(modules = [ProjectsModule::class])
interface ProjectsComponent {

    fun inject(projectsFragment: ProjectsFragment)
}