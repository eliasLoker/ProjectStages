package com.example.projectstages.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.projectstages.R
import com.example.projectstages.databinding.ActivityMainBinding
import com.example.projectstages.ui.projects.ProjectsNavigationListener
import com.example.projectstages.ui.task.TaskFragment
import com.example.projectstages.ui.taskslist.TasksListFragment
import com.example.projectstages.ui.taskslist.TasksNavigationListener

class MainActivity : AppCompatActivity(), ProjectsNavigationListener, TasksNavigationListener {

    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            changeToolbar(destination.id)
        }
    }

    private fun changeToolbar(destinationID: Int) {
        binding.toolbar.apply {
            when (destinationID) {
                R.id.projectsFragment -> {
                    projectNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.pale_less_green))
                    tasksNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    taskNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                }
                R.id.tasksFragment -> {
                    projectNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    tasksNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.pale_less_green))
                    taskNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                }
                else -> {
                    projectNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    tasksNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    taskNameTextView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.pale_less_green))
                }
            }
        }
    }

    override fun goToTaskFromProjects(projectID: Long) {
        val bundle = TasksListFragment.getBundle(projectID)
        navController.navigate(R.id.action_projectsFragment_to_tasksFragment, bundle)
    }

    override fun goToTask(taskID: Long) {
        val bundle = TaskFragment.getBundleForEditTask(taskID)
        navController.navigate(R.id.action_tasksFragment_to_taskFragment, bundle)
    }

    override fun goToAddTask(projectID: Long) {
        val bundle = TaskFragment.getBundleForCreateTask(projectID)
        navController.navigate(R.id.action_tasksFragment_to_taskFragment, bundle)
    }
}