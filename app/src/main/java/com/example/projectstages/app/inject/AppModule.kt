package com.example.projectstages.app.inject

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.projectstages.app.App
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.ProjectDatabase
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    @AppScope
    fun provideApplicationContext(app: Application): Context = app

    @Provides
    @AppScope
    fun provideDatabase(context: Context) : ProjectDatabase {
        Log.d("DependecyInject", "init provideDatabase")
        return Room
            .databaseBuilder(
                context,
                ProjectDatabase::class.java,
                "project_database"
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @AppScope
    fun provideDao(projectDatabase: ProjectDatabase) : ProjectDao {
        return projectDatabase.getProjectDao()
    }
}