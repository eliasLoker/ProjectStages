package com.example.projectstages.ui.projects.inject

import androidx.fragment.app.Fragment
import com.example.projectstages.ui.projects.ProjectsFragment
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Singleton

@ProjectScope
@Subcomponent(modules = [ProjectModule::class])
interface ProjectComponent {

    fun inject(projectsFragment: ProjectsFragment)
}
//    @Subcomponent.Factory
//    interface Factory {
//        @BindsInstance fun fragment(fragment: Fragment) : Factory
//        fun build() : ProjectComponent
//    }
//
//    fun inject(projectsFragment: ProjectsFragment)