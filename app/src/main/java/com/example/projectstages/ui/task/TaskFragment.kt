package com.example.projectstages.ui.task

import android.os.Bundle
import android.util.Log
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
import com.example.projectstages.ui.task.viewmodel.TaskViewModelImpl
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.onItemSelected
import com.example.projectstages.utils.onTextChanged
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerAdapterWithImageAndText
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerItem

class TaskFragment(
    layoutID: Int = R.layout.fragment_task
) : BaseFragment<
        FragmentTaskBinding,
        TaskViewModelImpl.ViewState,
        TaskViewModelImpl.Action,
        TaskViewModelImpl.ViewEffect,
        TaskViewModelImpl.ViewEvent,
        TaskViewModelImpl
        >(layoutID, FragmentTaskBinding::inflate) {

    private lateinit var taskViewModel: TaskViewModelImpl

    private val stateObserver = Observer<TaskViewModelImpl.ViewState> {
        binding.apply {
            progressBar.isVisible = it.progressBarVisibility
            stateSpinner.isVisible = it.stateSpinnerVisibility
            descriptionTextInputLayout.isVisible = it.descriptionEditTextVisibility
            saveButton.isVisible = it.saveButtonVisibility
            descriptionTextInputEditText.setText(it.descriptionEditTextText)
            stateSpinner.setSelection(it.stateSpinnerPosition)
            when(it.taskTitleType) {
                Constants.TaskTitleType.ADD -> {
                    toolbar.toolbar.subtitle = "Добавить задачу"
                    saveButton.text = "Сохранить задачу"
                }
                Constants.TaskTitleType.EDIT -> {
                    toolbar.toolbar.subtitle = "Редактировать задачу"
                    saveButton.text = "Сохранить изменения"
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
        Log.d("TaskDebug", "onActivityCreated $taskID")
        val factory = TaskFactory(isEdit, projectID, taskID, interactor)
        taskViewModel = ViewModelProviders
            .of(this, factory)
            .get(TaskViewModelImpl::class.java)

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
                TaskViewModelImpl.ViewEvent.OnSaveButtonClicked
            )
        }

        binding.descriptionTextInputEditText.onTextChanged {
//            taskViewModel.onTextChangedDescription(it)
            //TODO("To viewEvent")
        }

        binding.stateSpinner.onItemSelected {
//            taskViewModel.onItemSelectedStateSpinner(it)
            //TODO("To viewEvent")
        }

        /*
        //TODO("to ViewEffect")
        taskViewModel.taskEvents.observe(viewLifecycleOwner, {
            when(it) {
                is TaskEvents.SuccessAdd -> {
                    //TODO("Maybe add toast")
                    findNavController().popBackStack()
                }
                is TaskEvents.FailureAdd -> requireContext().showToast("Не удалось добавить запись")
            }
        })
        */
    }

    override fun processViewEffect(viewEffect: BaseViewEffect) {
        when(viewEffect) {
            is TaskViewModelImpl.ViewEffect.GoToTaskList
            -> findNavController().popBackStack()
        }
    }

    companion object {

        private const val PROJECT_ID = "PROJECT_ID"
        private const val TASK_ID = "TASK_ID"
        private const val IS_EDIT = "IS_EDIT"

        @JvmStatic
        fun getBundleEditTask(taskID: Long?, isEdit: Boolean) = Bundle().apply {
            putBoolean(IS_EDIT, isEdit)
            taskID?.let {
                putLong(TASK_ID, taskID)
            }
        }

        @JvmStatic
        fun getBundleCreateTask(projectID: Long) = Bundle().apply {
            putLong(PROJECT_ID, projectID)
        }
    }
}