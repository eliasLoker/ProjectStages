package com.example.projectstages.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.projectstages.base.viewmodel.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

private typealias FragmentViewBindingInflater2<VB> = (
    inflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean
) -> VB

abstract class BaseFragment2<
        VB : ViewBinding,
        ViewState : BaseViewState,
        Action : BaseAction,
        ViewEffect: BaseViewEffect,
        ViewEvent : BaseViewEvent,
        ViewModel : BaseViewModel<ViewState, Action, ViewEffect, ViewEvent>
        >(
    @LayoutRes
    private val layoutID: Int,
    private val bindingInflater: FragmentViewBindingInflater2<VB>
) : Fragment(layoutID) {

    private var _binding: VB? = null

    protected val binding get() = _binding!!

    abstract val viewModel : ViewModel

//    abstract var viewModelFactory: ViewModelProvider.Factory
//    abstract val viewModelClass: KClass<out BaseViewModel<ViewState, Action, ViewEffect, ViewEvent>>
//    protected lateinit var viewModel: BaseViewModel<ViewState, Action, ViewEffect, ViewEvent>

//    private fun getViewModels() : BaseViewModel<ViewState, Action, ViewEffect, ViewEvent> = ViewModelProvider(this, viewModelFactory)[viewModelClass.java]

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = getViewModels()
//        subscribeToViewState(viewModel.stateFlow)
//        subscribeToViewState(viewModel.stateFlow)
//        subscribeToViewEffect(viewModel.viewEffect)
        subscribeToViewState(viewModel.stateFlow)
        subscribeToViewEffect(viewModel.viewEffect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun updateViewState(viewState: ViewState)

    abstract fun showViewEffect(viewEffect: ViewEffect)

    private fun subscribeToViewState(viewStateFlow: Flow<ViewState?>) {
        viewStateFlow.onEach {
            val viewStateNotNull = it ?: return@onEach
            updateViewState(viewStateNotNull)
        }.launchWhenStartedWithCollect(this.lifecycleScope)
    }

    private fun subscribeToViewEffect(viewEffectFlow: Flow<ViewEffect?>) {
        viewEffectFlow.onEach {
            val viewEffectNotNull = it ?: return@onEach
            showViewEffect(viewEffectNotNull)
        }.launchWhenStartedWithCollect(this.lifecycleScope)
    }

    /*
        Thanks to Kirill Rozov :)
    */
    private fun <T> Flow<T>.launchWhenStartedWithCollect(lifecycleCoroutineScope: LifecycleCoroutineScope) {
        lifecycleCoroutineScope.launchWhenStarted {
            this@launchWhenStartedWithCollect.collect()
        }
    }
}