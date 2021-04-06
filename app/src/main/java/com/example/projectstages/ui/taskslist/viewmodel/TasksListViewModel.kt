package com.example.projectstages.ui.taskslist.viewmodel

import androidx.lifecycle.LiveData
import com.example.projectstages.base.BaseViewModelImpl
import com.example.projectstages.ui.taskslist.adapter.TasksListAdapterListener
import com.example.projectstages.ui.taskslist.event.TasksListNavigationEvents

interface TasksListViewModel : BaseViewModelImpl, TasksListAdapterListener {

    val tasksListNavigationEvents: LiveData<TasksListNavigationEvents>

    fun onAddTaskButtonClicked(description: String, taskType: Int)

    fun onGoToAddTaskClicked()

}