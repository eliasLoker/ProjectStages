package com.example.projectstages.ui.task.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.ui.task.interactor.TaskInteractor

class TaskFactory(
    private val isEdit: Boolean,
    private val projectID: Long,
    private val taskID: Long?,
    private val taskInteractor: TaskInteractor
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TaskViewModel(isEdit, projectID, taskID, taskInteractor) as T
    }
}