package com.example.projectstages.ui.task.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.BaseAction
import com.example.projectstages.base.BaseViewModel
import com.example.projectstages.base.BaseViewState
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TaskViewModelImpl(
    private val isEdit: Boolean,
    private val taskID: Long?,
    private val taskInteractor: TaskInteractor
) : BaseViewModel<TaskViewModelImpl.ViewState, TaskViewModelImpl.Action>(ViewState()),
    TaskViewModel{

    override fun onActivityCreated(isFirstLoading: Boolean) {
        Log.d("TaskDebug", "VM state: $state")
//        onReduceState(Action.Loading)
        fetchTask()
        Log.d("TaskDebug", "VM state2: $state")
    }

    private fun fetchTask() {
        Log.d("TaskDebug", "fetchTask $taskID")
        if (!isEdit) {
            sendAction(Action.SetTitle(Constants.TaskTitleType.ADD))
            sendAction(Action.SuccessAdd)
        } else {
            taskID?.let { taskIdNotNull ->
                sendAction(Action.SetTitle(Constants.TaskTitleType.EDIT))
                viewModelScope.launch {
                    Log.d("TaskDebug", "START")
                    val resultDescription = async { taskInteractor.getTaskDescriptionByTaskId(taskIdNotNull) }
                    val resultState = async { taskInteractor.getTaskStateByTaskId(taskIdNotNull) }
                    sendAction(Action.SuccessEdit(resultDescription.await(), resultState.await()))
                    Log.d("TaskDebug", "RESULT ASYNC: ${resultDescription.await()}, ${resultState.await()}")
                }
            }
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
}