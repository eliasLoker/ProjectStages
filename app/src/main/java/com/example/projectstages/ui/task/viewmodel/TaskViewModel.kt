package com.example.projectstages.ui.task.viewmodel

import androidx.lifecycle.LiveData
import com.example.projectstages.base.BaseViewModelImpl
import com.example.projectstages.ui.task.TaskEvents

interface TaskViewModel : BaseViewModelImpl {

    val taskEvents: LiveData<TaskEvents>

    fun onSaveButtonClicked()

    fun onTextChangedDescription(text: String)

    fun onItemSelectedStateSpinner(position: Int)
}