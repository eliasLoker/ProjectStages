package com.example.projectstages.ui.tasks.adapter

import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.projectstages.R
import com.example.projectstages.base.BaseAdapter
import com.example.projectstages.databinding.ItemTasksBinding
import com.example.projectstages.ui.tasks.model.Task

class TasksAdapter(
    private val listener: TasksAdapterListener
) : BaseAdapter<TasksHolders.BaseHolder>() {

    private var tasks = listOf<Task>()

    fun setList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TasksHolders.BaseHolder {
        val backgroundColor = TasksHolders.getBackgroundColor(viewType)
        val viewColor = TasksHolders.getViewColor(viewType)
        return TasksHolders.DefaultType(parent.inflateBinding(ItemTasksBinding::inflate))
            .apply {
            verticalView.setBackgroundColor(ContextCompat.getColor(parent.context, viewColor))
            itemView.rootView.setBackgroundColor(ContextCompat.getColor(parent.context, backgroundColor))
        }
    }

    override fun onBindViewHolder(holder: TasksHolders.BaseHolder, position: Int) {
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recycler_anim)
        holder.bind(tasks[position].description, tasks[position].date)
        holder.itemView.setOnClickListener {
            listener.onTaskClicked(tasks[position].id)
        }
    }

    override fun getItemCount() = tasks.size

    override fun getItemViewType(position: Int) = tasks[position].state
}