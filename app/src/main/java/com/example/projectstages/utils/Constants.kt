package com.example.projectstages.utils

import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {
        const val YELLOW_TYPE = 0
        const val RED_TYPE = 1
        const val GREEN_TYPE = 2
        const val BLUE_TYPE = 3
        const val PINK_TYPE = 4
        const val BLACK_TYPE = 5

        val userFormatProjects = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val userFormatTasks = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    }

    enum class TaskStates(val stateID: Int) {
        COMPLETED(0),
        IN_PROGRESS(1),
        IN_THOUGHT(2)
    }

    //TODO("Додумать. Решение с enum-ами не нравится")
    enum class TaskTitleType {
        ADD, EDIT
    }

    enum class EmptyList {
        EMPTY, ERROR
    }

    enum class FailureType {
        EMPTY_LIST,
        ERROR
    }
}

