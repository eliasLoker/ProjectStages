package com.example.projectstages.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

abstract class BaseViewModel<
        ViewState : BaseViewState,
        Action : BaseAction,
        ViewEffect: BaseViewEffect,
        ViewEvent : BaseViewEvent
        >(initialState : ViewState) : ViewModel() {

    private val viewEffectChannel = Channel<ViewEffect?>(Channel.BUFFERED)
    val viewEffect = viewEffectChannel.receiveAsFlow()

    private val stateMutableFlow = MutableStateFlow<ViewState?>(null)
    val stateFlow: StateFlow<ViewState?> = stateMutableFlow.asStateFlow()

    protected var state by Delegates.observable(initialState) { _, old, new ->
        stateMutableFlow.value = new
        if (new != old) {
            //TODO("Заглянуть сюда и еще подумать")
        }
    }

    protected fun sendAction(viewAction: Action) {
        state = onReduceState(viewAction)
    }

    protected fun sendViewEffect(viewEffect: ViewEffect) {
        viewModelScope.launch(Dispatchers.IO) {
            viewEffectChannel.send(viewEffect)
        }
    }

    protected abstract fun onReduceState(viewAction: Action) : ViewState

    abstract fun processViewEvent(viewEvent: ViewEvent)

}

