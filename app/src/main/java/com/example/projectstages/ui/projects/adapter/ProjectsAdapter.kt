package com.example.projectstages.ui.projects.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.projectstages.ProgressItem
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
//        val backgroundColor = R.color.white
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
        /*
        view.itemView.apply {
            setBackgroundColor(ContextCompat.getColor(parent.context, backgroundColor))
        }
        */
        view.folderImageView.setImageDrawable(ContextCompat.getDrawable(parent.context, folderView))
        view.itemView.rootView.apply {
            setBackgroundColor(ContextCompat.getColor(parent.context, backgroundColor))
        }
        return view
    }

    override fun onBindViewHolder(holder: ProjectsHolders.BaseHolder, position: Int) {
        Log.d("DebugAdapter", "$position ${projects[position].countTasksByState[2]}")

        val colorsProgressBar = holder.colorsProgressBar
        colorsProgressBar.isVisible = projects[position].countTasksByState[0] + projects[position].countTasksByState[1] + projects[position].countTasksByState[2] != 0

        //START
        //TODO("Вероятно, в будущем нужно убрать эту расширяемость, тк не нужна и захардкодить три цвета внутри ColorsProgressBar")
        val progressItemList = ArrayList<ProgressItem>()

        val mProgressItem = ProgressItem(R.color.bright_green, projects[position].countTasksByState[0].toFloat())
        progressItemList.add(mProgressItem)

        val mProgressItem2 = ProgressItem(R.color.bright_red, projects[position].countTasksByState[1].toFloat())
        progressItemList.add(mProgressItem2)

        val mProgressItem3 = ProgressItem(R.color.bright_yellow, projects[position].countTasksByState[2].toFloat())
        progressItemList.add(mProgressItem3)

        colorsProgressBar.initData(progressItemList)
        //END

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