package com.example.projectstages.ui.projects.adapter

interface ProjectsAdapterListener {

    fun onItemClicked(id: Long)

    fun onPopupEditClicked(position: Int)

    fun onPopupDeleteClicked(position: Int)
}