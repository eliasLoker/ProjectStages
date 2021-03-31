package com.example.projectstages.ui.projects.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.projectstages.ColorsProgressBar
import com.example.projectstages.databinding.*

class ProjectsHolders {

    abstract class BaseHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        abstract val folderImageView: ImageView

        abstract val projectNameTextView: TextView

        abstract val updateTextView: TextView

        abstract val colorsProgressBar: ColorsProgressBar

        fun bind(
            projectName: String,
            update: String
        ) {
            projectNameTextView.text = projectName
            updateTextView.text = update
        }
    }


    class YellowType(binding: ItemProjectsDefaultBinding) : BaseHolder(binding) {

        override val folderImageView: ImageView = binding.folderImageView

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val colorsProgressBar: ColorsProgressBar = binding.customProgressBar
    }

}

