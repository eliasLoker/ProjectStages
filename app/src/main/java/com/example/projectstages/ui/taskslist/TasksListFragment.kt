package com.example.projectstages.ui.taskslist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.databinding.FragmentTasksListBinding
import com.example.projectstages.ui.taskslist.adapter.TasksListAdapter
import com.example.projectstages.ui.taskslist.adapter.TasksListAdapterListener
import com.example.projectstages.ui.taskslist.interactor.TasksListInteractor
import com.example.projectstages.ui.taskslist.model.Task
import com.example.projectstages.ui.taskslist.viewmodel.TasksListFactory
import com.example.projectstages.ui.taskslist.viewmodel.TasksListViewModel
import com.example.projectstages.utils.AdapterItemDecorator
import com.example.projectstages.utils.AdapterStickyItemDecorator2
import com.example.projectstages.utils.Constants

class TasksListFragment(
    layoutID: Int = R.layout.fragment_tasks_list
) : BaseFragment<
        FragmentTasksListBinding,
        TasksListViewModel.ViewState,
        TasksListViewModel.Action,
        TasksListViewModel.ViewEffect,
        TasksListViewModel.ViewEvent
        >(layoutID, FragmentTasksListBinding::inflate), TasksListAdapterListener {

    override lateinit var viewModelFactory: ViewModelProvider.Factory
    override val viewModelClass = TasksListViewModel::class

    private lateinit var tasksListAdapter: TasksListAdapter
    private lateinit var navigation: TasksNavigationListener

    override fun onAttach(context: Context) {
        val id = arguments?.getLong(TAG_FOR_PROJECT_ID, 0L) ?: 0L
        val interactor = TasksListInteractor(requireContext().appComponent.projectDao)
        viewModelFactory = TasksListFactory(id, interactor)
        navigation = activity as TasksNavigationListener
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            tasksListAdapter = TasksListAdapter(this@TasksListFragment)
            adapter = tasksListAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_adapter).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

//        binding.toolbar.addQuestionMenuButton.setOnClickListener {
//            viewModel.processViewEvent(
//                TasksListViewModel.ViewEvent.OnAddTaskClicked
//            )
//        }
    }

    override fun updateViewState(viewState: TasksListViewModel.ViewState) {
        binding.apply {
            progressBar.isVisible = viewState.progressBarVisibility
            recyclerView.isVisible = viewState.taskRecyclerVisibility
            tasksListAdapter.setList(viewState.tasks)
            val errorText = when(viewState.errorMessageTextViewType) {
                Constants.EmptyList.EMPTY -> requireContext().getString(R.string.tasks_list_empty)
                Constants.EmptyList.ERROR -> requireContext().getString(R.string.tasks_list_error)
            }
            errorTextView.apply {
                text = errorText
                isVisible = viewState.errorMessageTextViewVisibility
            }
        }
    }

    override fun showViewEffect(viewEffect: TasksListViewModel.ViewEffect) {
        when(viewEffect) {
            is TasksListViewModel.ViewEffect.GoToTask
            -> navigation.goToTask(viewEffect.taskID)

            is TasksListViewModel.ViewEffect.GoToAddTask
            -> navigation.goToAddTask(viewEffect.projectId)
        }
    }

    override fun onTaskClicked(id: Long) {
        viewModel.processViewEvent(
            TasksListViewModel.ViewEvent.OnTaskClicked(id)
        )
    }

    private fun getSectionCallback(tasks: List<Task>): AdapterStickyItemDecorator2.SectionCallback {
        return object : AdapterStickyItemDecorator2.SectionCallback {
            override fun isSection(position: Int): Boolean {
                return position == 0
                        || tasks[position - 1].state != tasks[position].state
            }

            override fun getSectionHeader(position: Int): CharSequence {
                return resources.getStringArray(R.array.task_types)[tasks[position].state]
            }
        }
    }

    companion object {

        private const val TAG_FOR_PROJECT_ID = "PROJECT_ID"


        @JvmStatic
        fun getBundle(projectID: Long) = Bundle().apply {
            putLong(TAG_FOR_PROJECT_ID, projectID)
        }
    }
}