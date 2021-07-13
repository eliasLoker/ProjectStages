package com.example.projectstages.ui.taskslist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.base.viewmodel.BaseFactory
import com.example.projectstages.ui.taskslist.interactor.TasksListInteractor

class TasksListFactory(
    private val projectId: Long,
    private val listInteractor: TasksListInteractor
) : BaseFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TasksListViewModel(projectId, listInteractor) as T
    }
}