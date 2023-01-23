package com.example.projectstages.base.viewmodel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class BaseInteractor {

    val defaultInteractorDispatcher: CoroutineDispatcher = Dispatchers.IO
}