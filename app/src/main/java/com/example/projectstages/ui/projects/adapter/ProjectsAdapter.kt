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

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsHolders.BaseHolder {
//        return ProjectsHolders.YellowType(parent.inflateBinding(ItemProjectsYellowBinding::inflate))
//    }

    override fun onBindViewHolder(holder: ProjectsHolders.BaseHolder, position: Int) {
        Log.d("DebugAdapter", "$position ${projects[position].countTasksByState[2]}")

        val view = holder.colorsProgressBar
        view.isVisible = projects[position].countTasksByState[0] + projects[position].countTasksByState[1] + projects[position].countTasksByState[2] != 0
        //TODO(Show if min two task types)
        Log.d("Projects", "Summ: ${projects[position].countTasksByState[0] + projects[position].countTasksByState[1] + projects[position].countTasksByState[2]}")

        Log.d("Projects", "Item 1: ${projects[position].countTasksByState[0]}")

        Log.d("Projects", "Item 2: ${projects[position].countTasksByState[1]}")
        Log.d("Projects", "Item 3: ${projects[position].countTasksByState[2]}")
        //START
        val progressItemList = ArrayList<ProgressItem>()

        val sum = projects[position].countTasksByState[0] + projects[position].countTasksByState[1] + projects[position].countTasksByState[2]
        val mProgressItem = ProgressItem(R.color.bright_green, projects[position].countTasksByState[0].toFloat())
        Log.d("Projects", "Item 1 det: ${sum.toFloat() / 100 * projects[position].countTasksByState[0].toFloat()}")
//        val mProgressItem = ProgressItem(R.color.bright_green, sum.toFloat() / 100 * projects[position].countTasksByState[0].toFloat())
        progressItemList.add(mProgressItem)

        val mProgressItem2 = ProgressItem(R.color.bright_red, projects[position].countTasksByState[1].toFloat())
//        val mProgressItem2 = ProgressItem(R.color.bright_red, sum.toFloat() / 100 * projects[position].countTasksByState[1].toFloat())
        progressItemList.add(mProgressItem2)

        val mProgressItem3 = ProgressItem(R.color.bright_yellow, projects[position].countTasksByState[2].toFloat())
//        val mProgressItem3 = ProgressItem(R.color.bright_yellow, sum.toFloat() / 100 * projects[position].countTasksByState[2].toFloat())
        progressItemList.add(mProgressItem3)

        view.initData(progressItemList)
        view.invalidate()
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