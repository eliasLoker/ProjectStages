package com.example.projectstages.ui.taskslist.event

sealed class TasksListNavigationEvents {

    object SuccessAddDialog : TasksListNavigationEvents()

    object FailureAddDialog : TasksListNavigationEvents()

//    object ShowDeleteTaskDialog : TasksListNavigationEvents()

    object SuccessDelete : TasksListNavigationEvents()

    object FailureDelete : TasksListNavigationEvents()

//    class ShowEditTaskDialog(
//        val description: String,
//        val type: Int
//    ) : TasksListNavigationEvents()

    class GoToTask(
        val taskID: Long
    ) : TasksListNavigationEvents()

    object SuccessUpdate : TasksListNavigationEvents()

    object FailureUpdate : TasksListNavigationEvents()
}