package com.example.projectstages.ui.task.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.data.entity.TaskEntity
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TaskViewModel(
    private val isEdit: Boolean,
    private val projectID: Long,
    private val taskID: Long,
    private val taskInteractor: TaskInteractor
) : BaseViewModel<
        TaskViewModel.ViewState,
        TaskViewModel.Action,
        TaskViewModel.ViewEffect,
        TaskViewModel.ViewEvent
        >(ViewState()) {


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
                sendAction(Action.EditMode(resDescr, taskType))
            }
        } else {
            sendAction(Action.AddMode)
        }
    }

    override fun onReduceState(viewAction: Action): ViewState {
        return when(viewAction) {

            is Action.Loading -> state.copy(
                progressBarVisibility = true,
                stateSpinnerVisibility = false,
                descriptionEditTextVisibility = false,
                saveButtonVisibility = false,
            )

            is Action.EditMode -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
                descriptionEditTextText = viewAction.descriptionText,
                stateSpinnerPosition = viewAction.state,
                taskType = Constants.TaskTitleType.EDIT
            )

            is Action.AddMode -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
                taskType = Constants.TaskTitleType.ADD
            )
        }
    }

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnSaveButtonClicked
            -> if (isEdit) updateTask() else createTask()

            is ViewEvent.OnTextChangedDescription
            -> {
                taskDescription.clear()
                taskDescription.append(viewEvent.text)
            }

            is ViewEvent.OnItemSelectedStateSpinner
            -> { taskType = viewEvent.position }

            is ViewEvent.OnDeleteButtonClicked
            -> { }

            is ViewEvent.OnAcceptDeleteClicked
            -> { }
        }
    }

    private fun createTask() {
        viewModelScope.launch {
            val taskEntity = TaskEntity(projectID, taskDescription.toString(), taskType, System.currentTimeMillis())
            val effect = when(taskInteractor.insertTask(taskEntity) > 0) {
                true -> ViewEffect.GoToTaskList
                false -> ViewEffect.FailureAdd
            }
            sendViewEffect(effect)
        }
    }

    private fun updateTask() {
        viewModelScope.launch {
            val effect = when(taskInteractor.updateTask(taskID, taskDescription.toString(), taskType, System.currentTimeMillis()) > 0) {
                true -> ViewEffect.GoToTaskList
                false -> ViewEffect.FailureUpdate
            }
            sendViewEffect(effect)
        }
    }

    data class ViewState(
        val taskType: Constants.TaskTitleType = Constants.TaskTitleType.ADD,
        val progressBarVisibility: Boolean = true,
        val stateSpinnerVisibility: Boolean = false,
        val descriptionEditTextVisibility: Boolean = false,
        val saveButtonVisibility: Boolean = false,
        val descriptionEditTextText: String = "",
        val stateSpinnerPosition: Int = 0
    ) : BaseViewState

    sealed class Action : BaseAction {

        object Loading : Action()

        class EditMode(
            val descriptionText: String,
            val state: Int
        ) : Action()

        object AddMode : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        object GoToTaskList : ViewEffect()

        object FailureAdd: ViewEffect()

        object FailureUpdate: ViewEffect()

        object FailureDelete: ViewEffect()

        object ShowDeleteDialog: ViewEffect()
    }

    sealed class ViewEvent : BaseViewEvent {

        object OnSaveButtonClicked : ViewEvent()

        class OnTextChangedDescription(
            val text: String
        ) : ViewEvent()

        class OnItemSelectedStateSpinner(
            val position: Int
        ) : ViewEvent()

        object OnDeleteButtonClicked : ViewEvent()

        object OnAcceptDeleteClicked : ViewEvent()
    }
}