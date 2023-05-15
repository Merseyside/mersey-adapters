package com.merseyside.adapters.delegates.nestedDelegate

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.holder.TypedBindingHolder
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.IDelegateAdapter
import com.merseyside.adapters.delegates.manager.DelegatesManager
import com.merseyside.merseyLib.kotlin.extensions.remove
import com.merseyside.merseyLib.kotlin.logger.log

interface INestedDelegateAdapter<Item : Parent, Parent, Model, Data, InnerAdapter>
    : IDelegateAdapter<Item, Parent, Model>
        where Model : NestedAdapterParentViewModel<Item, Parent, out Data>,
              InnerAdapter : BaseAdapter<Data, out VM<Data>> {

    var delegatesManagerProvider: () -> DelegatesManager<*, *, *>

    val adapterList: MutableList<Pair<Model, InnerAdapter>>

    fun createNestedAdapter(model: Model): InnerAdapter

    fun getNestedRecyclerView(binding: ViewDataBinding, model: Model): RecyclerView?

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
    fun onBindNestedAdapter(holder: TypedBindingHolder<Model>, model: Model, position: Int) {
        "bind nested adapter".log("kek")
        getNestedRecyclerView(holder.binding, model)?.apply {
            val adapter = getNestedAdapterByModel(model)
            setInnerData(adapter, model)
            if (this.adapter != adapter) {
                this.adapter = adapter
            }
        }
    }
}