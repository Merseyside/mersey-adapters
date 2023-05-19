package com.merseyside.adapters.delegates.feature.placeholder.resolver

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.doAsync
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.ModelListCallback
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.utils.safeLet

abstract class PlaceholderDataResolver<Parent, ParentModel : VM<Parent>> :
    ModelListCallback<ParentModel>, ILogger {

    private lateinit var modelList: ModelList<Parent, ParentModel>
    private lateinit var provider: PlaceholderProvider<Parent, ParentModel>
    protected lateinit var adapter: CompositeAdapter<Parent, out ParentModel>

    var isPlaceholderAdded = false
        private set

    fun setProvider(provider: PlaceholderProvider<Parent, ParentModel>) {
        this.provider = provider
    }

    fun initAdapter(adapter: CompositeAdapter<Parent, ParentModel>) {
        this.adapter = adapter
        modelList = adapter.adapterConfig.modelList
        safeLet(provider.placeholderDelegate) {
            adapter.delegatesManager.addDelegates(it)
        }

        adapter.addOnAttachToRecyclerViewListener(onAttachListener)
    }

    abstract fun getPlaceholderPosition(adapter: CompositeAdapter<Parent, out ParentModel>): Int

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

    protected fun addPlaceholderAsync(position: Int = getPlaceholderPosition(adapter)) {
        adapter.doAsync { addPlaceholder(position) }
    }

    protected suspend fun addPlaceholder(position: Int = getPlaceholderPosition(adapter)) =
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
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") ignored: BaseAdapter<*, *>
        ) {
            onAdapterAttached(adapter)
        }

        override fun onDetached(recyclerView: RecyclerView, adapter: BaseAdapter<*, *>) {
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

    companion object {
        const val LAST_POSITION = -1
    }
}