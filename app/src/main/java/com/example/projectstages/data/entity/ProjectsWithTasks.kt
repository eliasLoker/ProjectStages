package com.example.projectstages.data.entity

import androidx.room.Relation

data class ProjectsWithTasks(
    var id: Long,
    var name: String,
    var type: Int,
    var createdTimestamp: Long,
    @Relation(parentColumn = "id", entityColumn = "projectId")
    var tasks: List<TaskEntity>
)