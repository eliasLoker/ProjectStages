package com.example.projectstages.ui.tasks.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.projectstages.databinding.ItemTaskBinding
import com.example.projectstages.databinding.ItemTasksCompletedBinding
import com.example.projectstages.databinding.ItemTasksProgressBinding
import com.example.projectstages.databinding.ItemTasksThoughtBinding

class TasksHolders {

    abstract class BaseHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        abstract val descriptionTextView: TextView

        abstract val verticalView: View

        abstract val dateTextView: TextView
//        abstract val deleteButton: ImageView

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
}