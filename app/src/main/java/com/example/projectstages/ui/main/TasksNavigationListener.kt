package com.example.projectstages.ui.main

interface TasksNavigationListener {

    fun goToTask(taskID: Long)

    fun goToAddTask(projectID: Long)
}