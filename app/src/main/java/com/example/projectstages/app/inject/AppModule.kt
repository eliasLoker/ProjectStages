package com.example.projectstages.app.inject

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.ProjectDatabase
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    @AppScope
    fun providesApplicationContext(app: Application): Context = app

    @Provides
    @AppScope
    fun providesDatabase(context: Context) : ProjectDatabase = Room
            .databaseBuilder(
                context,
                ProjectDatabase::class.java,
                "project_database"
            )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @AppScope
    fun providesDao(projectDatabase: ProjectDatabase) : ProjectDao
    = projectDatabase.getProjectDao()
}