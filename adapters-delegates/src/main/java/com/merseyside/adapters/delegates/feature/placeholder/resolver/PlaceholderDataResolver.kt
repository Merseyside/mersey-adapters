package com.merseyside.adapters.delegates.feature.placeholder.resolver

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.doAsync
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.callback.OnModelListChangedCallback
import com.merseyside.adapters.delegates.simple.Delegate
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.utils.safeLet
import kotlinx.coroutines.job
import kotlin.coroutines.coroutineContext

abstract class PlaceholderDataResolver<Parent, ParentModel : VM<Parent>> :
    OnModelListChangedCallback, ILogger {

    private lateinit var modelList: ModelList<Parent, ParentModel>
    private lateinit var provider: PlaceholderProvider<out Parent, Parent>
    protected lateinit var adapter: CompositeAdapter<Parent, ParentModel>

    var isPlaceholderAdded = false
        private set

    fun setPlaceholderProvider(provider: PlaceholderProvider<out Parent, Parent>) {
        this.provider = provider
    }

    @Suppress("UNCHECKED_CAST")
    open fun initAdapter(adapter: CompositeAdapter<Parent, ParentModel>) {
        this.adapter = adapter
        modelList = adapter.adapterConfig.modelList
        safeLet(provider.placeholderDelegate) {
            adapter.delegatesManager.addDelegates(it as Delegate<Parent, ParentModel>)
        }

        adapter.addOnAttachToRecyclerViewListener(onAttachListener)
    }

    open fun onAdapterAttached(adapter: CompositeAdapter<Parent, out ParentModel>) {
        enableModelListCallback()
    }

    protected fun addPlaceholderAsync(position: Int = LAST_POSITION) {
        adapter.doAsync { addPlaceholder(position) }
    }

    protected suspend fun addPlaceholder(position: Int = LAST_POSITION) {
        if (!isPlaceholderAdded) {
            turnMutableState {
                if (position == LAST_POSITION) adapter.add(provider.placeholder)
                else adapter.add(position, provider.placeholder)
                isPlaceholderAdded = true
            }
        }
    }

    protected fun removePlaceholderAsync() {
        adapter.doAsync { removePlaceholder() }
    }

    protected suspend fun removePlaceholder() {
        if (isPlaceholderAdded) {
            turnMutableState {
                isPlaceholderAdded = false
                adapter.remove(provider.placeholder)
            }
        }
    }

    private val onAttachListener = object : OnAttachToRecyclerViewListener {
        override fun onAttached(
            recyclerView: RecyclerView,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ignored: IBaseAdapter<*, *>
        ) {
            onAdapterAttached(adapter)
        }

        override fun onDetached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>) {
            disableModelListCallback()
        }
    }

    private suspend fun turnMutableState(mutate: suspend () -> Unit) {
        disableModelListCallback()
        mutate()
        coroutineContext.job.invokeOnCompletion { enableModelListCallback() }
    }

    private fun enableModelListCallback() {
        modelList.addOnModelListChangedCallback(this)
    }

    private fun disableModelListCallback() {
        modelList.removeOnModelListChangedCallback(this)
    }

    protected fun isEmpty() = modelList.isEmpty()

    override val tag: String = "PlaceholderDataResolver"

    companion object {
        const val LAST_POSITION = -1
    }
}