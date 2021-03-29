package com.example.projectstages.ui.projects.viewmodel

import androidx.lifecycle.LiveData
import com.example.projectstages.base.BaseViewModelImpl
import com.example.projectstages.ui.projects.adapter.ProjectsAdapterListener
import com.example.projectstages.ui.projects.events.ProjectsNavigationEvents

interface ProjectsViewModel : BaseViewModelImpl, ProjectsAdapterListener {

    val _navigationEvents: LiveData<ProjectsNavigationEvents>

    fun onAddButtonClicked(name: String, type: Int)
}