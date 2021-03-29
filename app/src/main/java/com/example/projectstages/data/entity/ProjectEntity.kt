package com.example.projectstages.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)], tableName = "projects")
data class ProjectEntity(
    var name: String,
    var type: Int,
    var createdTimestamp: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
