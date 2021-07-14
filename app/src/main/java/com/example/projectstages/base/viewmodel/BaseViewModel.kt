package com.example.projectstages.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    /*
      private val _viewEffect: SingleLiveEvent<ViewEffect> = SingleLiveEvent()
      val viewEffect = _viewEffect
      */
    /*
    https://stackoverflow.com/questions/59002902/singleliveevent-with-buffer-with-coroutines
    Подумать над оптимизацией этого решения доставки единичных Event'ов

    Альтернативный вариант замены SingleLiveEvent на Flow для LiveData:
    https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055

    Вариант BroadcastChannel<ViewEffect?>(Channel.BUFFERED) копил все таки event'ы, даже с
    единичным capacity.
    //TODO("Разобраться детальнее")
    https://github.com/Kotlin/kotlinx.coroutines/issues/2005
    */
    private val viewEffectChannel = Channel<ViewEffect?>(Channel.BUFFERED)
    val viewEffect = viewEffectChannel.receiveAsFlow()

    /*
    private val stateMutableLiveData = MutableLiveData<ViewState>()
    val stateLiveData = stateMutableLiveData.asLiveData()
    */
    /*
    //Last variant flow
    private val stateMutableFlow = MutableStateFlow<ViewState?>(null)
    val stateFlow: StateFlow<ViewState?> = stateMutableFlow.asStateFlow()
    */
    //TODO("Временно вернул LiveData и она работает корректноб но все равно перейти на Flow")
    private val stateMutableLiveData = MutableLiveData<ViewState>()
    val stateLiveData = stateMutableLiveData.asLiveData()

    protected var state by Delegates.observable(initialState) { _, old, new ->
//        stateMutableFlow.value = new //Last variant flow
        stateMutableLiveData.postValue(new)
        //TODO("Заглянуть сюда и еще подумать")
        if (new != old) {
//            addStateTransition(old, new)
//            logLast()
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

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

