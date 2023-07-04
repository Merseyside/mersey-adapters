@file:OptIn(InternalAdaptersApi::class)

package com.merseyside.adapters.core.nested

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.listManager.INestedModelListManager
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.merseyLib.kotlin.extensions.remove

interface INestedAdapter<Parent, Model, InnerData, InnerAdapter> : IBaseAdapter<Parent, Model>,
    NestedAdapterActions<Parent, Model, InnerData, InnerAdapter>, HasNestedAdapterListener<InnerData>
        where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
              InnerAdapter : BaseAdapter<InnerData, out AdapterParentViewModel<out InnerData, InnerData>> {

    override val adapterConfig: NestedAdapterConfig<Parent, Model, InnerData, InnerAdapter>

    var adapterList: MutableList<Pair<Model, InnerAdapter>>

    override val listManager: INestedModelListManager<Parent, Model, InnerData, InnerAdapter>
        get() = adapterConfig.listManager

    fun initNestedAdapter(model: Model): InnerAdapter
    fun getNestedView(holder: ViewHolder<Parent, Model>): RecyclerView?

    private fun internalInitInnerAdapter(model: Model): InnerAdapter {
        return initNestedAdapter(model).also { innerAdapter ->
            onInitAdapterListener?.onInitNestedAdapter(innerAdapter)
        }
    }

    suspend fun getNestedAdapterByItem(item: Parent): InnerAdapter? {
        val model = getModelByItem(item)
        return model?.let {
            getAdapterIfExists(it)
        }
    }

    private fun putAdapter(model: Model, adapter: InnerAdapter) {
        adapterList.add(model to adapter)
    }

    private fun getAdapterIfExists(model: Model): InnerAdapter? {
        return adapterList.find { it.first.areItemsTheSameInternal(model.item) }?.second
    }

    /* Models list actions */
    override fun getNestedAdapterByModel(model: Model): InnerAdapter? {
        return getAdapterIfExists(model)
    }

    override fun initNestedAdapterByModel(model: Model): InnerAdapter {
        return internalInitInnerAdapter(model).also { adapter ->
            putAdapter(model, adapter)
        }
    }

    override fun removeNestedAdapterByModel(model: Model): Boolean {
        return adapterList.remove { (adaptersModel, _) ->
            adaptersModel == model
        }
    }

//    override suspend fun clear() {
//        adapterList.clear()
//        super.clear()
//    }
}