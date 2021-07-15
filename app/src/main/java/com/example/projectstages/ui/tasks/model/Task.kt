package com.example.projectstages.ui.tasks.model

data class Task(
    val id: Long,
    val description: String,
    val date: String,
    val type: Int,
    val state: Int
)
