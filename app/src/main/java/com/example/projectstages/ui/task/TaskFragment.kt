package com.example.projectstages.ui.task

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.base.observeViewState
import com.example.projectstages.databinding.FragmentTaskBinding
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.viewmodel.TaskFactory
import com.example.projectstages.ui.task.viewmodel.TaskViewModel
import com.example.projectstages.ui.task.viewmodel.TaskViewModelImpl
import com.example.projectstages.utils.Constants
import com.example.projectstages.utils.spinnerwithimageandtext.SpinnerAdapterWithImageAndText
import com.example.projectstages.utils.spinnerwithimageandtext.SpinnerItem

class TaskFragment(
    layoutID: Int = R.layout.fragment_task
) : BaseFragment<FragmentTaskBinding>(layoutID, FragmentTaskBinding::inflate) {

    private lateinit var taskViewModel: TaskViewModel

    private val stateObserver = Observer<TaskViewModelImpl.ViewState> {
//        Log.d("TaskDebug", "state: $it")
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val taskID = arguments?.getLong(TASK_ID)
        val isEdit = arguments?.getBoolean(IS_EDIT) ?: false
        val interactor = TaskInteractor(requireContext().appComponent.projectDao)
        Log.d("TaskDebug", "onActivityCreated $taskID")
        val factory = TaskFactory(isEdit, taskID, interactor)
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
        /*
        val spinnerAdapter = ArrayAdapter
            .createFromResource(
                requireContext(),
                R.array.task_types,
                android.R.layout.simple_spinner_item
            )
        */
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.apply {
            adapter = spinnerAdapter
        }

        taskViewModel.onActivityCreated(savedInstanceState == null)

        observeViewState(taskViewModel.stateLiveData, stateObserver)
//        observe(taskViewModel.stateLiveData, stateObserver)
    }

    companion object {

        private const val TASK_ID = "TASK_ID"
        private const val IS_EDIT = "IS_EDIT"

        @JvmStatic
        fun getBundle(taskID: Long?, isEdit: Boolean) = Bundle().apply {
            putBoolean(IS_EDIT, isEdit)
            taskID?.let {
                putLong(TASK_ID, taskID)
            }
        }
    }
}