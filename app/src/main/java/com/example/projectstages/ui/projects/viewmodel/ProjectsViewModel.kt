package com.example.projectstages.ui.projects.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ProjectsViewModel(
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
        fetchProjects()
    }

    private val _projects = ArrayList<Project>()
    private var positionProjectForDeleteOrEdit = 0

    private fun fetchProjects() {
        viewModelScope.launch {
                when(val projects = interactor.getProjects()) {
                    is ResultWrapper.Success -> {
                        projects.data.collectLatest { it ->
                            when(it.isNotEmpty()) {
                                true -> {
                                    _projects.clear()
                                    it.forEach { projectsWithTasks ->
                                        val count = projectsWithTasks.tasks.size
                                        val timestamp = when(count > 0) {
                                            true -> {
                                                projectsWithTasks.tasks.maxByOrNull { projectsWithTasks.createdTimestamp }!!.createdTimestamp
                                                //TODO(Противно использовать !!, подумать еще на досуге )
                                            }
                                            false -> projectsWithTasks.createdTimestamp
                                        }

                                        val formattedDate = Constants.userFormatProjects.format(Date(timestamp))

                                        val completedTasks
                                                = projectsWithTasks.tasks
                                            .filter { taskEntity -> taskEntity.state == Constants.TaskStates.COMPLETED.stateID }
                                            .count()

                                        val progressTasks
                                                = projectsWithTasks.tasks
                                            .filter { taskEntity -> taskEntity.state == Constants.TaskStates.IN_PROGRESS.stateID }
                                            .count()

                                        val thoughtTasks
                                                = projectsWithTasks.tasks
                                            .filter { taskEntity -> taskEntity.state == Constants.TaskStates.IN_THOUGHT.stateID }
                                            .count()

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
                        sendAction(Action.Error)
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
                    taskStatisticViewsVisibility = true,
                    projects = viewAction.projects,
                    allTasks = viewAction.allTasks,
                    completedTasks = viewAction.completedTasks
                )

            is Action.EmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                taskStatisticViewsVisibility = false
            )

            is Action.Error
            -> state.copy(
                progressBarVisibility = false,
                errorTextViewVisibility = true,
                taskStatisticViewsVisibility = false
            )
        }
    }

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnAddProjectClicked
            -> sendViewEffect(ViewEffect.ShowAddProjectDialog)

            is ViewEvent.OnAcceptAddProjectClicked
            -> addProject(viewEvent.name, viewEvent.type)

            is ViewEvent.OnItemClicked
            -> {
//                Log.d("ProjectsViewModel", "VE: ${viewEvent.position}")
//                return
                sendViewEffect(ViewEffect.GoToTaskList(
                    _projects[viewEvent.position].id,
                    _projects[viewEvent.position].name))
            }

            is ViewEvent.OnPopupDeleteClicked
            -> onPopupDeleteClicked(viewEvent.position)

            is ViewEvent.OnPopupEditClicked
            -> onPopupEditClicked(viewEvent.position)

            is ViewEvent.OnAcceptDeleteProject
            -> deleteProject()

            is ViewEvent.OnAcceptEditProject
            -> updateProject(viewEvent.name, viewEvent.type)
        }
    }

    private fun updateProject(name: String, type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateResult = interactor.updateProjectById(
                _projects[positionProjectForDeleteOrEdit].id,
                name,
                type
            )
            val effect = when(updateResult > 0) {
                true -> ViewEffect.SuccessEdit
                false -> ViewEffect.FailureEdit
            }
            sendViewEffect(effect)
        }
    }

    private fun deleteProject() {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteResult = interactor.deleteProjectById(_projects[positionProjectForDeleteOrEdit].id)
            val effect = when(deleteResult > 0) {
                true -> ViewEffect.SuccessDelete
                false -> ViewEffect.FailureDelete
            }
            sendViewEffect(effect)
        }
    }

    private fun addProject(name: String, type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val project = ProjectEntity(name, type, System.currentTimeMillis())
            val insertResult = interactor.insertProject(project)
            val effect = when(insertResult > 0) {
                true -> ViewEffect.SuccessAddDialog
                false -> ViewEffect.FailureAddDialog
            }
            sendViewEffect(effect)
        }
    }

    private fun onPopupDeleteClicked(position: Int) {
        positionProjectForDeleteOrEdit = position
        sendViewEffect(ViewEffect.ShowDeleteProjectDialog(
            _projects[position].name
        ))
    }

    private fun onPopupEditClicked(position: Int) {
        positionProjectForDeleteOrEdit = position
        sendViewEffect(ViewEffect.ShowEditProjectDialog(
            _projects[position].name,
            _projects[position].type
        ))
    }

    data class ViewState(
        val progressBarVisibility: Boolean = true,
        val taskStatisticViewsVisibility: Boolean = false,
        val emptyListTextViewVisibility: Boolean = false,
        val projectsAdapterVisibility: Boolean = false,
        val projects: List<Project> = emptyList(),
        val errorTextViewVisibility: Boolean = false,
        val allTasks: Int = 0,
        val completedTasks: Int = 0
    ) : BaseViewState

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