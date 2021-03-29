package com.example.projectstages.ui.projects.adapter

import android.util.Log
import android.view.ViewGroup
import com.example.projectstages.base.BaseAdapter
import com.example.projectstages.databinding.*
import com.example.projectstages.ui.projects.model.Project
import com.example.projectstages.utils.Constants

class ProjectsAdapter(
    private val listener: ProjectsAdapterListener
) : BaseAdapter<ProjectsHolders.BaseHolder>() {

    private var projects = listOf<Project>()

    fun setList(newProjects: List<Project>) {
        projects = newProjects
        notifyDataSetChanged()
    }

    /*
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsHolders.BaseHolder {
        return when(viewType) {
            Constants.RED_TYPE -> ProjectsHolders.RedType(parent.inflateBinding(ItemProjectsRedBinding::inflate))
            Constants.GREEN_TYPE -> ProjectsHolders.GreenType(parent.inflateBinding(ItemProjectsGreenBinding::inflate))
            Constants.BLUE_TYPE -> ProjectsHolders.BlueType(parent.inflateBinding(ItemProjectsBlueBinding::inflate))
            Constants.PINK_TYPE -> ProjectsHolders.PinkType(parent.inflateBinding(ItemProjectsPinkBinding::inflate))
            Constants.BLACK_TYPE -> ProjectsHolders.BlackType(parent.inflateBinding(ItemProjectsBlackBinding::inflate))
            else ->ProjectsHolders.YellowType(parent.inflateBinding(ItemProjectsYellowBinding::inflate))
        }
    }
    */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsHolders.BaseHolder {
        return ProjectsHolders.YellowType(parent.inflateBinding(ItemProjectsYellowBinding::inflate))
    }

    override fun onBindViewHolder(holder: ProjectsHolders.BaseHolder, position: Int) {
        Log.d("DebugAdapter", "$position ${projects[position].countTasksByState[2]}")
        holder.bind(
            projectName = projects[position].name,
            update = projects[position].updateDate,
            completedTasks = "${projects[position].countTasksByState[0]}",
            progressTasks = "${projects[position].countTasksByState[1]}",
            thoughtTasks = "${projects[position].countTasksByState[2]}"
        )
        holder.itemView.setOnClickListener {
            listener.onItemClicked(projects[position].id)
        }
    }

    override fun getItemCount() = projects.size

    override fun getItemViewType(position: Int) = projects[position].type
}