package com.example.projectstages.ui.taskslist.viewmodel

import androidx.lifecycle.LiveData
import com.example.projectstages.base.BaseViewModelImpl
import com.example.projectstages.ui.taskslist.adapter.TasksListAdapterListener
import com.example.projectstages.ui.taskslist.event.TasksListNavigationEvents

interface TasksListViewModel : BaseViewModelImpl, TasksListAdapterListener {

    val tasks_list_navigationEvents: LiveData<TasksListNavigationEvents>

    fun onAddTaskButtonClicked(description: String, taskType: Int)

    fun onAcceptDeleteTask()

    fun onGoToAddTaskClicked()

//    fun onUpdateButtonClicked(description: String, state: Int)
}