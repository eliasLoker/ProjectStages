package com.example.projectstages.ui.projects.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.projectstages.R
import com.example.projectstages.customview.ColorsProgressBar
import com.example.projectstages.databinding.ItemProjectsBinding
import com.example.projectstages.utils.Constants

class ProjectsHolders {

    abstract class BaseHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        abstract val folderImageView: ImageView

        abstract val projectNameTextView: TextView

        abstract val updateTextView: TextView

        abstract val colorsProgressBar: ColorsProgressBar

        abstract val menuImageView: ImageView

        fun bind(
            projectName: String,
            update: String
        ) {
            projectNameTextView.text = projectName
            updateTextView.text = update
        }
    }


    class DefaultType(binding: ItemProjectsBinding) : BaseHolder(binding) {

        override val folderImageView: ImageView = binding.folderImageView

        override val projectNameTextView = binding.projectNameTextView

        override val updateTextView: TextView = binding.updateTextView

        override val colorsProgressBar: ColorsProgressBar = binding.customProgressBar

        override val menuImageView: ImageView = binding.menuImageView
    }

    companion object {

        fun getBackgroundColor(viewType: Int) = when(viewType) {
            Constants.RED_TYPE -> R.color.pale_red
            Constants.GREEN_TYPE -> R.color.pale_green
            Constants.BLUE_TYPE -> R.color.pale_blue
            Constants.PINK_TYPE -> R.color.pale_pink
            Constants.BLACK_TYPE -> R.color.pale_grey
            else -> R.color.pale_yellow
        }

        fun getFolderView(viewType: Int) = when(viewType) {
            Constants.RED_TYPE -> R.drawable.ic_file_red
            Constants.GREEN_TYPE -> R.drawable.ic_file_green
            Constants.BLUE_TYPE -> R.drawable.ic_file_blue
            Constants.PINK_TYPE -> R.drawable.ic_file_pink
            Constants.BLACK_TYPE -> R.drawable.ic_file_black
            else -> R.drawable.ic_file_yellow
        }

        fun getColorForEmptyTask(viewType: Int) = when(viewType) {
            Constants.RED_TYPE -> R.color.color_red_folder
            Constants.GREEN_TYPE -> R.color.color_green_folder
            Constants.BLUE_TYPE -> R.color.color_blue_folder
            Constants.PINK_TYPE -> R.color.color_pink_folder
            Constants.BLACK_TYPE -> R.color.color_black_folder
            else -> R.color.color_yellow_folder
        }
    }

}

