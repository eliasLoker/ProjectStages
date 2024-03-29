package com.example.projectstages.ui.projects.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.BaseViewModel
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.ui.projects.viewmodel.ProjectsContract.ViewEvent
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val interactor: ProjectsInteractor
) : BaseViewModel<
            ProjectsContract.ViewState,
            ProjectsContract.Action,
            ProjectsContract.ViewEffect,
            ViewEvent
            >
        (ProjectsContract.ViewState()) {

    init {
        fetchProjects()
    }

    private val _projects = ArrayList<Project>()
    private var positionProjectForDeleteOrEdit = 0

    private fun fetchProjects() {
        val completedStateID = Constants.TaskStates.COMPLETED.stateID
        val inProgressStateID = Constants.TaskStates.IN_PROGRESS.stateID
        val inThoughtStateID = Constants.TaskStates.IN_THOUGHT.stateID

        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendAction(ProjectsContract.Action.Error)
        }

        viewModelScope.launch(exceptionHandler) {
            val projects = interactor.getProjects()
            projects.collectLatest { it ->
                when(it.isNotEmpty()) {
                    true -> {
                        _projects.clear()
                        it.forEach { projectsWithTasks ->
                            val count = projectsWithTasks.tasks.size
                            val timestamp = when(count > 0) {
                                true -> {
                                    projectsWithTasks.tasks.maxByOrNull { projectsWithTasks.createdTimestamp }!!.updatedTimestamp
                                    //TODO(Противно использовать !!, подумать еще на досуге )
                                }
                                false -> projectsWithTasks.createdTimestamp
                            }

                            val formattedDate = Constants.userFormatProjects.format(Date(timestamp))

                            val completedTasks = projectsWithTasks.tasks
                                .filter { taskEntity -> taskEntity.state == completedStateID }
                                .count()

                            val progressTasks = projectsWithTasks.tasks
                                .filter { taskEntity -> taskEntity.state == inProgressStateID }
                                .count()

                            val thoughtTasks = projectsWithTasks.tasks
                                .filter { taskEntity -> taskEntity.state == inThoughtStateID }
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
                            .filter { taskEntity -> taskEntity.state == completedStateID }
                            .count()
                        sendAction(ProjectsContract.Action.NotEmptyList(_projects, allTasks, completedTasks))
                    }
                    false -> sendAction(ProjectsContract.Action.EmptyList)
                }
            }
        }
    }

    override fun onReduceState(viewAction: ProjectsContract.Action): ProjectsContract.ViewState {
        return when(viewAction) {
            is ProjectsContract.Action.Loading
            -> state.copy()

            is ProjectsContract.Action.NotEmptyList -> state.copy(
                    progressBarVisibility = false,
                    projectsAdapterVisibility = true,
                    headerViewsVisibility = true,
                    projects = viewAction.projects,
                    allTasks = viewAction.allTasks,
                    completedTasks = viewAction.completedTasks,
                    addProjectButtonVisibility = true,
                    errorTextViewVisibility = false,
                )

            is ProjectsContract.Action.EmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                projectsAdapterVisibility = false,
                headerViewsVisibility = false,
                addProjectButtonVisibility = true,
                errorTextViewVisibility = true,
                failureType = Constants.FailureType.EMPTY_LIST
            )

            is ProjectsContract.Action.Error
            -> state.copy(
                progressBarVisibility = false,
                errorTextViewVisibility = true,
                addProjectButtonVisibility = true,
                headerViewsVisibility = false,
                failureType = Constants.FailureType.ERROR
            )
        }
    }

    override fun processViewEvent(viewEvent: ViewEvent) {
        when(viewEvent) {
            is ViewEvent.OnAddProjectClicked
            -> sendViewEffect(ProjectsContract.ViewEffect.ShowAddProjectDialog)

            is ViewEvent.OnAcceptAddProjectClicked
            -> addProject(viewEvent.name, viewEvent.type)

            is ViewEvent.OnItemClicked
            -> sendViewEffect(ProjectsContract.ViewEffect.GoToTaskList(
                    _projects[viewEvent.position].id,
                    _projects[viewEvent.position].name))

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
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendViewEffect(ProjectsContract.ViewEffect.FailureEdit)
        }
        viewModelScope.launch(exceptionHandler) {
            val updateResult = interactor.updateProjectById(
                _projects[positionProjectForDeleteOrEdit].id,
                name,
                type
            )
            val effect = when(updateResult > 0) {
                true -> ProjectsContract.ViewEffect.SuccessEdit
                false -> ProjectsContract.ViewEffect.FailureEdit
            }
            sendViewEffect(effect)
        }
    }

    private fun deleteProject() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendViewEffect(ProjectsContract.ViewEffect.FailureDelete)
        }
        viewModelScope.launch(exceptionHandler) {
            val deleteResult = interactor.deleteProjectById(_projects[positionProjectForDeleteOrEdit].id)
            val effect = when(deleteResult > 0) {
                true -> ProjectsContract.ViewEffect.SuccessDelete
                false -> ProjectsContract.ViewEffect.FailureDelete
            }
            sendViewEffect(effect)
        }
    }

    private fun addProject(name: String, type: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendViewEffect(ProjectsContract.ViewEffect.FailureAddDialog)
        }
        viewModelScope.launch(exceptionHandler) {
            val project = ProjectEntity(name, type, System.currentTimeMillis())
            val insertResult = interactor.insertProject(project)
            val effect = when(insertResult > 0) {
                true -> ProjectsContract.ViewEffect.SuccessAddDialog
                false -> ProjectsContract.ViewEffect.FailureAddDialog
            }
            sendViewEffect(effect)
        }
    }

    private fun onPopupDeleteClicked(position: Int) {
        positionProjectForDeleteOrEdit = position
        sendViewEffect(
            ProjectsContract.ViewEffect.ShowDeleteProjectDialog(
            _projects[position].name
        ))
    }

    private fun onPopupEditClicked(position: Int) {
        positionProjectForDeleteOrEdit = position
        sendViewEffect(
            ProjectsContract.ViewEffect.ShowEditProjectDialog(
            _projects[position].name,
            _projects[position].type
        ))
    }
}