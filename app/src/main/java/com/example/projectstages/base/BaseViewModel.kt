package com.example.projectstages.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.properties.Delegates

abstract class BaseViewModel<ViewState : BaseViewState, ViewAction : BaseAction>(initialState : ViewState) : ViewModel() {

    private val stateMutableLiveData = MutableLiveData<ViewState>()
    val stateLiveData = stateMutableLiveData.asLiveData()

    protected var state by Delegates.observable(initialState) { _, old, new ->
        stateMutableLiveData.value = new

        if (new != old) {
//            addStateTransition(old, new)
//            logLast()
        }
    }

    protected fun sendAction(viewAction: ViewAction) {
        state = onReduceState(viewAction)
    }

//    fun onActivityCreated() {
//        onLoadData()
//    }

    abstract fun onActivityCreated(isFirstLoading: Boolean)

    protected abstract fun onReduceState(viewAction: ViewAction) : ViewState

}

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>