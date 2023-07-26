package com.merseyside.adapters.delegates.feature.placeholder.resolver

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.doAsync
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.ModelListCallback
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.utils.safeLet

abstract class PlaceholderDataResolver<Parent, ParentModel : VM<Parent>> :
    ModelListCallback<ParentModel>, ILogger {

    private lateinit var modelList: ModelList<Parent, ParentModel>
    private lateinit var provider: PlaceholderProvider<out Parent, Parent>
    protected lateinit var adapter: CompositeAdapter<Parent, ParentModel>

    var isPlaceholderAdded = false
        private set

    fun setPlaceholderProvider(provider: PlaceholderProvider<out Parent, Parent>) {
        this.provider = provider
    }

    open fun initAdapter(adapter: CompositeAdapter<Parent, ParentModel>) {
        this.adapter = adapter
        modelList = adapter.adapterConfig.modelList
        safeLet(provider.placeholderDelegate) {
            adapter.delegatesManager.addDelegates(it as DelegateAdapter<out Parent, Parent, out ParentModel>)
        }

        adapter.addOnAttachToRecyclerViewListener(onAttachListener)
    }

    open fun onAdapterAttached(adapter: CompositeAdapter<Parent, out ParentModel>) {
        enableModelListCallback()
    }

    /* Model list callbacks are optional */
    override suspend fun onInsert(models: List<ParentModel>, count: Int) {}

    override suspend fun onInserted(models: List<ParentModel>, position: Int, count: Int) {}

    override suspend fun onRemove(models: List<ParentModel>, count: Int) {}

    override suspend fun onRemoved(models: List<ParentModel>, position: Int, count: Int) {}

    override suspend fun onChanged(
        model: ParentModel,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
    }

    override suspend fun onMoved(fromPosition: Int, toPosition: Int) {}

    override suspend fun onCleared() {}

    protected fun addPlaceholderAsync(position: Int = LAST_POSITION) {
        adapter.doAsync { addPlaceholder(position) }
    }

    protected suspend fun addPlaceholder(position: Int = LAST_POSITION) =
        turnMutableState {
            isPlaceholderAdded = true
            if (position == LAST_POSITION) adapter.add(provider.placeholder)
            else adapter.add(position, provider.placeholder)
        }

    protected fun removePlaceholderAsync() {
        adapter.doAsync { removePlaceholder() }
    }

    protected suspend fun removePlaceholder() = turnMutableState {
        isPlaceholderAdded = false
        adapter.remove(provider.placeholder)
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

    private suspend fun turnMutableState(block: suspend () -> Unit) {
        disableModelListCallback()
        block()
        enableModelListCallback()
    }

    private fun enableModelListCallback() {
        modelList.addModelListCallback(this)
    }

    private fun disableModelListCallback() {
        modelList.removeModelListCallback(this)
    }

    protected fun isEmpty() = adapter.isEmpty()

    override val tag: String = "PlaceholderDataResolver"

    companion object {
        const val LAST_POSITION = -1
    }
}