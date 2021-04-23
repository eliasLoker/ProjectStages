package com.example.projectstages.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.example.projectstages.base.viewmodel.*

private typealias FragmentViewBindingInflater2<VB> = (
    inflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean
) -> VB

abstract class BaseFragment<
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

    protected val viewEffectObserver = Observer<BaseViewEffect> {
        processViewEffect(it)
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun processViewEffect(viewEffect: BaseViewEffect)
}

fun <T : BaseViewState, U> LifecycleOwner.observeViewState(liveDate: LiveData<T>, observer: Observer<U>) {
    liveDate.observe(this, observer as Observer<T>)
    //TODO("Додумать и переписать реализацию с дженериками")
}

fun <T> LifecycleOwner.observeViewState2(liveData: LiveData<T>, observer: Observer<T>) {
    liveData.observe(this, observer)
}

fun <T : BaseViewEffect, U> LifecycleOwner.observeViewEffect(liveDate: LiveData<T>, observer: Observer<U>) {
    liveDate.observe(this, observer as Observer<T>)
    //TODO("Додумать и переписать реализацию с дженериками")
}