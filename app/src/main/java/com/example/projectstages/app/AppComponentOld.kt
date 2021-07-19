package com.example.projectstages.app

import android.content.Context
import androidx.room.Room
import com.example.projectstages.data.ProjectDao
import com.example.projectstages.data.ProjectDatabase

class AppComponentOld(context: Context) {

    val projectDao: ProjectDao = Room
        .databaseBuilder(
            context.applicationContext,
            ProjectDatabase::class.java,
            "project_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        .getProjectDao()
}