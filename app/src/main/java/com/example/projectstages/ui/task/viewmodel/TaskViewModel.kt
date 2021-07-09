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
                Log.d("TaskDebug", "Req tID: $taskID")
                val resultDescription = async { taskInteractor.getTaskDescriptionByTaskId(taskID) }
                val resultState = async { taskInteractor.getTaskStateByTaskId(taskID) }
                taskType = resultState.await()
                val resDescr = resultDescription.await()
                sendAction(Action.SuccessEdit(resDescr, taskType))
                Log.d("TaskDebug", "Descr: $resDescr, TT: $taskType")
            }
        } else {
            sendAction(Action.SetTitle(Constants.TaskTitleType.ADD))
            sendAction(Action.SuccessAdd)
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

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnSaveButtonClicked
            -> { }

            is ViewEvent.OnTextChangedDescription
            -> { }

            is ViewEvent.OnItemSelectedStateSpinner
            -> { }

            is ViewEvent.OnDeleteButtonClicked
            -> { }

            is ViewEvent.OnAcceptDeleteClicked
            -> { }
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