package com.example.projectstages.ui.projects

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectstages.R
import com.example.projectstages.app.App.Companion.appComponent
import com.example.projectstages.base.BaseFragment
import com.example.projectstages.base.observeViewState
import com.example.projectstages.databinding.FragmentProjectsBinding
import com.example.projectstages.ui.projects.adapter.ProjectsAdapter
import com.example.projectstages.ui.projects.events.ProjectsNavigationEvents
import com.example.projectstages.ui.projects.interactor.ProjectsInteractor
import com.example.projectstages.ui.projects.viewmodel.ProjectsFactory
import com.example.projectstages.ui.projects.viewmodel.ProjectsViewModel
import com.example.projectstages.ui.projects.viewmodel.ProjectsViewModelImpl
import com.example.projectstages.ui.taskslist.TasksListFragment
import com.example.projectstages.ui.test.TestFragment
import com.example.projectstages.utils.AdapterItemDecorator
import com.example.projectstages.utils.SpinnerAdapterWithImage

class ProjectsFragment(
    layoutId: Int = R.layout.fragment_projects
) : BaseFragment<FragmentProjectsBinding>(layoutId, FragmentProjectsBinding::inflate) {

    private lateinit var projectsViewModel: ProjectsViewModel
    private lateinit var projectsAdapter: ProjectsAdapter

    private val stateObserver = Observer<ProjectsViewModelImpl.ViewState> {
        binding.apply {
            progressBar.isVisible = it.progressBarVisibility

            recyclerView.isVisible = it.projectsAdapterVisibility
            projectsAdapter.setList(it.projects)
//            Log.d("ProjectsDebug", "State: $it")
            toolbar.toolbar.apply {
                title = it.title
                subtitle = it.subtitle
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val projectsInteractor = ProjectsInteractor(requireContext().appComponent.projectDao)
        val title = requireContext().resources.getString(R.string.app_name)
        val subtitle = "Список проектов"
        val projectsFactory = ProjectsFactory(title, subtitle, projectsInteractor)
        projectsViewModel = ViewModelProviders
            .of(this, projectsFactory)
            .get(ProjectsViewModelImpl::class.java)
        projectsViewModel.onActivityCreated(savedInstanceState == null)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            projectsAdapter = ProjectsAdapter(projectsViewModel)
            adapter = projectsAdapter
            val margin = requireContext().resources.getDimension(R.dimen.margin_adapter).toInt()
            addItemDecoration(AdapterItemDecorator(margin))
        }

        observeViewState(projectsViewModel.stateLiveData, stateObserver)

        binding.toolbar.addQuestionMenuButton.setOnClickListener {
            //В VM Callback
            showAddProjectDialog()
        }

        binding.toolbar.addQuestionMenuButton.setOnLongClickListener {
            goToTest()
            return@setOnLongClickListener true
        }

        projectsViewModel._navigationEvents.observe(viewLifecycleOwner, {
            when (it) {
                is ProjectsNavigationEvents.SuccessAddDialog
                -> showSimpleDialog("Категория добавлена", "Категория успешно добавлена")

                is ProjectsNavigationEvents.FailureAddDialog
                -> showErrorDialog("Ошибка добавления категории", "Категория не добавлена, укажите уникальное название")

                is ProjectsNavigationEvents.GoToProjectDetails
//                -> goToProjectDetails(it.projectID)
                -> showGoToTasksDialog(it.projectID)
            }
        })
    }

    private fun showSimpleDialog(title: String, message: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
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
//            .setTitle(title)
//            .setMessage(message)
            .setView(mainVerticalLayout)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun showAddProjectDialog() {
        val messageWithTitle = buildSpannedString {
            bold {
                append("Добавление категории\n\n")
            }
            italic {
                append("Введите новое уникальное название проекта")
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
            hint = "Название проекта"
            layoutParams = marginParams
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        val addButton = Button(requireContext()).apply {
            text = "Добавить"
            layoutParams = marginParams
        }

        val cancelButton = Button(requireContext()).apply {
            text = "Отмена"
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
            projectsViewModel.onAddButtonClicked(
                projectNameEditText.text.toString(),
                spinner.selectedItemPosition
            )
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun goToTasks(id: Long) {
        findNavController().navigate(
            R.id.action_projectsFragment_to_tasksFragment,
            TasksListFragment.getBundle(id)
        )
    }

    private fun goToTest() {
        findNavController().navigate(
            R.id.action_projectsFragment_to_testFragment,
            TestFragment.newInstance()
        )
    }

    private fun showGoToTasksDialog(id: Long) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Какой экран открыть?")
            .setPositiveButton("TasksList") { dialog, _ ->
                goToTasks(id)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}