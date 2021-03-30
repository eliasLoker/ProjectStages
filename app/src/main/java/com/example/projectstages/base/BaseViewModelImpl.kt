package com.example.projectstages.base

import androidx.lifecycle.LiveData

interface BaseViewModelImpl {

    /*
    Не нравится дублирование stateLiveData и onActivityCreated из
    абстрактного класса BaseViewModel, но реализацию правильно делать через интерефейсы.
    В будущем продумать, как избавиться. Кроме того, продумать названия интерфейсов и классов
    их реализации
    TODO("Проблема дублирования")
    */
    val stateLiveData: LiveData<out BaseViewState>

    fun onViewCreated(isFirstLoading: Boolean)
}