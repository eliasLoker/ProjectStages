package com.example.projectstages.ui.projects.events

sealed class ProjectsNavigationEvents {

    object SuccessAddDialog : ProjectsNavigationEvents()

    object FailureAddDialog : ProjectsNavigationEvents()

    class GoToProjectDetails(
        val projectID: Long
    ) : ProjectsNavigationEvents()
}