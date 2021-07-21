package com.example.projectstages.ui.projects.viewmodel

import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.utils.Constants

class ProjectsContract : BaseContract{

    data class ViewState(
        val progressBarVisibility: Boolean = true,
        val headerViewsVisibility: Boolean = false,
        val addProjectButtonVisibility: Boolean = false,
        val emptyListTextViewVisibility: Boolean = false,
        val projectsAdapterVisibility: Boolean = false,
        val projects: List<Project> = emptyList(),
        val errorTextViewVisibility: Boolean = false,
        val allTasks: Int = 0,
        val completedTasks: Int = 0,
        val failureType: Constants.FailureType = Constants.FailureType.EMPTY_LIST
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
            val projects: List<Project>,
            val allTasks: Int,
            val completedTasks: Int
        ) : Action()

        object Error : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        object SuccessAddDialog : ViewEffect()

        object FailureAddDialog : ViewEffect()

        class GoToTaskList(
            val projectID: Long,
            val projectName: String
        ) : ViewEffect()

        object ShowAddProjectDialog : ViewEffect()

        class ShowDeleteProjectDialog(
            val name: String
        ) : ViewEffect()

        object SuccessDelete : ViewEffect()

        object FailureDelete : ViewEffect()

        class ShowEditProjectDialog(
            val name: String,
            var type: Int
        ) : ViewEffect()

        object SuccessEdit : ViewEffect()

        object FailureEdit : ViewEffect()
    }

    sealed class ViewEvent : BaseViewEvent {

        object OnAddProjectClicked : ViewEvent()

        class OnAcceptAddProjectClicked(
            val name: String,
            val type: Int
        ) : ViewEvent()

        class OnItemClicked(
            val position: Int
        ) : ViewEvent()

        class OnPopupDeleteClicked(
            val position: Int
        ) : ViewEvent()

        class OnPopupEditClicked(
            val position: Int
        ) : ViewEvent()

        object OnAcceptDeleteProject : ViewEvent()

        class OnAcceptEditProject(
            val name: String,
            val type: Int
        ) : ViewEvent()
    }
}