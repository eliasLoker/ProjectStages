package com.example.projectstages.ui.task.viewmodel

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
    private val taskID: Long?,
    private val taskInteractor: TaskInteractor
) : BaseViewModel<
        TaskViewModel.ViewState,
        TaskViewModel.Action,
        TaskViewModel.ViewEffect,
        TaskViewModel.ViewEvent
        >(ViewState()) {

    private var taskStringBuilder = StringBuilder()
    private var taskType = 0

    init {
        fetchTask()
    }

    private fun fetchTask() {
        if (isEdit) {
            taskID ?: throw IllegalArgumentException("TaskID is null, but fragment in edit mode")
            sendAction(Action.SetTitle(Constants.TaskTitleType.EDIT))
            viewModelScope.launch {
                val resultDescription = async { taskInteractor.getTaskDescriptionByTaskId(taskID) }
                val resultState = async { taskInteractor.getTaskStateByTaskId(taskID) }
                taskType = resultState.await()
                sendAction(Action.SuccessEdit(resultDescription.await(), taskType))
            }
        } else {
            sendAction(Action.SetTitle(Constants.TaskTitleType.ADD))
            sendAction(Action.SuccessAdd)
        }
    }

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnSaveButtonClicked
            -> onSaveButtonClicked()

            is ViewEvent.OnTextChangedDescription
            -> {
                taskStringBuilder.clear()
                taskStringBuilder.append(viewEvent.text)
            }

            is ViewEvent.OnItemSelectedStateSpinner
            -> taskType = viewEvent.position

            is ViewEvent.OnDeleteButtonClicked
            -> viewEffect.value = ViewEffect.ShowDeleteDialog

            is ViewEvent.OnAcceptDeleteClicked
            -> deleteTask()

        }
    }

    private fun onSaveButtonClicked() {
        if (isEdit) updateTask() else createTask()
    }

    private fun createTask() {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val task = TaskEntity(projectID, taskStringBuilder.toString(), taskType, timestamp)
            val insertResult = taskInteractor.insertTask(task)
            val effect = when(insertResult > 0) {
                true -> ViewEffect.GoToTaskList
                false -> ViewEffect.FailureAdd
            }
            viewEffect.value = effect
        }
    }

    private fun updateTask() {
        taskID ?: throw IllegalArgumentException("TaskID is null")
        viewModelScope.launch {
                val timestamp = System.currentTimeMillis()
                val updateResult = taskInteractor.updateTask(taskID, taskStringBuilder.toString(), taskType, timestamp)
                val effect = when(updateResult > 0) {
                    true -> ViewEffect.GoToTaskList
                    false -> ViewEffect.FailureUpdate
                }
                viewEffect.value = effect
        }
    }

    private fun deleteTask() {
        taskID ?: throw IllegalArgumentException("TaskID is null, but try delete")
        viewModelScope.launch {
            val deleteResult = taskInteractor.deleteTask(taskID)
            val effect = when(deleteResult > 0) {
                true -> ViewEffect.GoToTaskList
                false -> ViewEffect.FailureDelete
            }
            viewEffect.value = effect
        }
    }

    override fun onReduceState(viewAction: Action): ViewState {
        return when(viewAction) {
            is Action.SetTitle -> state.copy(
                taskTitleType = viewAction.titleType
            )
            is Action.Loading -> state.copy(
                progressBarVisibility = true,
                stateSpinnerVisibility = false,
                descriptionEditTextVisibility = false,
                saveButtonVisibility = false,
            )

            is Action.SuccessEdit -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
                descriptionEditTextText = viewAction.descriptionText,
                stateSpinnerPosition = viewAction.state
            )

            is Action.SuccessAdd -> state.copy(
                progressBarVisibility = false,
                stateSpinnerVisibility = true,
                descriptionEditTextVisibility = true,
                saveButtonVisibility = true,
            )
        }
    }

    data class ViewState(
        val taskTitleType: Constants.TaskTitleType = Constants.TaskTitleType.ADD,
        val progressBarVisibility: Boolean = true,
        val stateSpinnerVisibility: Boolean = false,
        val descriptionEditTextVisibility: Boolean = false,
        val saveButtonVisibility: Boolean = false,
        val descriptionEditTextText: String = "",
        val stateSpinnerPosition: Int = 0
    ) : BaseViewState

    sealed class Action : BaseAction {

        class SetTitle(
            val titleType: Constants.TaskTitleType
        ) : Action()

        object Loading : Action()

        class SuccessEdit(
            val descriptionText: String,
            val state: Int
        ) : Action()

        object SuccessAdd : Action()
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