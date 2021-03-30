package com.example.projectstages.ui.taskslist

import android.os.Bundle
import android.util.Log
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
import com.example.projectstages.utils.showToast

class TasksListFragment(
    layoutID: Int = R.layout.fragment_tasks_list
) : BaseFragment<FragmentTasksListBinding>(layoutID, FragmentTasksListBinding::inflate) {

    private lateinit var tasksListViewModel: TasksListViewModel
    private lateinit var tasksListAdapter: TasksListAdapter

    private val stateObserver = Observer<TasksListViewModelImpl.ViewState> {
        Log.d("TaskDebug", "TasksFragment state: $it")
        binding.apply {
            progressBar.isVisible = it.progressBarVisibility
            recyclerView.isVisible = it.taskRecyclerVisibility
            tasksListAdapter.setList(it.tasks)
            /*
            //TODO("Доделать декоратор, который добавит Sticky header")
            val sectionItemDecoration = AdapterStickyItemDecorator2(
                getSectionCallback(it.tasks),
                resources.getDimensionPixelSize(R.dimen.margin_adapter)
            )
            recyclerView.addItemDecoration(sectionItemDecoration)
            */
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(TAG_FOR_ID, 0L) ?: 0L
//        binding.textView.text = "Hello, ${id}"

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
            //TODO("Коллбэк в VM")
//            showAddTaskDialog()
//            goToTaskEdit(null, false)
            tasksListViewModel.onGoToAddTaskClicked()
        }

        tasksListViewModel.tasks_list_navigationEvents.observe(viewLifecycleOwner, {
            when (it) {
//                is TasksListNavigationEvents.ShowDeleteTaskDialog -> showDeleteTaskDialog()

                is TasksListNavigationEvents.SuccessDelete
                -> requireContext().showToast("Успешно удалено")

                is TasksListNavigationEvents.FailureDelete
                -> requireContext().showToast("Ошибка удаления")

//                is TasksListNavigationEvents.ShowEditTaskDialog
//                -> showEditTaskDialog(it.description, it.type)

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

    /*
    private fun showEditTaskDialog(description: String, state: Int) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35)
        }

        val marginParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val descriptionEditText = EditText(requireContext()).apply {
            setText(description)
            textSize = 18f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            layoutParams = marginParams
        }

        val spinnerAdapter = ArrayAdapter
            .createFromResource(
                requireContext(),
                R.array.task_types,
                android.R.layout.simple_spinner_item
            )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val taskTypeSpinner = Spinner(requireContext()).apply {
            layoutParams = marginParams
            adapter = spinnerAdapter
        }
        taskTypeSpinner.setSelection(state)

        layout.addView(descriptionEditText)
        layout.addView(taskTypeSpinner)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(layout)
            .setPositiveButton("Изменить") { dialog, _ ->
                tasksListViewModel.onUpdateButtonClicked(
                    descriptionEditText.text.toString(),
                    taskTypeSpinner.selectedItemPosition
                )
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
    */

    /*
    private fun showDeleteTaskDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы действительно хотите удалить задачу?")
            .setPositiveButton("Да") { dialog, _ ->
                tasksListViewModel.onAcceptDeleteTask()
                dialog.dismiss()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
    */

    /*
    private fun showAddTaskDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35)
        }

        val marginParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val messageTextView = TextView(requireContext()).apply {
            text = "Введите задачу"
            textSize = 18f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        val descriptionEditText = EditText(requireContext()).apply {
            hint = "Описание"
            textSize = 18f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            layoutParams = marginParams
        }

        val spinnerAdapter = ArrayAdapter
            .createFromResource(
                requireContext(),
                R.array.task_types,
                android.R.layout.simple_spinner_item
            )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val taskTypeSpinner = Spinner(requireContext()).apply {
            layoutParams = marginParams
            adapter = spinnerAdapter
        }

        layout.addView(messageTextView)
        layout.addView(descriptionEditText)
        layout.addView(taskTypeSpinner)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(layout)
            .setPositiveButton("Добавить") { dialog, _ ->
                tasksListViewModel.onAddTaskButtonClicked(
                    descriptionEditText.text.toString(),
                    taskTypeSpinner.selectedItemPosition
                )
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
    */

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