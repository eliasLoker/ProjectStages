package com.example.projectstages.ui.taskslist.adapter

import android.view.ViewGroup
import com.example.projectstages.base.BaseAdapter
import com.example.projectstages.databinding.ItemTasksCompletedBinding
import com.example.projectstages.databinding.ItemTasksProgressBinding
import com.example.projectstages.databinding.ItemTasksThoughtBinding
import com.example.projectstages.ui.taskslist.model.Task

class TasksListAdapter(
    private val listenerList: TasksListAdapterListener
) : BaseAdapter<TasksListHolders.BaseHolder>() {

    private var tasks = listOf<Task>()

    fun setList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> TasksListHolders.CompletedType(parent.inflateBinding(ItemTasksCompletedBinding::inflate))
        1 -> TasksListHolders.ProgressType(parent.inflateBinding(ItemTasksProgressBinding::inflate))
        2 -> TasksListHolders.ThoughtType(parent.inflateBinding(ItemTasksThoughtBinding::inflate))
        else -> TasksListHolders.CompletedType(parent.inflateBinding(ItemTasksCompletedBinding::inflate))
    }

    override fun onBindViewHolder(holder: TasksListHolders.BaseHolder, position: Int) {
        holder.bind(tasks[position].description, tasks[position].date)
//        holder.deleteButton.setOnClickListener {
//            listenerList.onDeleteButtonClicked(tasks[position].id)
//        }
//        holder.editButton.setOnClickListener {
//            listenerList.onEditButtonClicked(tasks[position].id)
//        }
        holder.itemView.setOnClickListener {
            listenerList.onTaskClicked(tasks[position].id)
        }
    }

    override fun getItemCount() = tasks.size

    override fun getItemViewType(position: Int) = tasks[position].state
}