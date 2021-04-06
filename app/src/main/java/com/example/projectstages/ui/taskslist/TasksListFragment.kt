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
import com.example.projectstages.base.observeViewState
import com.example.projectstages.databinding.FragmentTasksListBinding
import com.example.projectstages.ui.task.TaskFragment
import com.example.projectstages.ui.taskslist.adapter.TasksListAdapter
import com.example.projectstages.ui.taskslist.event.TasksListNavigationEvents
import com.example.projectstages.ui.taskslist.interactor.TasksListInteractor
import com.example.projectstages.ui.taskslist.model.Task
import com.example.projectstages.ui.taskslist.viewmodel.TasksListFactory
import com.example.projectstages.ui.taskslist.viewmodel.TasksListViewModel
import com.example.projectstages.ui.taskslist.viewmodel.TasksListViewModelImpl
import com.example.projectstages.utils.AdapterItemDecorator
import com.example.projectstages.utils.AdapterStickyItemDecorator2
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.showToast

class TasksListFragment(
    layoutID: Int = R.layout.fragment_tasks_list
) : BaseFragment<FragmentTasksListBinding>(layoutID, FragmentTasksListBinding::inflate) {

    private lateinit var tasksListViewModel: TasksListViewModel
    private lateinit var tasksListAdapter: TasksListAdapter

    private val stateObserver = Observer<TasksListViewModelImpl.ViewState> {
        binding.apply {
            progressBar.isVisible = it.progressBarVisibility
            recyclerView.isVisible = it.taskRecyclerVisibility
            tasksListAdapter.setList(it.tasks)
            val errorText = when(it.errorMessageTextViewType) {
                Constants.EmptyList.EMPTY -> "Список задач пуст"
                Constants.EmptyList.ERROR -> "Ошибка получения списка задач"
            }
            errorTextView.text = errorText
            binding.errorTextView.isVisible = it.errorMessageTextViewVisibility
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(TAG_FOR_ID, 0L) ?: 0L

        val interactor = TasksListInteractor(requireContext().appComponent.projectDao)
        val factory = TasksListFactory(id, interactor)
        tasksListViewModel = ViewModelProviders
            .of(this, factory)
            .get(TasksListViewModelImpl::class.java)
        tasksListViewModel.onViewCreated(savedInstanceState == null)

        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//            layoutManager = GridLayoutManager(requireContext(), 2)
            tasksListAdapter = TasksListAdapter(tasksListViewModel)
            adapter = tasksListAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_adapter).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

        observeViewState(tasksListViewModel.stateLiveData, stateObserver)

        binding.toolbar.addQuestionMenuButton.setOnClickListener {
            tasksListViewModel.onGoToAddTaskClicked()
        }

        tasksListViewModel.tasksListNavigationEvents.observe(viewLifecycleOwner, {
            when (it) {

                is TasksListNavigationEvents.SuccessDelete
                -> requireContext().showToast("Успешно удалено")

                is TasksListNavigationEvents.FailureDelete
                -> requireContext().showToast("Ошибка удаления")

                is TasksListNavigationEvents.SuccessUpdate
                -> requireContext().showToast("Таск успешно изменен")

                is TasksListNavigationEvents.FailureUpdate
                -> requireContext().showToast("Ошибка изменения")

                is TasksListNavigationEvents.GoToTask
                -> goToTaskEdit(it.taskID, true)

                is TasksListNavigationEvents.GoToAddTask
                -> goToTaskAdd(it.projectID)
            }
        })
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

    private fun goToTaskEdit(id: Long?, isEdit: Boolean) {
        val bundle = TaskFragment.getBundleEditTask(id, isEdit)
        findNavController().navigate(R.id.action_tasksFragment_to_taskFragment, bundle)
    }

    private fun goToTaskAdd(projectID: Long) {
        val bundle = TaskFragment.getBundleCreateTask(projectID)
        findNavController().navigate(R.id.action_tasksFragment_to_taskFragment, bundle)
    }

    companion object {

        private const val TAG_FOR_ID = "ID"

        @JvmStatic
        fun getBundle(id: Long) = Bundle().apply { putLong(TAG_FOR_ID, id) }
    }
}