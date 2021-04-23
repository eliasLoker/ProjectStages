package com.example.projectstages.ui.projects.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ProjectsViewModelImpl(
    private val title: String,
    private val subtitle: String,
    private val interactor: ProjectsInteractor
) :
    BaseViewModel<
            ProjectsViewModelImpl.ViewState,
            ProjectsViewModelImpl.Action,
            ProjectsViewModelImpl.ViewEffect,
            ProjectsViewModelImpl.ViewEvent
            >
        (ViewState()) {
    init {
        Log.d("ProjectsDebug2", "init")
        sendAction(Action.SetToolbar(title, subtitle))
        fetchProjects()
    }
    private val _projects = ArrayList<Project>()

    private fun fetchProjects() {
        viewModelScope.launch {
            when (val projects = interactor.getProjects()) {
                is ResultWrapper.Success -> {
                    projects.data.collectLatest {
//                    projects.data.collectLatest {
                        Log.d("ProjectsDebug2", "fetchProjects ${it.size}")
                        when(it.isNotEmpty()) {
                            true -> {
                                _projects.clear()
                                it.forEach{ projectEntity ->
                                    val projectId = projectEntity.id
//                                        val count = interactor.countTasksByProjectId(projectId)
                                    val count = 2
                                        val countTasksByState = arrayOf(
                                            1,
                                            2,
                                            3
                                        )
                                        val timestamp = 232342345433345L
                                        val formattedDate =
                                            Constants.userFormat.format(Date(timestamp))
                                        val project = Project(
                                            projectEntity.id,
                                            projectEntity.name + " count: ${count} ",
                                            projectEntity.type,
                                            formattedDate,
                                            countTasksByState
                                        )
                                        _projects.add(project)
                                }
                                Log.d("ProjectsDebug2", "fetchProjects 2 ${_projects.size}")
                                sendAction(Action.NotEmptyList(_projects))
                            }
                            false -> {
                                sendAction(Action.EmptyList)
                                Log.d("ProjectsDebug2", "fetchProjects isNotEmpty FALSE")
                            }
                        }
                    }
                }

                is ResultWrapper.Error -> {
                    Log.d("ProjectsDebug", "ERROR: ${projects.exception.localizedMessage}")
                    sendAction(Action.Error)
                }
            }
        }
    }

    /*
    private fun fetchProjects() {
        viewModelScope.launch {
            when (val projects = interactor.getProjects()) {
                is ResultWrapper.Success -> {
                    val sdf = projects.data
//                    projects.data.collectLatest {
                    projects.data.collectLatest {
                        Log.d("ProjectsDebug", "fetchProjects 1 ${it.size}")
                        when(it.isNotEmpty()) {
                            true -> {
                                _projects.clear()
                                Log.d("ProjectsDebug", "fetchProjects 2 SIZE ${_projects.size}")
                                Log.d("ProjectsDebug", "fetchProjects 3 collectLatest ${it.size}")
                                it.forEach{ projectEntity ->
                                    Log.d("ProjectsDebug", "fetchProjects 4 forEach ${projectEntity.name}")
                                    val projectId = projectEntity.id
                                            val job2 = launch {
                                                val count = interactor.countTasksByProjectId(projectId)
                                                Log.d("ProjectsDebug", "fetchProjects COUNT: $count ${projectEntity.name}")

                                                val timestamp = when(count > 0) {
                                                    true -> interactor.getLastTaskTimestampsByProjectId(projectId)
                                                    false -> projectEntity.createdTimestamp
                                                }
                                                Log.d("ProjectsDebug", "TS: $timestamp")
                                            }

                                            val countTasksByState = arrayOf(
                                                1,
                                                2,
                                                3
                                            )
                                            val timestamp = 232342345433345L
                                            val formattedDate =
                                                Constants.userFormat.format(Date(timestamp))
                                            val project = Project(
                                                projectEntity.id,
                                                projectEntity.name,
                                                projectEntity.type,
                                                formattedDate,
                                                countTasksByState
                                            )
                                            _projects.add(project)
                                }
                                Log.d("ProjectsDebug", "fetchProjects 5 ${_projects.size}")
//                                Log.d("ProjectsDebug", "fetchProjects ACTION 1 ${it.size}")
                                sendAction(Action.NotEmptyList(_projects))
                            }
                            false -> sendAction(Action.EmptyList)
                        }
                    }
                }

                is ResultWrapper.Error -> {
                    Log.d("ProjectsDebug", "ERROR: ${projects.exception.localizedMessage}")
                    sendAction(Action.Error)
                }
            }
        }
    }
    */

    /*
    private fun fetchProjects() {
        viewModelScope.launch {
            when (val projects = interactor.getProjects()) {
                is ResultWrapper.Success -> {
                    val sdf = projects.data
                    projects.data.collectLatest {
                        Log.d("ProjectsDebug", "fetchProjects 1 ${it.size}")
                        when(it.isNotEmpty()) {
                            true -> {
                                _projects.clear()
                                Log.d("ProjectsDebug", "fetchProjects 2 SIZE ${_projects.size}")
                                Log.d("ProjectsDebug", "fetchProjects 3 collectLatest ${it.size}")
                                it.forEach{ projectEntity ->
                                    Log.d("ProjectsDebug", "fetchProjects 4 forEach ${projectEntity.name}")
                                    val projectId = projectEntity.id
                                    val count = interactor.countTasksByProjectId(projectId)
                                    val timestamp = when(count > 0) {
                                        true -> interactor.getLastTaskTimestampsByProjectId(projectId)
                                        false -> projectEntity.createdTimestamp
                                    }
                                    val formattedDate = Constants.userFormat.format(Date(timestamp))

                                    val completedTasks
                                    = interactor.countStatesTasksByProjectId(projectId, Constants.TaskStates.COMPLETED.stateID)
                                    val progressTasks
                                            = interactor.countStatesTasksByProjectId(projectId, Constants.TaskStates.IN_PROGRESS.stateID)
                                    val thoughtTasks
                                            = interactor.countStatesTasksByProjectId(projectId, Constants.TaskStates.IN_THOUGHT.stateID)
                                    val countTasksByState = arrayOf(
                                        completedTasks,
                                        progressTasks,
                                        thoughtTasks
                                    )
                                    //TODO(Check queries with incorrect data)
                                    val project = Project(
                                        projectEntity.id,
                                        projectEntity.name,
                                        projectEntity.type,
                                        formattedDate,
                                        countTasksByState
                                    )
                                    _projects.add(project)
                                }
                                Log.d("ProjectsDebug", "fetchProjects 5 ${_projects.size}")
//                                Log.d("ProjectsDebug", "fetchProjects ACTION 1 ${it.size}")
                                sendAction(Action.NotEmptyList(_projects))
                            }
                            false -> sendAction(Action.EmptyList)
                        }
                    }
                }

                is ResultWrapper.Error -> {
                    Log.d("ProjectsDebug", "ERROR: ${projects.exception.localizedMessage}")
                    sendAction(Action.Error)
                }
            }
        }
    }
    */

    private fun onAddProjectClicked(name: String, type: Int) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val project = ProjectEntity(name, type, timestamp)
            val insertResult = interactor.insertProject(project)
            val effect = when (insertResult >= 0) {
                true -> ViewEffect.SuccessAddDialog
                false -> ViewEffect.FailureAddDialog
            }
            viewEffect.value = effect
        }
    }

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnAcceptAddProjectClicked
            -> onAddProjectClicked(
                viewEvent.name,
                viewEvent.type
            )

            is ViewEvent.OnAddProjectClicked
            -> viewEffect.value = ViewEffect.ShowAddProjectDialog

            is ViewEvent.OnItemClicked
            -> viewEffect.value = ViewEffect.GoToTaskList(viewEvent.id)
        }
    }

    override fun onReduceState(viewAction: Action): ViewState {
        return when (viewAction) {
            is Action.Loading
            -> state.copy()

            is Action.SetToolbar
            -> state.copy(
                title = viewAction.title,
                subtitle = viewAction.subtitle
            )

            is Action.EmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true
            )

            is Action.NotEmptyList
            -> state.copy(
                progressBarVisibility = false,
                projectsAdapterVisibility = true,
                projects = viewAction.projects
            )

            is Action.Error
            -> state.copy(
                progressBarVisibility = false,
                errorTextViewVisibility = true
            )
        }
    }

    data class ViewState(
        val progressBarVisibility: Boolean = true,
        val title: String = "",
        val subtitle: String = "",
        val emptyListTextViewVisibility: Boolean = false,
        val projectsAdapterVisibility: Boolean = false,
        val projects: List<Project> = emptyList(),
        val errorTextViewVisibility: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {

        object Loading : Action()

        class SetToolbar(
            val title: String,
            val subtitle: String
        ) : Action()

        object EmptyList : Action()

        class NotEmptyList(
            val projects: List<Project>
        ) : Action()

        object Error : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        object SuccessAddDialog : ViewEffect()

        object FailureAddDialog : ViewEffect()

        class GoToTaskList(
            val projectID: Long
        ) : ViewEffect()

        object ShowAddProjectDialog : ViewEffect()
    }

    sealed class ViewEvent : BaseViewEvent {

        object OnAddProjectClicked : ViewEvent()

        class OnAcceptAddProjectClicked(
            val name: String,
            val type: Int
        ) : ViewEvent()

        class OnItemClicked(
            val id: Long
        ) : ViewEvent()

        object OnDestroyView : ViewEvent()
    }
}