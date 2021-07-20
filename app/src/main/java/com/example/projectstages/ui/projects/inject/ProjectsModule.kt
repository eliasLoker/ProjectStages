package com.example.projectstages.ui.projects.inject

import com.example.projectstages.data.ProjectDao
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.interactor.ProjectsInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ProjectsModule {

    @Provides
    fun providesProjectsInteractor(projectDao: ProjectDao) : ProjectsInteractor
    = ProjectsInteractorImpl(projectDao)
}