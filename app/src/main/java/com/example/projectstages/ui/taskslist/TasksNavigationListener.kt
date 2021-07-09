package com.example.projectstages.ui.taskslist

interface TasksNavigationListener {

    fun goToTask(taskID: Long)

    fun goToAddTask(projectID: Long)
}