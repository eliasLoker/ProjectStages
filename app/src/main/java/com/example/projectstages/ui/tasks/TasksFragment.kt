package com.example.projectstages.ui.tasks

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectstages.R
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.databinding.FragmentTasksBinding
import com.example.projectstages.ui.main.TasksNavigationListener
import com.example.projectstages.ui.tasks.adapter.TasksAdapter
import com.example.projectstages.ui.tasks.adapter.TasksAdapterListener
import com.example.projectstages.ui.tasks.viewmodel.*
import com.example.projectstages.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment(
    layoutID: Int = R.layout.fragment_tasks
) : BaseFragment<
        FragmentTasksBinding,
        TasksContract.ViewState,
        TasksContract.Action,
        TasksContract.ViewEffect,
        TasksContract.ViewEvent,
        TasksViewModel
        >(layoutID, FragmentTasksBinding::inflate), TasksAdapterListener {

    override val viewModel  by viewModels<TasksViewModel>()

    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var navigation: TasksNavigationListener

    override fun onAttach(context: Context) {
        navigation = activity as TasksNavigationListener
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            tasksAdapter = TasksAdapter(this@TasksFragment)
            adapter = tasksAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_default).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

        binding.addTaskButton.setOnClickListener {
            viewModel.processViewEvent(
                TasksContract.ViewEvent.OnAddTaskClicked
            )
        }
    }

    override fun updateViewState(viewState: TasksContract.ViewState) {
        binding.apply {
            progressBar.isVisible = viewState.progressBarVisibility
            recyclerView.isVisible = viewState.taskRecyclerVisibility
            tasksAdapter.setList(viewState.tasks)
            val errorText = when(viewState.failureType) {
                Constants.FailureType.EMPTY_LIST -> requireContext().getString(R.string.tasks_list_empty)
                Constants.FailureType.ERROR -> requireContext().getString(R.string.tasks_list_error)
            }
            errorTextView.apply {
                text = errorText
                isVisible = viewState.errorMessageTextViewVisibility
            }
            projectNameTextView.text = viewState.projectName

            placeholderView.isVisible = viewState.headerViewsVisibility
            projectNameTextView.isVisible = viewState.headerViewsVisibility

            addTaskButton.isVisible = viewState.addTaskButtonVisibility
        }
    }

    override fun showViewEffect(viewEffect: TasksContract.ViewEffect) {
        when(viewEffect) {
            is TasksContract.ViewEffect.GoToTask
            -> navigation.goToTask(viewEffect.taskID)

            is TasksContract.ViewEffect.GoToAddTask
            -> navigation.goToAddTask(viewEffect.projectId)
        }
    }

    override fun onTaskClicked(id: Long) {
        viewModel.processViewEvent(
            TasksContract.ViewEvent.OnTaskClicked(id)
        )
    }

    /*
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
    */

    companion object {

        const val TAG_FOR_PROJECT_ID = "PROJECT_ID"
        const val TAG_FOR_PROJECT_NAME = "PROJECT_NAME"

        @JvmStatic
        fun getBundle(projectID: Long, projectName: String) = Bundle().apply {
            putLong(TAG_FOR_PROJECT_ID, projectID)
            putString(TAG_FOR_PROJECT_NAME, projectName)
        }
    }
}