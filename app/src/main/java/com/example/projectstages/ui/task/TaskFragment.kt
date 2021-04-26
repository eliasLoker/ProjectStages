package com.example.projectstages.ui.task

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.base.observeViewEffect
import com.example.projectstages.base.observeViewState
import com.example.projectstages.base.viewmodel.BaseViewEffect
import com.example.projectstages.databinding.FragmentTaskBinding
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.viewmodel.TaskFactory
import com.example.projectstages.ui.task.viewmodel.TaskViewModel
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.onItemSelected
import com.example.projectstages.utils.onTextChanged
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerAdapterWithImageAndText
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerItem
import com.example.projectstages.utils.showToast

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

        val projectID = arguments?.getLong(PROJECT_ID) ?: 0L
        val taskID = arguments?.getLong(TASK_ID)
        val isEdit = arguments?.getBoolean(IS_EDIT) ?: false
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
        observeViewState(taskViewModel.stateLiveData, stateObserver)
        observeViewEffect(taskViewModel.viewEffect, viewEffectObserver)

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
    }

    override fun processViewEffect(viewEffect: BaseViewEffect) {
        when(viewEffect) {
            is TaskViewModel.ViewEffect.GoToTaskList
            -> findNavController().popBackStack()

            is TaskViewModel.ViewEffect.FailureAdd
            -> requireContext().showToast(requireContext().getString(R.string.task_add_error))

            is TaskViewModel.ViewEffect.FailureUpdate
            -> requireContext().showToast(requireContext().getString(R.string.task_update_error))
        }
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