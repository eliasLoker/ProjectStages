package com.example.projectstages.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectstages.utils.SingleLiveEvent
import kotlin.properties.Delegates

abstract class BaseViewModel<
        ViewState : BaseViewState,
        Action : BaseAction,
        ViewEffect: BaseViewEffect,
        ViewEvent : BaseViewEvent
        >(initialState : ViewState) : ViewModel() {

    private val _viewEffect: SingleLiveEvent<ViewEffect> = SingleLiveEvent()
    val viewEffect = _viewEffect

    private val stateMutableLiveData = MutableLiveData<ViewState>()
    val stateLiveData = stateMutableLiveData.asLiveData()

    protected var state by Delegates.observable(initialState) { _, old, new ->
        stateMutableLiveData.value = new

        if (new != old) {
//            addStateTransition(old, new)
//            logLast()
        }
    }

    protected fun sendAction(viewAction: Action) {
        state = onReduceState(viewAction)
    }

//    fun onActivityCreated() {
//        onLoadData()
//    }

    abstract fun onViewCreated(isFirstLoading: Boolean)
    //TODO("Проверить, что везде коллбэк именно onViewCreated")

    protected abstract fun onReduceState(viewAction: Action) : ViewState

    abstract fun processViewEvent(viewEvent: ViewEvent)

    fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>
}

