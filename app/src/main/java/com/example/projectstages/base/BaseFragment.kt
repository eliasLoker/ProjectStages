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

private typealias FragmentViewBindingInflater<VB> = (
    inflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean
) -> VB

abstract class BaseFragment<VB : ViewBinding>(
    @LayoutRes
    private val layoutID: Int,
    private val bindingInflater: FragmentViewBindingInflater<VB>
) : Fragment(layoutID) {

    private var _binding: VB? = null

    protected val binding get() = _binding!!

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun <T> LifecycleOwner.observe(liveDate: LiveData<T>, observer: Observer<T>) {
    liveDate.observe(this, observer)
}

//fun <T : BaseViewState, U> LifecycleOwner.observeViewState(liveDate: LiveData<T>, observer: Observer<U>) {
//    liveDate.observe(this, observer as Observer<T>)
//}

//fun LifecycleOwner.observeViewState(liveDate: LiveData<out BaseViewState>, observer: Observer<in BaseViewState>) {
//    liveDate.observe(this, observer)
//}

//fun <T> LifecycleOwner.observeViewState(liveDate: LiveData<T>, observer: Observer<T>) where T : BaseViewState{
fun <T : BaseViewState, U> LifecycleOwner.observeViewState(liveDate: LiveData<T>, observer: Observer<U>) {
    liveDate.observe(this, observer as Observer<T>)
    //TODO("Додумать и переписать реализацию с дженериками")
}