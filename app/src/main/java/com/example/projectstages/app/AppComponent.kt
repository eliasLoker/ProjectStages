package com.example.projectstages.app

import android.content.Context
import androidx.room.Room
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.ProjectDatabase
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppComponent {

    @Provides
    fun provideProjectDao(@ApplicationContext context: Context) : ProjectDao = Room
        .databaseBuilder(
            context.applicationContext,
            ProjectDatabase::class.java,
            "project_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        .getProjectDao()

    @Provides
    fun provideInteractor(projectDao: ProjectDao) : ProjectsInteractor {
        return ProjectsInteractor(projectDao)
    }
}