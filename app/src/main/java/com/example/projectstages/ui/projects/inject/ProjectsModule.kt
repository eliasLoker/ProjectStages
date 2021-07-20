package com.example.projectstages.ui.projects.inject

import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.viewmodel.ProjectsFactory
import dagger.Module
import dagger.Provides

@Module
class ProjectsModule {

    @Provides
    @ProjectsScope
    fun providesProjectsInteractor(projectDao: ProjectDao) : ProjectsInteractor {
        return ProjectsInteractor(projectDao)
    }

    @Provides
    @ProjectsScope
    fun providesProjectsFactory(projectsInteractor: ProjectsInteractor) : ViewModelProvider.Factory {
        return ProjectsFactory(projectsInteractor)
    }
}