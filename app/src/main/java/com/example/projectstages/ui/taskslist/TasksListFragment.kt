package com.example.projectstages.ui.taskslist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.databinding.FragmentTasksListBinding
import com.example.projectstages.ui.task.TaskFragment
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
        TasksListViewModel.ViewEffect
        >(layoutID, FragmentTasksListBinding::inflate), TasksListAdapterListener {

    private lateinit var tasksListViewModel: TasksListViewModel
    private lateinit var tasksListAdapter: TasksListAdapter
    private lateinit var navigation: TasksNavigationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigation = activity as TasksNavigationListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(TAG_FOR_PROJECT_ID, 0L) ?: 0L

        val interactor = TasksListInteractor(requireContext().appComponent.projectDao)
        val factory = TasksListFactory(id, interactor)
        tasksListViewModel = ViewModelProviders
            .of(this, factory)
            .get(TasksListViewModel::class.java)

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            tasksListAdapter = TasksListAdapter(this@TasksListFragment)
            adapter = tasksListAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_adapter).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

        onEachViewState(tasksListViewModel.stateFlow)
        onEachViewEffect(tasksListViewModel.viewEffect)

        binding.toolbar.addQuestionMenuButton.setOnClickListener {
            tasksListViewModel.processViewEvent(
                TasksListViewModel.ViewEvent.OnAddTaskClicked
            )
        }
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

    override fun showSingleEvent(viewEffect: TasksListViewModel.ViewEffect) {
        when(viewEffect) {
            is TasksListViewModel.ViewEffect.GoToTask
            -> navigation.goToTask(viewEffect.taskID)

            is TasksListViewModel.ViewEffect.GoToAddTask
            -> navigation.goToAddTask(viewEffect.projectId)
        }
    }

    override fun onTaskClicked(id: Long) {
        tasksListViewModel.processViewEvent(
            TasksListViewModel.ViewEvent.OnTaskClicked(id)
        )
    }

    private fun goToTaskEdit(id: Long) {
        val bundle = TaskFragment.getBundleForEditTask(id)
        findNavController().navigate(R.id.action_tasksFragment_to_taskFragment, bundle)
    }

    private fun goToTaskAdd(projectID: Long) {
        val bundle = TaskFragment.getBundleForCreateTask(projectID)
        findNavController().navigate(R.id.action_tasksFragment_to_taskFragment, bundle)
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