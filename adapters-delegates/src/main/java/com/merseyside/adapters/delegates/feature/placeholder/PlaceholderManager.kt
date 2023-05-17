package com.merseyside.adapters.delegates.feature.placeholder

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.doAsync
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelListCallback
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider
import com.merseyside.adapters.delegates.feature.resolver.PlaceholderDataResolver
import com.merseyside.merseyLib.kotlin.utils.safeLet

class PlaceholderManager<Parent, ParentModel : VM<Parent>>(
    private val modelList: ModelList<Parent, ParentModel>,
    private val provider: PlaceholderProvider<Parent, ParentModel>,
    private val resolver: PlaceholderDataResolver
) : ModelListCallback<ParentModel> {

    private lateinit var adapter: CompositeAdapter<Parent, out VM<Parent>>

    private var isPlaceholderAdded = false

    private val size: Int
        get() = modelList.size

    private suspend fun onDataChanged(size: Int) {
        if (resolver.isPlaceholderVisible(size)) {
            if (!isPlaceholderAdded) {
                val position = resolver.getPlaceholderPosition()
                addPlaceholderToPosition(position)
            }
        } else {
            if (isPlaceholderAdded) {
                removePlaceholder()
            }
        }
    }

    private suspend fun removePlaceholder() {
        isPlaceholderAdded = false
        adapter.remove(provider.placeholder)
    }

    private suspend fun addPlaceholderToPosition(position: Int) {
        isPlaceholderAdded = true
        if (position == PlaceholderDataResolver.LAST_POSITION) adapter.add(provider.placeholder)
        else adapter.add(position, provider.placeholder)
    }

    override suspend fun onInsert(models: List<ParentModel>, count: Int) {
        turnMutableState {
            onDataChanged(models.size)
        }
    }

    override suspend fun onInserted(models: List<ParentModel>, position: Int, count: Int) {}

    override suspend fun onRemoved(models: List<ParentModel>, position: Int, count: Int) {
        turnMutableState {
            onDataChanged(size)
        }
    }

    override suspend fun onChanged(
        model: ParentModel,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
    }

    override suspend fun onMoved(fromPosition: Int, toPosition: Int) {}

    override suspend fun onCleared() {}

    internal fun initAdapter(adapter: CompositeAdapter<Parent, ParentModel>) {
        this.adapter = adapter
        safeLet(provider.placeholderDelegate) {
            adapter.delegatesManager.addDelegates(it)
        }

        adapter.addOnAttachToRecyclerViewListener(object : OnAttachToRecyclerViewListener {
            override fun onAttached(recyclerView: RecyclerView, adapter: BaseAdapter<*, *>) {
                if (resolver.isShowPlaceholderOnAttach()) {
                    if (resolver.isPlaceholderVisible(size)) {
                        val position = resolver.getPlaceholderPosition()
                        this@PlaceholderManager.adapter.doAsync {
                            addPlaceholderToPosition(position)
                            enableModelListCallback()
                        }
                    }
                } else {
                    enableModelListCallback()
                }
            }

            override fun onDetached(recyclerView: RecyclerView, adapter: BaseAdapter<*, *>) {
                disableModelListCallback()
            }
        })
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
        modelList.removeModelListCallback(this@PlaceholderManager)
    }
}