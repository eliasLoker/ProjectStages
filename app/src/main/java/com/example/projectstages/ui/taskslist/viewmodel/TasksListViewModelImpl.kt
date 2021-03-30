package com.example.projectstages.ui.taskslist.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.BaseAction
import com.example.projectstages.base.BaseViewModel
import com.example.projectstages.base.BaseViewState
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.ui.taskslist.event.TasksListNavigationEvents
import com.example.projectstages.ui.taskslist.interactor.TasksListInteractor
import com.example.projectstages.ui.taskslist.model.Task
import com.example.projectstages.utils.ResultWrapper
import com.example.projectstages.utils.SingleLiveEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TasksListViewModelImpl(
    private val projectId: Long,
    private val listInteractor: TasksListInteractor
) : BaseViewModel<TasksListViewModelImpl.ViewState, TasksListViewModelImpl.Action>(ViewState()),
    TasksListViewModel {

    private val _tasks = ArrayList<Task>()

    private var _selectedTaskId = 0L

    private val userFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")

    override val tasks_list_navigationEvents = SingleLiveEvent<TasksListNavigationEvents>()

    override fun onViewCreated(isFirstLoading: Boolean) {
        fetchTasks()
    }

    private fun fetchTasks() {
        viewModelScope.launch {
            when (val tasks = listInteractor.getTasks(projectId)) {
                is ResultWrapper.Success
                -> {
                    Log.d("ProjectsDebug", "TASKS: $tasks")
                    tasks.data.collectLatest {
                        Log.d("ProjectsDebug", "TASKS COLLECT: $it")
                        when (it.isNotEmpty()) {
                            true -> {
                                _tasks.clear()
                                it.forEachIndexed { index, taskEntity ->

                                    val task = Task(
                                        taskEntity.id,
                                        taskEntity.description,
                                        userFormat.format(Date(taskEntity.createdTimestamp)),
                                        listInteractor.getItemType(index),
                                        taskEntity.state
                                    )
                                    _tasks.add(task)
                                }
                                _tasks.sortBy { i -> i.state }
                                Log.d("ProjectsDebug", "SORTED: $_tasks")
                                sendAction(Action.NotEmptyList(_tasks))
                            }
                            false -> {
                                sendAction(Action.EmptyList)
                            }
                        }
                    }
                }

                is ResultWrapper.Error
                -> {
                    Log.d("ProjectsDebug", "TASKS: ${tasks.exception.localizedMessage}")
                }
            }
        }
    }

    override fun onAddTaskButtonClicked(description: String, taskType: Int) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val task = TaskEntity(projectId, description, taskType, timestamp)
            val insertResult = listInteractor.insertTask(task)
            val event = when (insertResult < 0) {
                true -> TasksListNavigationEvents.FailureAddDialog
                false -> TasksListNavigationEvents.SuccessAddDialog
            }
            tasks_list_navigationEvents.value = event
            //TODO("Name variable")
        }
    }

//    override fun onDeleteButtonClicked(id: Long) {
//        _selectedTaskId = id
//        tasks_list_navigationEvents.value = TasksListNavigationEvents.ShowDeleteTaskDialog
//    }

    override fun onAcceptDeleteTask() {
        viewModelScope.launch {
            val deleteResult = listInteractor.deleteTask(_selectedTaskId)
            val event = when (deleteResult > 0) {
                true -> TasksListNavigationEvents.SuccessDelete
                false -> TasksListNavigationEvents.FailureDelete
            }
            tasks_list_navigationEvents.value = event
        }
    }

    /*
    override fun onEditButtonClicked(id: Long) {
        _selectedTaskId = id
        _tasks.forEach {
            if (it.id == id) {
                _List_navigationEvents.value = TasksListNavigationEvents.ShowEditTaskDialog(
                    it.description,
                    it.state
                )
            }
        }
    }
    */

    /*
    override fun onUpdateButtonClicked(description: String, state: Int) {
        viewModelScope.launch {
            val updateResult = listInteractor.updateTask(_selectedTaskId, description, state)
            Log.d("ProjectsDebug", "UPDATE RES: $updateResult")
            val event = when(updateResult > 0) {
                true -> TasksListNavigationEvents.SuccessUpdate
                false -> TasksListNavigationEvents.FailureUpdate
            }
            tasks_list_navigationEvents.value = event
        }
    }
    */

    override fun onTaskClicked(id: Long) {
        tasks_list_navigationEvents.value = TasksListNavigationEvents.GoToTask(id)
    }


    override fun onGoToAddTaskClicked() {
        tasks_list_navigationEvents.value = TasksListNavigationEvents.GoToAddTask(projectId)
    }

    override fun onReduceState(viewAction: Action): ViewState {
        return when (viewAction) {
            is Action.Loading
            -> state.copy(progressBarVisibility = true)

            is Action.EmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                taskRecyclerVisibility = false
            )

            is Action.NotEmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = false,
                taskRecyclerVisibility = true,
                tasks = viewAction.tasks
            )
        }
    }

    data class ViewState(
        val progressBarVisibility: Boolean = true,
        val emptyListTextViewVisibility: Boolean = false,
        val taskRecyclerVisibility: Boolean = false,
        val tasks: List<Task> = emptyList()
    ) : BaseViewState

    sealed class Action : BaseAction {

        object Loading : Action()

        object EmptyList : Action()

        class NotEmptyList(
            val tasks: List<Task>
        ) : Action()
    }
}