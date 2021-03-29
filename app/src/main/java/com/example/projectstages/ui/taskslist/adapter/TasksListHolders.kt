package com.example.projectstages.ui.taskslist.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.projectstages.databinding.ItemTasksCompletedBinding
import com.example.projectstages.databinding.ItemTasksProgressBinding
import com.example.projectstages.databinding.ItemTasksThoughtBinding

class TasksListHolders {

    abstract class BaseHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        abstract val descriptionTextView: TextView

        abstract val dateTextView: TextView
//        abstract val deleteButton: ImageView

        fun bind(description: String, date: String) {
            descriptionTextView.text = description
            dateTextView.text = date
        }
    }

    class CompletedType(binding: ItemTasksCompletedBinding) : BaseHolder(binding) {

        override val descriptionTextView: TextView = binding.projectNameTextView

        override val dateTextView: TextView = binding.dateTextView

        //        override val deleteButton: ImageView = binding.deleteImageView

    }

    class ProgressType(binding: ItemTasksProgressBinding) : BaseHolder(binding) {

        override val descriptionTextView: TextView = binding.projectNameTextView

        override val dateTextView: TextView = binding.dateTextView
//        override val deleteButton: ImageView = binding.deleteImageView

    }

    class ThoughtType(binding: ItemTasksThoughtBinding) : BaseHolder(binding) {

        override val descriptionTextView: TextView = binding.projectNameTextView

        override val dateTextView: TextView = binding.dateTextView
//        override val deleteButton: ImageView = binding.deleteImageView

    }
}