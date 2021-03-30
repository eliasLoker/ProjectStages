package com.example.projectstages.ui.task

sealed class TaskEvents {

    object SuccessAdd : TaskEvents()

    object FailureAdd : TaskEvents()
}