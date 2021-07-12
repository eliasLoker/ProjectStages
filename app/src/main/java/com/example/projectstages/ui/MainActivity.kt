package com.example.projectstages.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.projectstages.R
import com.example.projectstages.ui.projects.ProjectsNavigationListener
import com.example.projectstages.ui.task.TaskFragment
import com.example.projectstages.ui.taskslist.TasksListFragment
import com.example.projectstages.ui.taskslist.TasksNavigationListener

class MainActivity : AppCompatActivity(), ProjectsNavigationListener, TasksNavigationListener {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
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