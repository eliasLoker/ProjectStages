package com.example.projectstages.ui.tasks.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.*
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.ui.tasks.interactor.TasksInteractor
import com.example.projectstages.ui.tasks.model.Task
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class TasksViewModel(
    private val projectId: Long,
    private val projectName: String,
    private val tasksInteractor: TasksInteractor
) : BaseViewModel<
        TasksContract.ViewState,
        TasksContract.Action,
        TasksContract.ViewEffect,
        TasksContract.ViewEvent
        >(TasksContract.ViewState()) {

    private val _tasks = ArrayList<Task>()

    init {
        viewModelScope.launch {
//            delay(1000)
            when(val tasks = tasksInteractor.getTasks(projectId)) {
                is ResultWrapper.Success -> {
                    tasks.data.collectLatest {
                        when(it.isNotEmpty()) {
                            true -> {
                                this@TasksViewModel._tasks.clear()
                                it.forEach { taskEntity ->
                                    val task = Task(
                                        taskEntity.id,
                                        taskEntity.description,
                                        Constants.userFormatTasks.format(Date(taskEntity.createdTimestamp)),
                                        taskEntity.state
                                    )
                                    _tasks.add(task)
                                }
                                _tasks.sortBy { i -> i.state }
                                sendAction(TasksContract.Action.NotEmptyList(_tasks, projectName))
                            }
                            false -> sendAction(TasksContract.Action.EmptyList)
                        }
                    }
                }
                is ResultWrapper.Error
                -> sendAction(TasksContract.Action.Error)
            }
        }
    }

    override fun processViewEvent(viewEvent: TasksContract.ViewEvent) {
        when(viewEvent) {
            is TasksContract.ViewEvent.OnTaskClicked
            -> sendViewEffect(TasksContract.ViewEffect.GoToTask(viewEvent.taskId))

            is TasksContract.ViewEvent.OnAddTaskClicked
            -> sendViewEffect(TasksContract.ViewEffect.GoToAddTask(projectId))
        }
    }

    override fun onReduceState(viewAction: TasksContract.Action): TasksContract.ViewState {
        return when (viewAction) {
            is TasksContract.Action.Loading
            -> state.copy(
                progressBarVisibility = true
            )

            is TasksContract.Action.EmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                taskRecyclerVisibility = false,
                errorMessageTextViewType = Constants.EmptyList.EMPTY,
                errorMessageTextViewVisibility = true,
                headerViewsVisibility = true,
                addTaskButtonVisibility = true
            )

            is TasksContract.Action.NotEmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = false,
                taskRecyclerVisibility = true,
                tasks = viewAction.tasks,
                projectName = projectName,
                headerViewsVisibility = true,
                addTaskButtonVisibility = true
            )

            is TasksContract.Action.Error
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                taskRecyclerVisibility = false,
                errorMessageTextViewType = Constants.EmptyList.ERROR,
                errorMessageTextViewVisibility = true
            )
        }
    }
}