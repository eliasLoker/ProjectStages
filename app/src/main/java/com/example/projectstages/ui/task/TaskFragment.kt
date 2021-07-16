package com.example.projectstages.ui.task

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerAdapterWithImageAndText
import com.example.projectstages.customview.spinnerwithimageandtext.SpinnerItem
import com.example.projectstages.databinding.FragmentTaskBinding
import com.example.projectstages.ui.main.ProjectsNavigationListener
import com.example.projectstages.ui.main.TaskNavigationListener
import com.example.projectstages.ui.task.interactor.TaskInteractor
import com.example.projectstages.ui.task.viewmodel.TaskContract
import com.example.projectstages.ui.task.viewmodel.TaskFactory
import com.example.projectstages.ui.task.viewmodel.TaskViewModel
import com.example.projectstages.utils.*

class TaskFragment(
    layoutID: Int = R.layout.fragment_task
) : BaseFragment<
        FragmentTaskBinding,
        TaskContract.ViewState,
        TaskContract.Action,
        TaskContract.ViewEffect,
        TaskContract.ViewEvent
        >(layoutID, FragmentTaskBinding::inflate) {

    override lateinit var viewModelFactory: ViewModelProvider.Factory
    override val viewModelClass = TaskViewModel::class
    private lateinit var navigation: TaskNavigationListener

    override fun onAttach(context: Context) {
        val projectID = getLongFromBundleExt(PROJECT_ID)
        val taskID = getLongFromBundleExt(TASK_ID)
        val isEdit = getBooleanFromBundleExt(IS_EDIT)
        val interactor = TaskInteractor(requireContext().appComponent.projectDao)
        viewModelFactory = TaskFactory(isEdit, projectID, taskID, interactor)
        navigation = (activity) as TaskNavigationListener
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val states = getStringArrayExt(R.array.task_types)
        val spinnerItems = ArrayList<SpinnerItem>().apply {
            add(SpinnerItem(states[0], R.drawable.ic_completed))
            add(SpinnerItem(states[1], R.drawable.ic_progress))
            add(SpinnerItem(states[2], R.drawable.ic_thought))
        }
        val spinnerAdapter = SpinnerAdapterWithImageAndText(requireContext().applicationContext, spinnerItems)

        binding.stateSpinner.apply {
            adapter = spinnerAdapter
        }

        binding.saveButton.setOnClickListener {
            viewModel.processViewEvent(
                TaskContract.ViewEvent.OnSaveButtonClicked
            )
        }

        binding.descriptionEditText.onTextChanged {
            viewModel.processViewEvent(
                TaskContract.ViewEvent.OnTextChangedDescription(it)
            )
        }

        binding.stateSpinner.onItemSelected {
            viewModel.processViewEvent(
                TaskContract.ViewEvent.OnItemSelectedStateSpinner(it)
            )
        }

        binding.deleteButton.setOnClickListener {
            viewModel.processViewEvent(TaskContract.ViewEvent.OnDeleteButtonClicked)
        }
    }

    override fun updateViewState(viewState: TaskContract.ViewState) {
        binding.apply {
            progressBar.isVisible = viewState.progressBarVisibility
            stateSpinner.isVisible = viewState.stateSpinnerVisibility
//            descriptionTextInputLayout.isVisible = viewState.descriptionEditTextVisibility
            saveButton.isVisible = viewState.saveButtonVisibility
            descriptionEditText.setText(viewState.descriptionEditTextText)
            stateSpinner.setSelection(viewState.stateSpinnerPosition)
            when(viewState.taskType) {
                Constants.TaskTitleType.ADD
                -> saveButton.text = getStringExt(R.string.task_save_task)

                Constants.TaskTitleType.EDIT
                -> saveButton.text = getStringExt(R.string.task_save_changes)
            }
        }
    }

    override fun showViewEffect(viewEffect: TaskContract.ViewEffect) {
        when(viewEffect) {
            is TaskContract.ViewEffect.GoToTaskList
            -> navigation.goToBack()

            is TaskContract.ViewEffect.ShowDeleteDialog
            -> showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Вы действительно хотите удалить задачу?")
            .setPositiveButton("Да") { dialog, _ ->
                viewModel.processViewEvent(
                    TaskContract.ViewEvent.OnAcceptDeleteClicked
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
        fun getBundleForEditTask(taskID: Long) = Bundle().apply {
            putBoolean(IS_EDIT, true)
            putLong(TASK_ID, taskID)
        }

        @JvmStatic
        fun getBundleForCreateTask(projectID: Long) = Bundle().apply {
            putLong(PROJECT_ID, projectID)
        }
    }
}