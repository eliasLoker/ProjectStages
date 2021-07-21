package com.example.projectstages.ui.task.viewmodel

import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.utils.Constants

class TaskContract : BaseContract {

    data class ViewState(
        val taskType: Constants.TaskTitleType = Constants.TaskTitleType.ADD,
        val progressBarVisibility: Boolean = true,
        val stateSpinnerVisibility: Boolean = false,
        val descriptionEditTextVisibility: Boolean = false,
        val saveButtonVisibility: Boolean = false,
        val descriptionEditTextText: String = "",
        val stateSpinnerPosition: Int = 0,
        val errorTextViewVisibility: Boolean = false,
        val deleteButtonVisibility: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {

        object Loading : Action()

        class EditMode(
            val descriptionText: String,
            val state: Int
        ) : Action()

        object AddMode : Action()

        object Error: Action()
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