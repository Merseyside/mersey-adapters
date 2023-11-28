@file:OptIn(InternalAdaptersApi::class)

package com.merseyside.adapters.core.nested

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.listManager.INestedModelListManager
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.utils.InternalAdaptersApi

interface INestedAdapter<Parent, Model, InnerData, InnerAdapter> : IBaseAdapter<Parent, Model>,
    NestedAdapterActions<Parent, Model, InnerData, InnerAdapter>, HasNestedAdapterListener<InnerData>
        where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
              InnerAdapter : BaseAdapter<InnerData, out VM<InnerData>> {

    override val adapterConfig: NestedAdapterConfig<Parent, Model, InnerData, InnerAdapter>

    var adapterMap: MutableMap<Any, InnerAdapter>

    override val listManager: INestedModelListManager<Parent, Model, InnerData, InnerAdapter>
        get() = adapterConfig.listManager

    fun initNestedAdapter(model: Model): InnerAdapter
    fun getNestedView(holder: ViewHolder<Parent, Model>): RecyclerView?

    override fun initNestedAdapterByModel(model: Model): InnerAdapter {
        return initNestedAdapter(model).also { innerAdapter ->
            onInitAdapterListener?.onInitNestedAdapter(innerAdapter)
            putAdapter(model, innerAdapter)
        }
    }

    suspend fun getNestedAdapterByItem(item: Parent): InnerAdapter? {
        val model = getModelByItem(item)
        return model?.let {
            getAdapterIfExists(it)
        }
    }

    private fun putAdapter(model: Model, adapter: InnerAdapter) {
        adapterMap[model.id] = adapter
    }

    private fun getAdapterIfExists(model: Model): InnerAdapter? {
        return adapterMap[model.id]
    }

    /* Models list actions */
    override fun getNestedAdapterByModel(model: Model): InnerAdapter? {
        return getAdapterIfExists(model)
    }

    override fun removeNestedAdapterByModel(model: Model): Boolean {
        return adapterMap.remove(model.id) != null
    }

//    override suspend fun clear() {
//        adapterList.clear()
//        super.clear()
//    }
}