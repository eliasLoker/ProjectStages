package com.example.projectstages.ui.projects.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.projectstages.R
import com.example.projectstages.base.BaseAdapter
import com.example.projectstages.customview.ProgressItem
import com.example.projectstages.databinding.ItemProjectsDefaultBinding
import com.example.projectstages.ui.projects.model.Project


class ProjectsAdapter(
    private val listener: ProjectsAdapterListener
) : BaseAdapter<ProjectsHolders.BaseHolder>() {

    private var projects = listOf<Project>()

    private lateinit var context: Context

    fun setList(newProjects: List<Project>) {
        projects = newProjects
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsHolders.BaseHolder {
        context = parent.context
        val backgroundColor = ProjectsHolders.getBackgroundColor(viewType)
        val folderView = ProjectsHolders.getFolderView(viewType)
        return ProjectsHolders.DefaultType(parent.inflateBinding(ItemProjectsDefaultBinding::inflate))
            .apply {
            folderImageView.setImageDrawable(ContextCompat.getDrawable(parent.context, folderView))
            itemView.rootView.setBackgroundColor(ContextCompat.getColor(parent.context, backgroundColor))
        }
    }

    override fun onBindViewHolder(holder: ProjectsHolders.BaseHolder, position: Int) {
        //TODO("Вероятно, в будущем нужно убрать эту расширяемость, тк излишняя и захардкодить три цвета внутри ColorsProgressBar")

        val colorsProgressBar = holder.colorsProgressBar

        val progressItemList = ArrayList<ProgressItem>().apply {
            when(projects[position].countTasksByState[0] + projects[position].countTasksByState[1] + projects[position].countTasksByState[2] != 0) {
                true -> {
                    add(ProgressItem(R.color.bright_green, projects[position].countTasksByState[0].toFloat()))
                    add(ProgressItem(R.color.bright_red, projects[position].countTasksByState[1].toFloat()))
                    add(ProgressItem(R.color.bright_yellow, projects[position].countTasksByState[2].toFloat()))
                }
                false -> {
                    add(ProgressItem(R.color.bright_blue, 1.0f))
                }
            }
        }
        colorsProgressBar.initData(progressItemList)

        holder.bind(
            projectName = projects[position].name,
            update = projects[position].updateDate
        )
        holder.itemView.setOnClickListener {
            listener.onItemClicked(position)
        }
        holder.menuImageView.setOnClickListener {
            val popup = PopupMenu(context, holder.menuImageView).apply {
                    inflate(R.menu.menu_projects_item)
                    setOnMenuItemClickListener {
                        when(it.itemId) {
                            R.id.menu_delete -> {
                                listener.onPopupDeleteClicked(position)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.menu_edit -> {
                                listener.onPopupEditClicked(position)
                                return@setOnMenuItemClickListener true
                            }
                            else -> return@setOnMenuItemClickListener false
                        }
                    }
                }
            popup.show()
        }
    }

    override fun getItemCount() = projects.size

    override fun getItemViewType(position: Int) = projects[position].type
}