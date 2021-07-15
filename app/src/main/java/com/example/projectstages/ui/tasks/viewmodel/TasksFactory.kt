package com.example.projectstages.ui.tasks.viewmodel

import androidx.lifecycle.ViewModel
import com.example.projectstages.base.viewmodel.BaseFactory
import com.example.projectstages.ui.tasks.interactor.TasksInteractor

class TasksFactory(
    private val projectId: Long,
    private val projectName: String,
    private val interactor: TasksInteractor
) : BaseFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TasksViewModel(projectId, projectName,  interactor) as T
    }
}