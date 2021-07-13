package com.example.projectstages.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFactory : ViewModelProvider.NewInstanceFactory() {

    abstract override fun <T : ViewModel?> create(modelClass: Class<T>): T
}