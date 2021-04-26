package com.example.projectstages.ui.taskslist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.base.observeViewEffect
import com.example.projectstages.base.viewmodel.BaseViewEffect
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
        TasksListViewModel.Action,
        TasksListViewModel.ViewEffect,
        TasksListViewModel.ViewEvent,
        TasksListViewModel
        >(layoutID, FragmentTasksListBinding::inflate), TasksListAdapterListener {

    private lateinit var tasksListViewModel: TasksListViewModel
    private lateinit var tasksListAdapter: TasksListAdapter

    private val stateObserver = Observer<TasksListViewModel.ViewState> {
        binding.apply {
            progressBar.isVisible = it.progressBarVisibility
            recyclerView.isVisible = it.taskRecyclerVisibility
            tasksListAdapter.setList(it.tasks)
            val errorText = when(it.errorMessageTextViewType) {
                Constants.EmptyList.EMPTY -> requireContext().getString(R.string.tasks_list_empty)
                Constants.EmptyList.ERROR -> requireContext().getString(R.string.tasks_list_error)
            }
            errorTextView.apply {
                text = errorText
                isVisible = it.errorMessageTextViewVisibility
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(TAG_FOR_ID, 0L) ?: 0L

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

//        observeViewState2(tasksListViewModel.stateLiveData, stateObserver)
        tasksListViewModel.stateLiveData.observe(viewLifecycleOwner, stateObserver)
        observeViewEffect(tasksListViewModel.viewEffect, viewEffectObserver)
        //TODO(Понять, почему observeViewState отрабатывает, как я написал, а не как я задумывал
        // изначально")

        binding.toolbar.addQuestionMenuButton.setOnClickListener {
            tasksListViewModel.processViewEvent(
                TasksListViewModel.ViewEvent.OnAddTaskClicked
            )
        }
    }

    override fun processViewEffect(viewEffect: BaseViewEffect) {
        when(viewEffect) {
            is TasksListViewModel.ViewEffect.GoToAddTask
            -> goToTaskAdd(viewEffect.projectId)

            is TasksListViewModel.ViewEffect.GoToTask
            -> goToTaskEdit(viewEffect.taskID)
        }
    }

    override fun onTaskClicked(id: Long) {
        tasksListViewModel.processViewEvent(
            TasksListViewModel.ViewEvent.OnTaskClicked(id)
        )
    }

    private fun goToTaskEdit(id: Long) {
        val bundle = TaskFragment.getBundleEditTask(id)
        findNavController().navigate(R.id.action_tasksFragment_to_taskFragment, bundle)
    }

    private fun goToTaskAdd(projectID: Long) {
        val bundle = TaskFragment.getBundleCreateTask(projectID)
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

        private const val TAG_FOR_ID = "ID"

        @JvmStatic
        fun getBundle(id: Long) = Bundle().apply { putLong(TAG_FOR_ID, id) }
    }
}