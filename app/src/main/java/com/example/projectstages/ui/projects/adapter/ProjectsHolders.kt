package com.example.projectstages.ui.projects.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.projectstages.databinding.*

class ProjectsHolders {

    abstract class BaseHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        abstract val folderImageView: ImageView

        abstract val projectNameTextView: TextView

        abstract val updateTextView: TextView

        abstract val completedTextView: TextView

        abstract val progressTextView: TextView

        abstract val thoughtTextView: TextView

        fun bind(
            projectName: String,
            update: String,
            completedTasks: String,
            progressTasks: String,
            thoughtTasks: String
        ) {
            projectNameTextView.text = projectName
            updateTextView.text = update
            completedTextView.text = completedTasks
            progressTextView.text = progressTasks
            thoughtTextView.text = thoughtTasks
        }
    }


    class YellowType(binding: ItemProjectsDefaultBinding) : BaseHolder(binding) {

        override val folderImageView: ImageView = binding.folderImageView

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val completedTextView: TextView = binding.completedSizeTextView

        override val progressTextView: TextView = binding.progressSizeTextView

        override val thoughtTextView: TextView = binding.thoughtSizeTextView
    }

    /*
    class RedType(binding: ItemProjectsRedBinding) : BaseHolder(binding) {

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val completedTextView: TextView = binding.completedSizeTextView

        override val progressTextView: TextView = binding.progressSizeTextView

        override val thoughtTextView: TextView = binding.thoughtSizeTextView
    }

    class GreenType(binding: ItemProjectsGreenBinding) : BaseHolder(binding) {

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val completedTextView: TextView = binding.completedSizeTextView

        override val progressTextView: TextView = binding.progressSizeTextView

        override val thoughtTextView: TextView = binding.thoughtSizeTextView
    }

    class BlueType(binding: ItemProjectsBlueBinding) : BaseHolder(binding) {

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val completedTextView: TextView = binding.completedSizeTextView

        override val progressTextView: TextView = binding.progressSizeTextView

        override val thoughtTextView: TextView = binding.thoughtSizeTextView
    }

    class PinkType(binding: ItemProjectsPinkBinding) : BaseHolder(binding) {

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val completedTextView: TextView = binding.completedSizeTextView

        override val progressTextView: TextView = binding.progressSizeTextView

        override val thoughtTextView: TextView = binding.thoughtSizeTextView
    }

    class BlackType(binding: ItemProjectsBlackBinding) : BaseHolder(binding) {

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val completedTextView: TextView = binding.completedSizeTextView

        override val progressTextView: TextView = binding.progressSizeTextView

        override val thoughtTextView: TextView = binding.thoughtSizeTextView
    }
    */
}

