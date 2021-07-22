package com.example.projectstages.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = CASCADE,
        )
    ],
indices = [Index("projectId", "description",  unique = true)]

)
data class TaskEntity(
    var projectId: Long,
    var description: String,
    var state: Int,
    var updatedTimestamp: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}