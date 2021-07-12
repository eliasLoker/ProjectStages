package com.example.projectstages.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.projectstages.base.viewmodel.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

private typealias FragmentViewBindingInflater<VB> = (
    inflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean
) -> VB

abstract class BaseFragment<
        VB : ViewBinding,
        ViewState : BaseViewState?,
        ViewEffect: BaseViewEffect?
        >(
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
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun updateViewState(viewState: ViewState)

    abstract fun showSingleEvent(viewEffect: ViewEffect)

    //TODO("Подумать над вызовом этих методов автоматически")
    fun onEachViewState(viewStateFlow: Flow<ViewState?>) {
        viewStateFlow.onEach {
            val viewStateNotNull = it ?: return@onEach
            updateViewState(viewStateNotNull)
        }.launchWhenStartedWithCollect(this.lifecycleScope)
    }

    fun onEachViewEffect(viewEffectFlow: Flow<ViewEffect?>) {
        viewEffectFlow.onEach {
            val viewEffectNotNull = it ?: return@onEach
            showSingleEvent(viewEffectNotNull)
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

fun Fragment.getStringExt(@StringRes resId: Int) : String {
    return this.requireContext().resources.getString(resId)
}

fun Fragment.getStringArrayExt(resId: Int) : Array<String> = this.requireContext().resources.getStringArray(resId)

fun Fragment.getLongFromBundleExt(key: String) : Long = arguments?.getLong(key) ?: 0L

fun Fragment.getBooleanFromBundleExt(key: String) : Boolean = arguments?.getBoolean(key) ?: false