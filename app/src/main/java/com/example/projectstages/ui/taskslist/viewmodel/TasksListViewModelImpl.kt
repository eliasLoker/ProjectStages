package com.example.projectstages.ui.taskslist.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.*
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.ui.taskslist.adapter.TasksListAdapterListener
import com.example.projectstages.ui.taskslist.interactor.TasksListInteractor
import com.example.projectstages.ui.taskslist.model.Task
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TasksListViewModelImpl(
    private val projectId: Long,
    private val listInteractor: TasksListInteractor
) : BaseViewModel<
        TasksListViewModelImpl.ViewState,
        TasksListViewModelImpl.Action,
        TasksListViewModelImpl.ViewEffect,
        TasksListViewModelImpl.ViewEvent
        >(ViewState()), TasksListAdapterListener {

    private val tasks = ArrayList<Task>()

    private val userFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

//    override val tasksListNavigationEvents = SingleLiveEvent<TasksListNavigationEvents>()
//TODO("заменить на viewEffect")

    override fun onViewCreated(isFirstLoading: Boolean) {
        fetchTasks()
    }

    private fun fetchTasks() {
        viewModelScope.launch {
            //TODO("Flow не вываливается в Error, если указать несуществующий ProjectID")
            when (val tasks = listInteractor.getTasks(projectId)) {
                is ResultWrapper.Success
                -> {
                    tasks.data.collectLatest {
                        when (it.isNotEmpty()) {
                            true -> {
                                this@TasksListViewModelImpl.tasks.clear()
                                it.forEachIndexed { index, taskEntity ->
                                    val task = Task(
                                        taskEntity.id,
                                        taskEntity.description,
                                        userFormat.format(Date(taskEntity.createdTimestamp)),
                                        listInteractor.getItemType(index),
                                        taskEntity.state
                                    )
                                    this@TasksListViewModelImpl.tasks.add(task)
                                }
                                this@TasksListViewModelImpl.tasks.sortBy { i -> i.state }
                                sendAction(Action.NotEmptyList(this@TasksListViewModelImpl.tasks))
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
        TODO("Not yet implemented")
    }

    /*
    //TODO("Коллбэки в viewEvent")
    override fun onAddTaskButtonClicked(description: String, taskType: Int) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val task = TaskEntity(projectId, description, taskType, timestamp)
            val insertResult = listInteractor.insertTask(task)
            val event = when (insertResult < 0) {
                true -> TasksListNavigationEvents.FailureAddDialog
                false -> TasksListNavigationEvents.SuccessAddDialog
            }
            tasksListNavigationEvents.value = event
        }
    }

    override fun onTaskClicked(id: Long) {
        tasksListNavigationEvents.value = TasksListNavigationEvents.GoToTask(id)
    }


    override fun onGoToAddTaskClicked() {
        tasksListNavigationEvents.value = TasksListNavigationEvents.GoToAddTask(projectId)
    }
    */

    override fun onTaskClicked(id: Long) {
        TODO("Not yet implemented")
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
        //TODO("Not yet implemented")
    }

    sealed class ViewEvent : BaseViewEvent {
        //TODO("Not yet implemented")
    }
}