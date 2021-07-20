package com.example.projectstages.ui.task.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.BaseViewModel
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.interactor.TaskInteractorImpl
import com.example.projectstages.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TaskViewModel(
    private val isEdit: Boolean,
    private val projectID: Long,
    private val taskID: Long,
    private val taskInteractor: TaskInteractor
) : BaseViewModel<
        TaskContract.ViewState,
        TaskContract.Action,
        TaskContract.ViewEffect,
        TaskContract.ViewEvent
        >(TaskContract.ViewState()) {


    private val taskDescription = StringBuilder()
    private var taskType = 0

    init {
        fetchTask()
    }

    private fun fetchTask() {
        if (isEdit) {
            viewModelScope.launch {
                val resultDescription = async { taskInteractor.getTaskDescriptionByTaskId(taskID) }
                val resultState = async { taskInteractor.getTaskStateByTaskId(taskID) }
                taskType = resultState.await()
                val resDescr = resultDescription.await()
                sendAction(TaskContract.Action.EditMode(resDescr, taskType))
            }
        } else {
            sendAction(TaskContract.Action.AddMode)
        }
    }

    override fun onReduceState(viewAction: TaskContract.Action): TaskContract.ViewState {
        return when(viewAction) {

            is TaskContract.Action.Loading -> state.copy(
                progressBarVisibility = true,
                stateSpinnerVisibility = false,
                descriptionEditTextVisibility = false,
                saveButtonVisibility = false,
            )

            is TaskContract.Action.EditMode -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
                descriptionEditTextText = viewAction.descriptionText,
                stateSpinnerPosition = viewAction.state,
                taskType = Constants.TaskTitleType.EDIT
            )

            is TaskContract.Action.AddMode -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
                taskType = Constants.TaskTitleType.ADD
            )
        }
    }

    override fun processViewEvent(viewEvent: TaskContract.ViewEvent) {
        when(viewEvent) {
            is TaskContract.ViewEvent.OnSaveButtonClicked
            -> if (isEdit) updateTask() else createTask()

            is TaskContract.ViewEvent.OnTextChangedDescription
            -> {
                taskDescription.clear()
                taskDescription.append(viewEvent.text)
            }

            is TaskContract.ViewEvent.OnItemSelectedStateSpinner
            -> { taskType = viewEvent.position }

            is TaskContract.ViewEvent.OnDeleteButtonClicked
            -> sendViewEffect(TaskContract.ViewEffect.ShowDeleteDialog)

            is TaskContract.ViewEvent.OnAcceptDeleteClicked
            -> deleteTask()
        }
    }

    private fun createTask() {
        viewModelScope.launch {
            val taskEntity = TaskEntity(projectID, taskDescription.toString(), taskType, System.currentTimeMillis())
            val effect = when(taskInteractor.insertTask(taskEntity) > 0) {
                true -> TaskContract.ViewEffect.GoToTaskList
                false -> TaskContract.ViewEffect.FailureAdd
            }
            sendViewEffect(effect)
        }
    }

    private fun updateTask() {
        viewModelScope.launch {
            val effect = when(taskInteractor.updateTask(taskID, taskDescription.toString(), taskType, System.currentTimeMillis()) > 0) {
                true -> TaskContract.ViewEffect.GoToTaskList
                false -> TaskContract.ViewEffect.FailureUpdate
            }
            sendViewEffect(effect)
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            val effect = when(taskInteractor.deleteTask(taskID) > 0) {
                true -> TaskContract.ViewEffect.GoToTaskList
                false -> TaskContract.ViewEffect.FailureDelete
            }
            sendViewEffect(effect)
        }
    }
}