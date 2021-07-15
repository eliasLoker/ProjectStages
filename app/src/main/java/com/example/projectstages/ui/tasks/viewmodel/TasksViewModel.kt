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
        TasksViewModel.ViewState,
        TasksViewModel.Action,
        TasksViewModel.ViewEffect,
        TasksViewModel.ViewEvent
        >(ViewState()) {

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
                                sendAction(Action.NotEmptyList(_tasks, projectName))
                            }
                            false -> sendAction(Action.EmptyList)
                        }
                    }
                }
                is ResultWrapper.Error
                -> sendAction(Action.Error)
            }
        }
    }

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnTaskClicked
            -> sendViewEffect(ViewEffect.GoToTask(viewEvent.taskId))

            is ViewEvent.OnAddTaskClicked
            -> sendViewEffect(ViewEffect.GoToAddTask(projectId))
        }
    }

    override fun onReduceState(viewAction: Action): ViewState {
        return when (viewAction) {
            is Action.Loading
            -> state.copy(
                progressBarVisibility = true
            )

            is Action.EmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                taskRecyclerVisibility = false,
                errorMessageTextViewType = Constants.EmptyList.EMPTY,
                errorMessageTextViewVisibility = true,
                headerViewsVisibility = true,
                addTaskButtonVisibility = true
            )

            is Action.NotEmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = false,
                taskRecyclerVisibility = true,
                tasks = viewAction.tasks,
                projectName = projectName,
                headerViewsVisibility = true,
                addTaskButtonVisibility = true
            )

            is Action.Error
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                taskRecyclerVisibility = false,
                errorMessageTextViewType = Constants.EmptyList.ERROR,
                errorMessageTextViewVisibility = true
            )
        }
    }

    data class ViewState(
        val progressBarVisibility: Boolean = true,
        val headerViewsVisibility: Boolean = false,
        val emptyListTextViewVisibility: Boolean = false,
        val taskRecyclerVisibility: Boolean = false,
        val tasks: List<Task> = emptyList(),
        val errorMessageTextViewType: Constants.EmptyList = Constants.EmptyList.ERROR,
        val errorMessageTextViewVisibility: Boolean = false,
        val projectName: String = "",
        val addTaskButtonVisibility: Boolean = false,
    ) : BaseViewState

    sealed class Action : BaseAction {

        object Loading : Action()

        object EmptyList : Action()

        class NotEmptyList(
            val tasks: List<Task>,
            val projectName: String
        ) : Action()

        object Error : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        class GoToAddTask(
            val projectId: Long
        ) : ViewEffect()

        class GoToTask(
            val taskID: Long
        ) : ViewEffect()
    }

    sealed class ViewEvent : BaseViewEvent {

        object OnAddTaskClicked : ViewEvent()

        class OnTaskClicked(
            val taskId: Long
        ) : ViewEvent()
    }
}