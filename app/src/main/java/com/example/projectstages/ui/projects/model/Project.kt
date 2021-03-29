package com.example.projectstages.ui.projects.model

data class Project(
    val id: Long,
    val name: String,
    val type: Int,
    val updateDate: String,
    val countTasksByState: Array<Int>
)