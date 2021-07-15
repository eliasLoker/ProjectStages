package com.example.projectstages.ui.tasks

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.databinding.FragmentTasksBinding
import com.example.projectstages.ui.main.TasksNavigationListener
import com.example.projectstages.ui.tasks.adapter.TasksAdapter
import com.example.projectstages.ui.tasks.adapter.TasksAdapterListener
import com.example.projectstages.ui.tasks.interactor.TasksInteractor
import com.example.projectstages.ui.tasks.model.Task
import com.example.projectstages.ui.tasks.viewmodel.TasksFactory
import com.example.projectstages.ui.tasks.viewmodel.TasksViewModel
import com.example.projectstages.utils.AdapterItemDecorator
import com.example.projectstages.utils.AdapterStickyItemDecorator2
import com.example.projectstages.utils.Constants

class TasksFragment(
    layoutID: Int = R.layout.fragment_tasks
) : BaseFragment<
        FragmentTasksBinding,
        TasksViewModel.ViewState,
        TasksViewModel.Action,
        TasksViewModel.ViewEffect,
        TasksViewModel.ViewEvent
        >(layoutID, FragmentTasksBinding::inflate), TasksAdapterListener {

    override lateinit var viewModelFactory: ViewModelProvider.Factory
    override val viewModelClass = TasksViewModel::class

    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var navigation: TasksNavigationListener

    override fun onAttach(context: Context) {
        val id = arguments?.getLong(TAG_FOR_PROJECT_ID, 0L) ?: 0L
        val interactor = TasksInteractor(requireContext().appComponent.projectDao)
        viewModelFactory = TasksFactory(id, interactor)
        navigation = activity as TasksNavigationListener
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            tasksAdapter = TasksAdapter(this@TasksFragment)
            adapter = tasksAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_adapter).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

//        binding.toolbar.addQuestionMenuButton.setOnClickListener {
//            viewModel.processViewEvent(
//                TasksListViewModel.ViewEvent.OnAddTaskClicked
//            )
//        }
    }

    override fun updateViewState(viewState: TasksViewModel.ViewState) {
        binding.apply {
            progressBar.isVisible = viewState.progressBarVisibility
            recyclerView.isVisible = viewState.taskRecyclerVisibility
            tasksAdapter.setList(viewState.tasks)
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

    override fun showViewEffect(viewEffect: TasksViewModel.ViewEffect) {
        when(viewEffect) {
            is TasksViewModel.ViewEffect.GoToTask
            -> navigation.goToTask(viewEffect.taskID)

            is TasksViewModel.ViewEffect.GoToAddTask
            -> navigation.goToAddTask(viewEffect.projectId)
        }
    }

    override fun onTaskClicked(id: Long) {
        viewModel.processViewEvent(
            TasksViewModel.ViewEvent.OnTaskClicked(id)
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