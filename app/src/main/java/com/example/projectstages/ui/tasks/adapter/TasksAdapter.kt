package com.example.projectstages.ui.tasks.adapter

import android.graphics.Color
import android.view.View.inflate
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.projectstages.R
import com.example.projectstages.base.BaseAdapter
import com.example.projectstages.databinding.ItemTaskBinding
import com.example.projectstages.databinding.ItemTasksCompletedBinding
import com.example.projectstages.databinding.ItemTasksProgressBinding
import com.example.projectstages.databinding.ItemTasksThoughtBinding
import com.example.projectstages.ui.tasks.model.Task
import com.example.projectstages.utils.Constants

class TasksAdapter(
    private val listener: TasksAdapterListener
) : BaseAdapter<TasksHolders.BaseHolder>() {

    private var tasks = listOf<Task>()

    fun setList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TasksHolders.BaseHolder {
        val backgroundColor = when(viewType) {
            Constants.TaskStates.COMPLETED.stateID -> R.color.pale_green
            Constants.TaskStates.IN_PROGRESS.stateID -> R.color.pale_red
            else -> R.color.pale_yellow
        }
        val viewColor = when(viewType) {
            Constants.TaskStates.COMPLETED.stateID -> R.color.green_completed
            Constants.TaskStates.IN_PROGRESS.stateID -> R.color.red
            else -> R.color.yellow
        }
        val view = TasksHolders.DefaultType(parent.inflateBinding(ItemTaskBinding::inflate))
        view.verticalView.setBackgroundColor(ContextCompat.getColor(parent.context, viewColor))
//        view.verticalView.setBackgroundColor(viewColor)
        view.itemView.rootView.setBackgroundColor(ContextCompat.getColor(parent.context, backgroundColor))
        return view
    }

    override fun onBindViewHolder(holder: TasksHolders.BaseHolder, position: Int) {
        holder.bind(tasks[position].description, tasks[position].date)
//        holder.deleteButton.setOnClickListener {
//            listenerList.onDeleteButtonClicked(tasks[position].id)
//        }
//        holder.editButton.setOnClickListener {
//            listenerList.onEditButtonClicked(tasks[position].id)
//        }
        holder.itemView.setOnClickListener {
            listener.onTaskClicked(tasks[position].id)
        }
    }

    override fun getItemCount() = tasks.size

    override fun getItemViewType(position: Int) = tasks[position].state
}