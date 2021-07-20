package com.example.projectstages.ui.projects.inject

import com.example.projectstages.ui.projects.ProjectsFragment
import dagger.Subcomponent

@ProjectsScope
@Subcomponent(modules = [ProjectsModule::class])
interface ProjectsComponent {

    fun inject(projectsFragment: ProjectsFragment)
}
//    @Subcomponent.Factory
//    interface Factory {
//        @BindsInstance fun fragment(fragment: Fragment) : Factory
//        fun build() : ProjectComponent
//    }
//
//    fun inject(projectsFragment: ProjectsFragment)