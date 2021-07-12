package com.example.projectstages.ui.projects.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ProjectsViewModel(
    title: String,
    subtitle: String,
    private val interactor: ProjectsInteractor
) :
    BaseViewModel<
            ProjectsViewModel.ViewState,
            ProjectsViewModel.Action,
            ProjectsViewModel.ViewEffect,
            ProjectsViewModel.ViewEvent
            >
        (ViewState()) {

    init {
        sendAction(Action.SetToolbar(title, subtitle))
        fetchProjects()
    }

    private val _projects = ArrayList<Project>()

    private fun fetchProjects() {
        viewModelScope.launch {
            when(val projects = interactor.getProjects2()) {
                is ResultWrapper.Success -> {
                    Log.d("ProjectsViewModel", "SUCCESS ResultWrapper")
                    projects.data.collectLatest { it ->
                        Log.d("ProjectsViewModel", "SUCCESS collectLatest $it")
                        when(it.isNotEmpty()) {
                            true -> {
                                _projects.clear()
                                it.forEach { projectsWithTasks ->
                                    val projectId = projectsWithTasks.id
                                    val count = projectsWithTasks.tasks.size
                                    val timestamp = when(count > 0) {
                                        true -> {
//                                            it.map { it.tasks }.maxOf { it1 -> it1.createdTimestamp }
                                            projectsWithTasks.tasks.maxByOrNull { projectsWithTasks.createdTimestamp }!!.createdTimestamp
                                            //TODO(Противно использовать !!, подумать еще на досуге )
                                        }
                                        false -> projectsWithTasks.createdTimestamp
                                    }

                                    val formattedDate = Constants.userFormatProjects.format(Date(timestamp))

                                    val completedTasks
                                            = projectsWithTasks.tasks.filter { taskEntity -> taskEntity.state == Constants.TaskStates.COMPLETED.stateID }.count()
                                    val progressTasks
                                            = projectsWithTasks.tasks.filter { taskEntity -> taskEntity.state == Constants.TaskStates.IN_PROGRESS.stateID }.count()
                                    val thoughtTasks
                                            = projectsWithTasks.tasks.filter { taskEntity -> taskEntity.state == Constants.TaskStates.IN_THOUGHT.stateID }.count()

                                    val countTasksByState = arrayOf(
                                        completedTasks,
                                        progressTasks,
                                        thoughtTasks
                                    )

                                    val project = Project(
                                        projectsWithTasks.id,
                                        projectsWithTasks.name,
                                        projectsWithTasks.type,
                                        formattedDate,
                                        countTasksByState
                                    )
                                    _projects.add(project)
                                }
                                val allTasks  = it.map { list -> list.tasks }
                                    .flatten()
                                    .count()

                                val completedTasks  = it.map { list -> list.tasks }
                                    .flatten()
                                    .filter { taskEntity -> taskEntity.state == 0 }
                                    .count()

                                sendAction(Action.NotEmptyList(_projects, allTasks, completedTasks))
                            }
                            false -> sendAction(Action.EmptyList)
                        }
                    }
                }

                is ResultWrapper.Error -> {
                    Log.d("ProjectsViewModel", "ERROR")
                }
            }
        }
    }

    override fun onReduceState(viewAction: Action): ViewState {
        return when(viewAction) {
            is Action.Loading
            -> state.copy()

            is Action.NotEmptyList -> state.copy(
                progressBarVisibility = false,
                projectsAdapterVisibility = true,
                projects = viewAction.projects,
                allTasks = viewAction.allTasks,
                completedTasks = viewAction.completedTasks
            )

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

            is Action.Error
            -> state.copy(
                progressBarVisibility = false,
                errorTextViewVisibility = true
            )
        }
    }

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnAddProjectClicked ->
                viewEffectChannel.sendViewEffect(ViewEffect.ShowAddProjectDialog)


            is ViewEvent.OnAcceptAddProjectClicked -> {
                viewModelScope.launch {
                    val project = ProjectEntity(viewEvent.name, viewEvent.type, System.currentTimeMillis())
                    val insertResult = interactor.insertProject(project)
                    val event = when(insertResult > 0) {
                        true -> ViewEffect.SuccessAddDialog
                        false -> ViewEffect.FailureAddDialog
                    }
                    viewEffectChannel.sendViewEffect(event)
                }
            }

            is ViewEvent.OnItemClicked
            -> viewEffectChannel.sendViewEffect(ViewEffect.GoToTaskList(viewEvent.id))
        }
    }

    data class ViewState(
        val progressBarVisibility: Boolean = true,
        val title: String = "",
        val subtitle: String = "",
        val emptyListTextViewVisibility: Boolean = false,
        val projectsAdapterVisibility: Boolean = false,
        val projects: List<Project> = emptyList(),
        val errorTextViewVisibility: Boolean = false,
        val allTasks: Int = 0,
        val completedTasks: Int = 0
    ) : BaseViewState

    sealed class Action : BaseAction {

        object Loading : Action()

        class SetToolbar(
            val title: String,
            val subtitle: String
        ) : Action()

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
    }
}