package com.example.projectstages.ui.task.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.BaseViewModel
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.ui.task.TaskFragment
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskInteractor: TaskInteractor,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<
        TaskContract.ViewState,
        TaskContract.Action,
        TaskContract.ViewEffect,
        TaskContract.ViewEvent
        >(TaskContract.ViewState()) {


    private val taskDescription = StringBuilder()
    private var taskType = 0
    private var isEdit = false
    private var projectID = 0L
    private var taskID = 0L

    init {
        isEdit = savedStateHandle.getLiveData<Boolean>(TaskFragment.IS_EDIT, false).value ?: false
        projectID = savedStateHandle.getLiveData<Long>(TaskFragment.PROJECT_ID, 0L).value ?: 0L
        taskID = savedStateHandle.getLiveData<Long>(TaskFragment.TASK_ID, 0L).value ?: 0L
        fetchTask()
    }

    private fun fetchTask() {
        if (isEdit) {
            viewModelScope.launch {
                when(val result = taskInteractor.getTaskByTaskId(taskID)) {
                    is ResultWrapper.Success -> {
                        sendAction(TaskContract.Action.EditMode(result.data.description, result.data.state))
                    }

                    is ResultWrapper.Error
                    -> sendAction(TaskContract.Action.Error)
                }
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
                deleteButtonVisibility = false
            )

            is TaskContract.Action.EditMode -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
                descriptionEditTextText = viewAction.descriptionText,
                stateSpinnerPosition = viewAction.state,
                taskType = Constants.TaskTitleType.EDIT,
                deleteButtonVisibility = true
            )

            is TaskContract.Action.AddMode -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
                taskType = Constants.TaskTitleType.ADD,
                deleteButtonVisibility = true
            )

            is TaskContract.Action.Error -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = false,
                descriptionEditTextVisibility = false,
                saveButtonVisibility = false,
                errorTextViewVisibility = true,
                deleteButtonVisibility = false
            )
        }
    }

    override fun processViewEvent(viewEvent: TaskContract.ViewEvent) {
        when(viewEvent) {
            is TaskContract.ViewEvent.OnSaveButtonClicked
            -> if (isEdit) updateTask() else insertTask()

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

    private fun insertTask() {
        viewModelScope.launch {
            val taskEntity = TaskEntity(projectID, taskDescription.toString(), taskType, System.currentTimeMillis())
            when(val resultInsert = taskInteractor.insertTask(taskEntity)) {
                is ResultWrapper.Success -> {
                    val effect = when(resultInsert.data > 0) {
                        true -> TaskContract.ViewEffect.GoToTaskList
                        false -> TaskContract.ViewEffect.FailureAdd
                    }
                    sendViewEffect(effect)
                }

                is ResultWrapper.Error
                -> sendViewEffect(TaskContract.ViewEffect.FailureAdd)
            }
        }
    }

    private fun updateTask() {
        viewModelScope.launch {
            when(val resultUpdate = taskInteractor.updateTask(taskID, taskDescription.toString(), taskType, System.currentTimeMillis())) {
                is ResultWrapper.Success -> {
                    val effect = when(resultUpdate.data > 0) {
                        true -> TaskContract.ViewEffect.GoToTaskList
                        false -> TaskContract.ViewEffect.FailureUpdate
                    }
                    sendViewEffect(effect)
                }

                is ResultWrapper.Error
                -> sendViewEffect(TaskContract.ViewEffect.FailureUpdate)
            }

        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            when(val resultDelete = taskInteractor.deleteTask(taskID)) {
                is ResultWrapper.Success -> {
                    val effect = when(resultDelete.data > 0) {
                        true -> TaskContract.ViewEffect.GoToTaskList
                        false -> TaskContract.ViewEffect.FailureDelete
                    }
                    sendViewEffect(effect)
                }

                is ResultWrapper.Error
                -> sendViewEffect(TaskContract.ViewEffect.FailureDelete)
            }
        }
    }
}