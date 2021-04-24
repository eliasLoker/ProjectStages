package com.example.projectstages.ui.projects.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.projectstages.customview.ProgressItem
import com.example.projectstages.R
import com.example.projectstages.base.BaseAdapter
import com.example.projectstages.databinding.ItemProjectsDefaultBinding
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsHolders.BaseHolder {
        val backgroundColor = when(viewType) {
            Constants.RED_TYPE -> R.color.pale_red
            Constants.GREEN_TYPE -> R.color.pale_green
            Constants.BLUE_TYPE -> R.color.pale_blue
            Constants.PINK_TYPE -> R.color.pale_pink
            Constants.BLACK_TYPE -> R.color.pale_grey
            else -> R.color.pale_yellow
        }
        val folderView = when(viewType) {
            Constants.RED_TYPE -> R.drawable.ic_file_red
            Constants.GREEN_TYPE -> R.drawable.ic_file_green
            Constants.BLUE_TYPE -> R.drawable.ic_file_blue
            Constants.PINK_TYPE -> R.drawable.ic_file_pink
            Constants.BLACK_TYPE -> R.drawable.ic_file_black
            else -> R.drawable.ic_file_yellow
        }
        val view = ProjectsHolders.YellowType(parent.inflateBinding(ItemProjectsDefaultBinding::inflate))
        view.folderImageView.setImageDrawable(ContextCompat.getDrawable(parent.context, folderView))
        view.itemView.rootView.setBackgroundColor(ContextCompat.getColor(parent.context, backgroundColor))
        return view
    }

    override fun onBindViewHolder(holder: ProjectsHolders.BaseHolder, position: Int) {
        //TODO("Вероятно, в будущем нужно убрать эту расширяемость, тк излишняя и захардкодить три цвета внутри ColorsProgressBar")
        val colorsProgressBar = holder.colorsProgressBar
        colorsProgressBar.isVisible = projects[position].countTasksByState[0] + projects[position].countTasksByState[1] + projects[position].countTasksByState[2] != 0
        val progressItemList = ArrayList<ProgressItem>().apply {
            add(ProgressItem(R.color.bright_green, projects[position].countTasksByState[0].toFloat()))
            add(ProgressItem(R.color.bright_red, projects[position].countTasksByState[1].toFloat()))
            add(ProgressItem(R.color.bright_yellow, projects[position].countTasksByState[2].toFloat()))
        }
        colorsProgressBar.initData(progressItemList)

        holder.bind(
            projectName = projects[position].name,
            update = projects[position].updateDate
        )
        holder.itemView.setOnClickListener {
            listener.onItemClicked(projects[position].id)
        }
    }

    override fun getItemCount() = projects.size

    override fun getItemViewType(position: Int) = projects[position].type
}