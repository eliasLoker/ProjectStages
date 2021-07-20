package com.example.projectstages.app

import android.content.Context
import androidx.room.Room
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.ProjectDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppComponent {

    @Provides
    @Singleton
    fun providesProjectDatabase(@ApplicationContext context: Context) : ProjectDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            ProjectDatabase::class.java,
            "project_database"
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providesProjectDao(projectDatabase: ProjectDatabase) : ProjectDao
    = projectDatabase.getProjectDao()
}