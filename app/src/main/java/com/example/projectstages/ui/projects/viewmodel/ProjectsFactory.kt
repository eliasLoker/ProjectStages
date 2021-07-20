package com.example.projectstages.ui.projects.viewmodel

import androidx.lifecycle.ViewModel
import com.example.projectstages.base.viewmodel.BaseFactory
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.interactor.ProjectsInteractorImpl

class ProjectsFactory(
    private val projectsInteractor: ProjectsInteractor
) : BaseFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectsViewModel(projectsInteractor) as T
    }
}