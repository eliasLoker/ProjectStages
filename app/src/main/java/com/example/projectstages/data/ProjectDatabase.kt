package com.example.projectstages.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.TaskEntity

@Database(entities = [ProjectEntity::class, TaskEntity::class], version = 1)
abstract class ProjectDatabase : RoomDatabase() {

    abstract fun getProjectDao() : ProjectDao
}