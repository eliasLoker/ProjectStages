package com.example.projectstages.ui.task.viewmodel

import androidx.lifecycle.ViewModel
import com.example.projectstages.base.viewmodel.BaseFactory
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.interactor.TaskInteractorImpl

class TaskFactory(
    private val isEdit: Boolean,
    private val projectID: Long,
    private val taskID: Long,
    private val taskInteractor: TaskInteractor
) : BaseFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TaskViewModel(isEdit, projectID, taskID, taskInteractor) as T
    }
}