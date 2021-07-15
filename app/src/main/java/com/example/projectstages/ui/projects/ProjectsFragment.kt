package com.example.projectstages.ui.projects

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.customview.SpinnerAdapterWithImage
import com.example.projectstages.databinding.FragmentProjectsBinding
import com.example.projectstages.ui.main.ProjectsNavigationListener
import com.example.projectstages.ui.projects.adapter.ProjectsAdapter
import com.example.projectstages.ui.projects.adapter.ProjectsAdapterListener
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.viewmodel.ProjectsFactory
import com.example.projectstages.ui.projects.viewmodel.ProjectsViewModel
import com.example.projectstages.utils.*

class ProjectsFragment(
    layoutId: Int = R.layout.fragment_projects
) : BaseFragment<
        FragmentProjectsBinding,
        ProjectsViewModel.ViewState,
        ProjectsViewModel.Action,
        ProjectsViewModel.ViewEffect,
        ProjectsViewModel.ViewEvent
        >(layoutId, FragmentProjectsBinding::inflate), ProjectsAdapterListener {

    override lateinit var viewModelFactory: ViewModelProvider.Factory
    override val viewModelClass = ProjectsViewModel::class

    private lateinit var projectsAdapter: ProjectsAdapter
    private lateinit var navigation: ProjectsNavigationListener

    override fun onAttach(context: Context) {
        val projectsInteractor = ProjectsInteractor(requireContext().appComponent.projectDao)
        viewModelFactory = ProjectsFactory(projectsInteractor)
        navigation = (activity) as ProjectsNavigationListener
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            projectsAdapter = ProjectsAdapter(this@ProjectsFragment)
            adapter = projectsAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_adapter).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

        binding.addProjectButton.setOnClickListener {
            viewModel.processViewEvent(
                ProjectsViewModel.ViewEvent.OnAddProjectClicked
            )
        }
    }

    override fun updateViewState(viewState: ProjectsViewModel.ViewState) {
        binding.apply {
            progressBar.isVisible = viewState.progressBarVisibility
            recyclerView.isVisible = viewState.projectsAdapterVisibility
            projectsAdapter.setList(viewState.projects)
            allTasksTextView.text = String.format(getStringExt(R.string.all_tasks), viewState.allTasks)
            completedTasksTextView.text = String.format(getStringExt(R.string.completed_tasks), viewState.completedTasks)

            placeholderView.isVisible = viewState.taskStatisticViewsVisibility
            verticalSeparatorView.isVisible = viewState.taskStatisticViewsVisibility
            allTasksTextView.isVisible = viewState.taskStatisticViewsVisibility
            completedTasksTextView.isVisible = viewState.taskStatisticViewsVisibility

            errorTextView.isVisible = viewState.errorTextViewVisibility
        }
    }

    override fun showViewEffect(viewEffect: ProjectsViewModel.ViewEffect) {
        when(viewEffect) {
            is ProjectsViewModel.ViewEffect.ShowAddProjectDialog
            -> showAddProjectDialog()

            is ProjectsViewModel.ViewEffect.SuccessAddDialog
            -> showSuccessDialog(
                getStringExt(R.string.project_success_add_title),
                getStringExt(R.string.project_success_add_message)
            )

            is ProjectsViewModel.ViewEffect.FailureAddDialog
            -> showErrorDialog(
                getStringExt(R.string.project_error_add_title),
                getStringExt(R.string.project_error_add_message)
            )

            is ProjectsViewModel.ViewEffect.GoToTaskList
            -> navigation.goToTaskFromProjects(viewEffect.projectID)

            is ProjectsViewModel.ViewEffect.ShowDeleteProjectDialog
            -> showDeleteProjectDialog(viewEffect.name)

            is ProjectsViewModel.ViewEffect.SuccessDelete
            -> showSuccessDialog(getStringExt(R.string.completed),getStringExt(R.string.delete_success))

            is ProjectsViewModel.ViewEffect.FailureDelete
            -> showErrorDialog(getString(R.string.error),getStringExt(R.string.delete_error))

            is ProjectsViewModel.ViewEffect.ShowEditProjectDialog
            -> showEditProjectDialog(viewEffect.name, viewEffect.type)

            is ProjectsViewModel.ViewEffect.SuccessEdit
            -> showSuccessDialog(getStringExt(R.string.completed),getStringExt(R.string.update_success))

            is ProjectsViewModel.ViewEffect.FailureEdit
            -> showErrorDialog(getStringExt(R.string.error), getStringExt(R.string.update_error))
        }
    }

    override fun onItemClicked(id: Long) {
        viewModel.processViewEvent(
            ProjectsViewModel.ViewEvent.OnItemClicked(id)
        )
    }

    override fun onPopupEditClicked(position: Int) {
        viewModel.processViewEvent(
            ProjectsViewModel.ViewEvent.OnPopupEditClicked(position)
        )
    }

    override fun onPopupDeleteClicked(position: Int) {
        viewModel.processViewEvent(
            ProjectsViewModel.ViewEvent.OnPopupDeleteClicked(position)
        )
    }

    private fun showEditProjectDialog(name: String, type: Int) {
        val messageWithTitle = buildSpannedString {
            bold {
                append(getStringExt(R.string.project_edit_category_title))
            }
        }

        val mainVerticalLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35)
            background = ContextCompat.getDrawable(requireContext(), R.color.blue_white)
        }

        val horizontalLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(requireContext(), R.color.blue_white)
            gravity = Gravity.CENTER_VERTICAL
        }

        val marginParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val messageTextView = TextView(requireContext()).apply {
            text = messageWithTitle
            textSize = 18f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        val projectNameEditText = EditText(requireContext()).apply {
            hint = requireContext().getString(R.string.project_name)
            layoutParams = marginParams
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
            filters = arrayOf(InputFilter.LengthFilter(20))
            setText(name)
        }
        //TODO("Add textInputLayout with symbol counter in future")

        val addButton = Button(requireContext()).apply {
            text = requireContext().getString(R.string.edit)
            layoutParams = marginParams
        }

        val cancelButton = Button(requireContext()).apply {
            text = getStringExt(R.string.cancel)
        }

        val spinner = Spinner(requireContext())

        val drawableIds = arrayOf(
            R.drawable.ic_file_yellow_mini,
            R.drawable.ic_file_red_mini,
            R.drawable.ic_file_green_mini,
            R.drawable.ic_file_blue_mini,
            R.drawable.ic_file_pink_mini,
            R.drawable.ic_file_black_mini
        )
        val adapter = SpinnerAdapterWithImage(requireContext(), drawableIds)
        spinner.adapter = adapter
        spinner.setSelection(type)

        horizontalLayout.apply {
            addView(spinner)
            addView(projectNameEditText)
        }

        mainVerticalLayout.apply {
            addView(messageTextView)
            addView(horizontalLayout)
            addView(addButton)
            addView(cancelButton)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setView(mainVerticalLayout)
            .create()
        addButton.setOnClickListener {
            viewModel.processViewEvent(
                ProjectsViewModel.ViewEvent.OnAcceptEditProject(
                    projectNameEditText.text.toString(),
                    spinner.selectedItemPosition
                )
            )
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDeleteProjectDialog(projectName: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getStringExt(R.string.delete_title))
            .setMessage(String.format(getStringExt(R.string.delete_message), projectName))
            .setCancelable(false)
            .setPositiveButton(requireContext().getString(R.string.ok)) { dialog, _ ->
                viewModel.processViewEvent(
                    ProjectsViewModel.ViewEvent.OnAcceptDeleteProject
                )
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }


    private fun showAddProjectDialog() {
        val messageWithTitle = buildSpannedString {
            bold {
                append(getStringExt(R.string.project_add_category_title))
            }
            italic {
                append(getStringExt(R.string.project_add_category_message))
            }
        }

        val mainVerticalLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35)
            background = ContextCompat.getDrawable(requireContext(), R.color.blue_white)
        }

        val horizontalLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(35)
            background = ContextCompat.getDrawable(requireContext(), R.color.blue_white)
            gravity = Gravity.CENTER_VERTICAL
        }

        val marginParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val messageTextView = TextView(requireContext()).apply {
            text = messageWithTitle
            textSize = 18f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        val projectNameEditText = EditText(requireContext()).apply {
            hint = requireContext().getString(R.string.project_name)
            layoutParams = marginParams
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
            filters = arrayOf(InputFilter.LengthFilter(20))
        }
        //TODO("Add textInputLayout with symbol counter in future")

        val addButton = Button(requireContext()).apply {
            text = requireContext().getString(R.string.add)
            layoutParams = marginParams
        }

        val cancelButton = Button(requireContext()).apply {
            text = requireContext().getString(R.string.cancel)
        }

        val spinner = Spinner(requireContext())

        val drawableIds = arrayOf(
            R.drawable.ic_file_yellow_mini,
            R.drawable.ic_file_red_mini,
            R.drawable.ic_file_green_mini,
            R.drawable.ic_file_blue_mini,
            R.drawable.ic_file_pink_mini,
            R.drawable.ic_file_black_mini
        )
        val adapter = SpinnerAdapterWithImage(requireContext(), drawableIds)
        spinner.adapter = adapter

        horizontalLayout.apply {
            addView(spinner)
            addView(projectNameEditText)
        }

        mainVerticalLayout.apply {
            addView(messageTextView)
            addView(horizontalLayout)
            addView(addButton)
            addView(cancelButton)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setView(mainVerticalLayout)
            .create()
        addButton.setOnClickListener {
            viewModel.processViewEvent(
                ProjectsViewModel.ViewEvent.OnAcceptAddProjectClicked(
                    projectNameEditText.text.toString(),
                    spinner.selectedItemPosition
                )
            )
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}