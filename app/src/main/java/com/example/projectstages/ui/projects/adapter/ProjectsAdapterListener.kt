package com.example.projectstages.ui.projects.adapter

interface ProjectsAdapterListener {

    fun onItemClicked(position: Int)

    fun onPopupEditClicked(position: Int)

    fun onPopupDeleteClicked(position: Int)
}