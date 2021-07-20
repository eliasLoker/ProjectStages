package com.example.projectstages.ui.tasks.viewmodel

import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.ui.tasks.model.Task
import com.example.projectstages.utils.Constants

class TasksContract : BaseContract{

    data class ViewState(
        val progressBarVisibility: Boolean = true,
        val headerViewsVisibility: Boolean = false,
        val emptyListTextViewVisibility: Boolean = false,
        val taskRecyclerVisibility: Boolean = false,
        val tasks: List<Task> = emptyList(),
        val errorMessageTextViewType: Constants.EmptyList = Constants.EmptyList.ERROR,
        val errorMessageTextViewVisibility: Boolean = false,
        val projectName: String = "",
        val addTaskButtonVisibility: Boolean = false,
    ) : BaseViewState {
        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    sealed class Action : BaseAction {

        object Loading : Action()

        object EmptyList : Action()

        class NotEmptyList(
            val tasks: List<Task>,
            val projectName: String
        ) : Action()

        object Error : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        class GoToAddTask(
            val projectId: Long
        ) : ViewEffect()

        class GoToTask(
            val taskID: Long
        ) : ViewEffect()
    }

    sealed class ViewEvent : BaseViewEvent {

        object OnAddTaskClicked : ViewEvent()

        class OnTaskClicked(
            val taskId: Long
        ) : ViewEvent()
    }
}