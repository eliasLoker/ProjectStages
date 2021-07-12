package com.example.projectstages.ui.projects

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.base.getStringExt
import com.example.projectstages.customview.SpinnerAdapterWithImage
import com.example.projectstages.databinding.FragmentProjectsBinding
import com.example.projectstages.ui.projects.adapter.ProjectsAdapter
import com.example.projectstages.ui.projects.adapter.ProjectsAdapterListener
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.viewmodel.ProjectsFactory
import com.example.projectstages.ui.projects.viewmodel.ProjectsViewModel
import com.example.projectstages.utils.AdapterItemDecorator

class ProjectsFragment(
    layoutId: Int = R.layout.fragment_projects
) : BaseFragment<
        FragmentProjectsBinding,
        ProjectsViewModel.ViewState,
        ProjectsViewModel.ViewEffect
        >(layoutId, FragmentProjectsBinding::inflate), ProjectsAdapterListener {

    private lateinit var projectsViewModel: ProjectsViewModel
    private lateinit var projectsAdapter: ProjectsAdapter
    private lateinit var navigation: ProjectsNavigationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigation = (activity) as ProjectsNavigationListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectsInteractor = ProjectsInteractor(requireContext().appComponent.projectDao)
        val title = requireContext().resources.getString(R.string.app_name)
        val subtitle = requireContext().getString(R.string.project_subtitle)
        val projectsFactory = ProjectsFactory(title, subtitle, projectsInteractor)
        projectsViewModel = ViewModelProviders
            .of(this, projectsFactory)
            .get(ProjectsViewModel::class.java)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            projectsAdapter = ProjectsAdapter(this@ProjectsFragment)
            adapter = projectsAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_adapter).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

        onEachViewState(projectsViewModel.stateFlow)
        onEachViewEffect(projectsViewModel.viewEffect)

        binding.toolbar.addQuestionMenuButton.setOnClickListener {
            projectsViewModel.processViewEvent(
                ProjectsViewModel.ViewEvent.OnAddProjectClicked
            )
        }
    }

    override fun updateViewState(viewState: ProjectsViewModel.ViewState) {
        binding.apply {
            progressBar.isVisible = viewState.progressBarVisibility
            recyclerView.isVisible = viewState.projectsAdapterVisibility
            projectsAdapter.setList(viewState.projects)
            toolbar.toolbar.apply {
                title = viewState.title
                subtitle = viewState.subtitle
            }
        }
    }

    override fun showSingleEvent(viewEffect: ProjectsViewModel.ViewEffect) {
        when(viewEffect) {
            is ProjectsViewModel.ViewEffect.ShowAddProjectDialog
            -> showAddProjectDialog()

            is ProjectsViewModel.ViewEffect.SuccessAddDialog
            -> showSimpleDialog(
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
        }
    }

    override fun onItemClicked(id: Long) {
        projectsViewModel.processViewEvent(
            ProjectsViewModel.ViewEvent.OnItemClicked(id)
        )
    }

    private fun showSimpleDialog(title: String, message: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(requireContext().getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun showErrorDialog(title: String, message: String) {
        val titleAndMessage = buildSpannedString {
            bold {
                append("$title\n\n")
            }
            italic {
                append(message)
            }
        }

        val marginParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 16, 16, 16)
        }

        val mainVerticalLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val errorImageView = ImageView(requireContext()).apply {
            setBackgroundResource(R.drawable.ic_decline)
            layoutParams = marginParams
        }
        val messageTextView = TextView(requireContext()).apply {
            text = titleAndMessage
            textSize = 18f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            layoutParams = marginParams
        }
        mainVerticalLayout.addView(errorImageView)
        mainVerticalLayout.addView(messageTextView)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(mainVerticalLayout)
            .setCancelable(false)
            .setPositiveButton(requireContext().getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun showAddProjectDialog() {
        val messageWithTitle = buildSpannedString {
            bold {
                append(requireContext().getString(R.string.project_add_category_title))
            }
            italic {
                append(requireContext().getString(R.string.project_add_category_message))
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
        }

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
            projectsViewModel.processViewEvent(
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

    private fun showGoToTasksDialog(id: Long) {
        //TODO("Maybe delete")
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Какой экран открыть?")
            .setPositiveButton("TasksList") { dialog, _ ->
//                goToTasks(id)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}