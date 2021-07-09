package com.example.projectstages.ui.taskslist.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.*
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.ui.taskslist.interactor.TasksListInteractor
import com.example.projectstages.ui.taskslist.model.Task
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class TasksListViewModel(
    private val projectId: Long,
    private val tasksListInteractor: TasksListInteractor
) : BaseViewModel<
        TasksListViewModel.ViewState,
        TasksListViewModel.Action,
        TasksListViewModel.ViewEffect,
        TasksListViewModel.ViewEvent
        >(ViewState()) {

    private val tasks = ArrayList<Task>()

    init {
        Log.d("TasksListVM", "prid $projectId")
        viewModelScope.launch {
            when(val tasks = tasksListInteractor.getTasks(projectId)) {
                is ResultWrapper.Success -> {
                    tasks.data.collectLatest {
                        when(it.isNotEmpty()) {
                            true -> {
                                this@TasksListViewModel.tasks.clear()
                                it.forEachIndexed { index, taskEntity ->
                                    val task = Task(
                                        taskEntity.id,
                                        taskEntity.description,
                                        Constants.userFormatTasks.format(Date(taskEntity.createdTimestamp)),
                                        tasksListInteractor.getItemType(index),
                                        taskEntity.state
                                    )
                                    this@TasksListViewModel.tasks.add(task)
                                }
                                this@TasksListViewModel.tasks.sortBy { i -> i.state }
                                sendAction(Action.NotEmptyList(this@TasksListViewModel.tasks))
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

    private fun fetchTasks() {
        viewModelScope.launch {
            //TODO("Flow не вываливается в Error, если указать несуществующий ProjectID")
            when (val tasks = tasksListInteractor.getTasks(projectId)) {
                is ResultWrapper.Success
                -> {
                    tasks.data.collectLatest {
                        when (it.isNotEmpty()) {
                            true -> {
                                this@TasksListViewModel.tasks.clear()
                                it.forEachIndexed { index, taskEntity ->
                                    val task = Task(
                                        taskEntity.id,
                                        taskEntity.description,
                                        Constants.userFormatTasks.format(Date(taskEntity.createdTimestamp)),
                                        tasksListInteractor.getItemType(index),
                                        taskEntity.state
                                    )
                                    this@TasksListViewModel.tasks.add(task)
                                }
                                this@TasksListViewModel.tasks.sortBy { i -> i.state }
                                sendAction(Action.NotEmptyList(this@TasksListViewModel.tasks))
                            }
                            false -> {
                                sendAction(Action.EmptyList)
                            }
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
            is ViewEvent.OnTaskClicked-> viewEffectChannel.sendViewEffect(ViewEffect.GoToTask(viewEvent.taskId))

            is ViewEvent.OnAddTaskClicked -> viewEffectChannel.sendViewEffect(ViewEffect.GoToAddTask(projectId))
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
                errorMessageTextViewVisibility = true
            )

            is Action.NotEmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = false,
                taskRecyclerVisibility = true,
                tasks = viewAction.tasks
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
        val emptyListTextViewVisibility: Boolean = false,
        val taskRecyclerVisibility: Boolean = false,
        val tasks: List<Task> = emptyList(),
        val errorMessageTextViewType: Constants.EmptyList = Constants.EmptyList.ERROR,
        val errorMessageTextViewVisibility: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {

        object Loading : Action()

        object EmptyList : Action()

        class NotEmptyList(
            val tasks: List<Task>
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