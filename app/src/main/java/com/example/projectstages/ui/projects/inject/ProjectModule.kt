package com.example.projectstages.ui.projects.inject

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ProjectModule {

    @Provides
    @ProjectScope
    fun provideProjectInteractor(projectDao: ProjectDao) : ProjectsInteractor {
        return ProjectsInteractor(projectDao)
    }
}