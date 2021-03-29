package com.example.projectstages.ui.taskslist.model

data class Task(
    val id: Long,
    val description: String,
    val date: String,
    val type: Int,
    val state: Int
)
