package com.example.projectstages.ui.tasks.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.projectstages.R
import com.example.projectstages.databinding.ItemTaskBinding
import com.example.projectstages.utils.Constants

class TasksHolders {

    abstract class BaseHolder(
        binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        abstract val descriptionTextView: TextView

        abstract val verticalView: View

        abstract val dateTextView: TextView

        fun bind(description: String, date: String) {
            descriptionTextView.text = description
            dateTextView.text = date
        }
    }

    class DefaultType(binding: ItemTaskBinding) : BaseHolder(binding) {

        override val descriptionTextView: TextView = binding.taskTextView

        override val dateTextView: TextView = binding.updatedDateTextView

        override val verticalView: View = binding.verticalView
    }

    companion object {

        fun getBackgroundColor(viewType: Int) = when(viewType) {
            Constants.TaskStates.COMPLETED.stateID -> R.color.pale_green
            Constants.TaskStates.IN_PROGRESS.stateID -> R.color.pale_red
            else -> R.color.pale_yellow
        }

        fun getViewColor(viewType: Int) = when(viewType) {
            Constants.TaskStates.COMPLETED.stateID -> R.color.green_completed
            Constants.TaskStates.IN_PROGRESS.stateID -> R.color.red
            else -> R.color.yellow
        }
    }
}