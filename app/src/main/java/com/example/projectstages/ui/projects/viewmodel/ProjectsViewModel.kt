package com.example.projectstages.ui.projects.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.projectstages.base.viewmodel.*
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.ui.projects.viewmodel.ProjectsContract.ViewEvent
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ProjectsViewModel(
    private val interactor: ProjectsInteractor
) :
    BaseViewModel<
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
        viewModelScope.launch {
//            delay(1000)
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
                                    sendAction(ProjectsContract.Action.NotEmptyList(_projects, allTasks, completedTasks))
                                }
                                false -> sendAction(ProjectsContract.Action.EmptyList)
                            }
                        }
                    }

                    is ResultWrapper.Error -> {
                        sendAction(ProjectsContract.Action.Error)
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
                    addProjectButtonVisibility = true
                )

            is ProjectsContract.Action.EmptyList
            -> state.copy(
                progressBarVisibility = false,
                emptyListTextViewVisibility = true,
                headerViewsVisibility = true,
                addProjectButtonVisibility = true
            )

            is ProjectsContract.Action.Error
            -> state.copy(
                progressBarVisibility = false,
                errorTextViewVisibility = true,
                headerViewsVisibility = false
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
            -> {
//                Log.d("ProjectsViewModel", "VE: ${viewEvent.position}")
//                return
                sendViewEffect(ProjectsContract.ViewEffect.GoToTaskList(
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
                true -> ProjectsContract.ViewEffect.SuccessEdit
                false -> ProjectsContract.ViewEffect.FailureEdit
            }
            sendViewEffect(effect)
        }
    }

    private fun deleteProject() {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteResult = interactor.deleteProjectById(_projects[positionProjectForDeleteOrEdit].id)
            val effect = when(deleteResult > 0) {
                true -> ProjectsContract.ViewEffect.SuccessDelete
                false -> ProjectsContract.ViewEffect.FailureDelete
            }
            sendViewEffect(effect)
        }
    }

    private fun addProject(name: String, type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
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