package com.example.projectstages.ui.projects.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.ui.projects.adapter.ProjectsAdapterListener
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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
        (ViewState()), ProjectsAdapterListener {

    private val _projects = ArrayList<Project>()
    private val userFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")

//    override val _navigationEvents = SingleLiveEvent<ProjectsNavigationEvents>()

    override fun onViewCreated(isFirstLoading: Boolean) {
        sendAction(Action.SetToolbar(title, subtitle))
        fetchProjects()
    }

    private fun fetchProjects() {
        viewModelScope.launch {
            when (val projects = interactor.getProjects()) {
                is ResultWrapper.Success -> {
                    projects.data.collectLatest {
                        when(it.isNotEmpty()) {
                            true -> {
                                _projects.clear()
                                it.forEach{ projectEntity ->
                                    val projectId = projectEntity.id
                                    val count = interactor.countTasksByProjectId(projectId)
                                    val timestamp = when(count > 0) {
                                        true -> interactor.getLastTaskTimestampsByProjectId(projectId)
                                        false -> projectEntity.createdTimestamp
                                    }
                                    val formattedDate = userFormat.format(Date(timestamp))

                                    val completedTasks
                                    = interactor.countStatesTasksByProjectId(projectId, 0)
                                    val progressTasks
                                            = interactor.countStatesTasksByProjectId(projectId, 1)
                                    val thoughtTasks
                                            = interactor.countStatesTasksByProjectId(projectId, 2)
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

//    override fun onAddButtonClicked(name: String, type: Int) {
//        viewModelScope.launch {
//            val timestamp = System.currentTimeMillis()
//            val project = ProjectEntity(name, type, timestamp)
//            val insertResult = interactor.insertProject(project)
//            val event = when (insertResult < 0) {
//                true -> ProjectsNavigationEvents.FailureAddDialog
//                false -> ProjectsNavigationEvents.SuccessAddDialog
//            }
//            _navigationEvents.value = event
//        }
//    }

//    override fun onItemClicked(id: Long) {
//        //TODO("Подумать, феншуйно ли передавать вот так айдишник")
//        _navigationEvents.value = ProjectsNavigationEvents.GoToProjectDetails(id)
//    }

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

    override fun processViewEvent(viewEvent: ViewEvent) {
        Log.d("ProjectsDebug", "processViewEvent $viewEvent")
        when(viewEvent) {
            is ViewEvent.OnAcceptAddProjectClicked
            -> onAddProjectClicked(
                viewEvent.name,
                viewEvent.type
            )

            is ViewEvent.OnAddProjectClicked
            -> viewEffect.value = ViewEffect.ShowAddProjectDialog
        }
    }

    private fun onAddProjectClicked(name: String, type: Int) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val project = ProjectEntity(name, type, timestamp)
            val insertResult = interactor.insertProject(project)
            val effect = when (insertResult < 0) {
                true -> ViewEffect.FailureAddDialog
                false -> ViewEffect.SuccessAddDialog
            }
//            _navigationEvents.value = event
//            viewEffect.setValue(effect)
            viewEffect.value = effect
        }
    }

    override fun onItemClicked(id: Long) {
        TODO("Not yet implemented")
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

        class GoToProjectDetails(
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
    }
}