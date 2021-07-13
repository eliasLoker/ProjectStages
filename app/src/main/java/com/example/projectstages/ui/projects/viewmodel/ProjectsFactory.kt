package com.example.projectstages.ui.projects.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.base.viewmodel.BaseFactory
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor

class ProjectsFactory(
    private val title: String,
    private val subtitle: String,
    private val interactor: ProjectsInteractor
) : BaseFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectsViewModel(title, subtitle, interactor) as T
    }
}