package com.example.projectstages.ui.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.base.getBooleanFromBundleExt
import com.example.projectstages.base.getStringExt
import com.example.projectstages.base.launchWhenStartedWithCollect
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerAdapterWithImageAndText
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerItem
import com.example.projectstages.databinding.FragmentTaskBinding
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.viewmodel.TaskFactory
import com.example.projectstages.ui.task.viewmodel.TaskViewModel
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.onItemSelected
import com.example.projectstages.utils.onTextChanged
import kotlinx.coroutines.flow.onEach
import com.example.projectstages.base.getLongFromBundleExt as getLongFromBundleExt1

class TaskFragment(
    layoutID: Int = R.layout.fragment_task
) : BaseFragment<
        FragmentTaskBinding,
        TaskViewModel.ViewState,
        TaskViewModel.Action,
        TaskViewModel.ViewEffect,
        TaskViewModel.ViewEvent,
        TaskViewModel
        >(layoutID, FragmentTaskBinding::inflate) {

    private lateinit var taskViewModel: TaskViewModel

    private val stateObserver = Observer<TaskViewModel.ViewState> {
        binding.apply {
            progressBar.isVisible = it.progressBarVisibility
            stateSpinner.isVisible = it.stateSpinnerVisibility
            descriptionTextInputLayout.isVisible = it.descriptionEditTextVisibility
            saveButton.isVisible = it.saveButtonVisibility
            descriptionTextInputEditText.setText(it.descriptionEditTextText)
            stateSpinner.setSelection(it.stateSpinnerPosition)
            when(it.taskTitleType) {
                Constants.TaskTitleType.ADD -> {
                    toolbar.toolbar.subtitle = requireContext().getString(R.string.task_add)
                    saveButton.text = requireContext().getString(R.string.task_save_task)
                    toolbar.deleteQuestionMenuButton.isVisible = false
                }
                Constants.TaskTitleType.EDIT -> {
                    toolbar.toolbar.subtitle = requireContext().getString(R.string.task_edit)
                    saveButton.text = requireContext().getString(R.string.task_save_changes)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectID = getLongFromBundleExt1(PROJECT_ID)
        val taskID = getLongFromBundleExt1(TASK_ID)
        val isEdit = getBooleanFromBundleExt(IS_EDIT)
        val interactor = TaskInteractor(requireContext().appComponent.projectDao)
        val factory = TaskFactory(isEdit, projectID, taskID, interactor)
        taskViewModel = ViewModelProviders
            .of(this, factory)
            .get(TaskViewModel::class.java)

        val states = resources.getStringArray(R.array.task_types)
        val spinnerItems = ArrayList<SpinnerItem>().apply {
            add(SpinnerItem(states[0], R.drawable.ic_completed))
            add(SpinnerItem(states[1], R.drawable.ic_progress))
            add(SpinnerItem(states[2], R.drawable.ic_thought))
        }
        val spinnerAdapter = SpinnerAdapterWithImageAndText(requireContext().applicationContext, spinnerItems)

        binding.stateSpinner.apply {
            adapter = spinnerAdapter
        }

        taskViewModel.stateFlow.onEach {
            //TODO('Не нравится return, подумать еще')
            val viewState = it ?: return@onEach
            updateUI(viewState)
        }.launchWhenStartedWithCollect(lifecycleScope)

        taskViewModel.viewEffect.onEach {
            //TODO('Не нравится return, подумать еще')
            val viewEffect = it ?: return@onEach
            showSingleEvent(viewEffect)
        }.launchWhenStartedWithCollect(lifecycleScope)

        binding.saveButton.setOnClickListener {
            taskViewModel.processViewEvent(
                TaskViewModel.ViewEvent.OnSaveButtonClicked
            )
        }

        binding.descriptionTextInputEditText.onTextChanged {
            taskViewModel.processViewEvent(
                TaskViewModel.ViewEvent.OnTextChangedDescription(it)
            )
        }

        binding.stateSpinner.onItemSelected {
            taskViewModel.processViewEvent(
                TaskViewModel.ViewEvent.OnItemSelectedStateSpinner(it)
            )
        }

        binding.toolbar.deleteQuestionMenuButton.setOnClickListener {
            taskViewModel.processViewEvent(
                TaskViewModel.ViewEvent.OnDeleteButtonClicked
            )
        }
    }

    private fun updateUI(viewState: TaskViewModel.ViewState) {
        binding.apply {
            progressBar.isVisible = viewState.progressBarVisibility
            stateSpinner.isVisible = viewState.stateSpinnerVisibility
            descriptionTextInputLayout.isVisible = viewState.descriptionEditTextVisibility
            saveButton.isVisible = viewState.saveButtonVisibility
            descriptionTextInputEditText.setText(viewState.descriptionEditTextText)
            stateSpinner.setSelection(viewState.stateSpinnerPosition)
            when(viewState.taskTitleType) {
                Constants.TaskTitleType.ADD -> {
                    toolbar.toolbar.subtitle = getStringExt(R.string.task_add)
                    saveButton.text = getStringExt(R.string.task_save_task)
                    toolbar.deleteQuestionMenuButton.isVisible = false
                }
                Constants.TaskTitleType.EDIT -> {
                    toolbar.toolbar.subtitle = getStringExt(R.string.task_edit)
                    saveButton.text = getStringExt(R.string.task_save_changes)
                }
            }
        }
    }

    private fun showSingleEvent(viewEffect: TaskViewModel.ViewEffect) {

    }

    private fun showDeleteDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Вы действительно хотите удалить задачу?")
            .setPositiveButton("Да") { dialog, _ ->
                taskViewModel.processViewEvent(
                    TaskViewModel.ViewEvent.OnAcceptDeleteClicked
                )
                dialog.dismiss()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    companion object {

        private const val PROJECT_ID = "PROJECT_ID"
        private const val TASK_ID = "TASK_ID"
        private const val IS_EDIT = "IS_EDIT"

        @JvmStatic
        fun getBundleEditTask(taskID: Long) = Bundle().apply {
            putBoolean(IS_EDIT, true)
            putLong(TASK_ID, taskID)
        }

        @JvmStatic
        fun getBundleCreateTask(projectID: Long) = Bundle().apply {
            putLong(PROJECT_ID, projectID)
        }
    }
}