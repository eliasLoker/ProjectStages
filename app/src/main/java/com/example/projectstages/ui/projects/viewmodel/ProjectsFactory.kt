package com.example.projectstages.ui.projects.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor

class ProjectsFactory(
    private val title: String,
    private val subtitle: String,
    private val interactor: ProjectsInteractor
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectsViewModel(title, subtitle, interactor) as T
    }
}