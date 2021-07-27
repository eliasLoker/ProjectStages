package com.example.projectstages.ui.tasks.viewmodel

import androidx.lifecycle.*
import com.example.projectstages.base.*
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.ui.tasks.TasksFragment
import com.example.projectstages.ui.tasks.interactor.TasksInteractor
import com.example.projectstages.ui.tasks.model.Task
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksInteractor: TasksInteractor,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<
        TasksContract.ViewState,
        TasksContract.Action,
        TasksContract.ViewEffect,
        TasksContract.ViewEvent
        >(TasksContract.ViewState()) {

    private var _tasks = ArrayList<Task>()
    private var projectId: Long = 0L
    private var projectName: String = ""

    init {
        projectId = savedStateHandle.getLiveData(TasksFragment.TAG_FOR_PROJECT_ID, 0L).value ?: 0L
        projectName = savedStateHandle.getLiveData(TasksFragment.TAG_FOR_PROJECT_NAME, "").value ?: ""
        fetchTasks(projectId, projectName)
    }

    private fun fetchTasks(projectId: Long, projectName: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendAction(TasksContract.Action.Error)
        }
        viewModelScope.launch(exceptionHandler) {
            val tasks = tasksInteractor.getTasks(projectId)
            tasks.collectLatest { tasksList ->
                when(tasksList.isNotEmpty()) {
                    true -> {
                        this@TasksViewModel._tasks.clear()
                        tasksList.forEach { taskEntity ->
                            val task = Task(
                                taskEntity.id,
                                taskEntity.description,
                                Constants.userFormatTasks.format(Date(taskEntity.updatedTimestamp)),
                                taskEntity.state
                            )
                            _tasks.add(task)
                        }
                        _tasks = _tasks.sortedWith(compareBy<Task> {it.state}.thenBy { it.date }).toMutableList() as ArrayList<Task>
                        sendAction(TasksContract.Action.NotEmptyList(_tasks, projectName))
                    }
                    false -> sendAction(TasksContract.Action.EmptyList)
                }
            }
        }
    }
    /*
    private fun fetchTasks() {
        projectId = savedStateHandle.getLiveData(TasksFragment.TAG_FOR_PROJECT_ID, 0L).value ?: 0L
        projectName = savedStateHandle.getLiveData(TasksFragment.TAG_FOR_PROJECT_NAME, "").value ?: ""
        viewModelScope.launch {
            when(val tasks = tasksInteractor.getTasks(projectId)) {
                is ResultWrapper.Success -> {
                    tasks.data.collectLatest { tasksList ->
                        when(tasksList.isNotEmpty()) {
                            true -> {
                                this@TasksViewModel._tasks.clear()
                                tasksList.forEach { taskEntity ->
                                    val task = Task(
                                        taskEntity.id,
                                        taskEntity.description,
                                        Constants.userFormatTasks.format(Date(taskEntity.updatedTimestamp)),
                                        taskEntity.state
                                    )
                                    _tasks.add(task)
                                }
                                _tasks = _tasks.sortedWith(compareBy<Task> {it.state}.thenBy { it.date }).toMutableList() as ArrayList<Task>
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
    */

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
                failureType = Constants.FailureType.EMPTY_LIST,
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
                addTaskButtonVisibility = true,
                errorMessageTextViewVisibility = false
            )

            is TasksContract.Action.Error
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                taskRecyclerVisibility = false,
                failureType = Constants.FailureType.ERROR,
                errorMessageTextViewVisibility = true
            )
        }
    }
}