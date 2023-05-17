package com.merseyside.adapters.delegates.nestedDelegate

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.manager.DelegatesManager
import com.merseyside.merseyLib.kotlin.extensions.remove

interface INestedDelegateAdapter<Item : Parent, Parent, Model, Data, InnerAdapter>
        where Model : NestedAdapterParentViewModel<Item, Parent, out Data>,
              InnerAdapter : BaseAdapter<Data, out VM<Data>> {

    var delegatesManagerProvider: () -> DelegatesManager<*, *, *>

    val adapterList: MutableList<Pair<Model, InnerAdapter>>

    fun createNestedAdapter(model: Model): InnerAdapter

    fun getNestedRecyclerView(holder: ViewHolder<Parent, Model>, model: Model): RecyclerView?

    fun removeNestedAdapterByModel(model: Model): Boolean {
        return adapterList.remove { (adaptersModel, _) ->
            adaptersModel == model
        }
    }

    @InternalAdaptersApi
    fun onModelUpdated(model: Model) {
        val adapter = getNestedAdapterByModel(model)
        setInnerData(adapter, model)
    }

    @InternalAdaptersApi
    private fun getNestedAdapterByModel(model: Model): InnerAdapter {
        return getAdapterIfExists(model) ?: createNestedAdapter(model)
            .also { adapter -> putAdapter(model, adapter) }
    }

    @InternalAdaptersApi
    private fun getAdapterIfExists(model: Model): InnerAdapter? {
        return adapterList.find { it.first.areItemsTheSameInternal(model.item) }?.second
    }

    private fun putAdapter(model: Model, adapter: InnerAdapter) {
        adapterList.add(model to adapter)
    }

    private fun setInnerData(adapter: InnerAdapter, model: Model) {
        model.getNestedData()?.let { data ->
            handleInnerData(adapter, data)
        }
    }

    fun handleInnerData(adapter: InnerAdapter, data: List<Data>) {
        adapter.updateAsync(data)
    }

    @InternalAdaptersApi
    fun onBindNestedAdapter(holder: ViewHolder<Parent, Model>, model: Model, position: Int) {
        getNestedRecyclerView(holder, model)?.apply {
            val adapter = getNestedAdapterByModel(model)
            setInnerData(adapter, model)
            if (this.adapter != adapter) {
                this.adapter = adapter
            }
        }
    }
}