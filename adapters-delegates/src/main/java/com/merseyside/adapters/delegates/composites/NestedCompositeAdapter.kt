package com.merseyside.adapters.delegates.composites

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.nested.INestedAdapter
import com.merseyside.adapters.core.nested.OnInitNestedAdapterListener
import com.merseyside.adapters.delegates.manager.SimpleDelegatesManager

abstract class NestedCompositeAdapter<Parent, Model, Data, InnerAdapter>(
    final override val adapterConfig: NestedAdapterConfig<Parent, Model, Data, InnerAdapter>,
    delegatesManager: SimpleDelegatesManager<Parent, Model> = SimpleDelegatesManager()
) : CompositeAdapter<Parent, Model>(adapterConfig, delegatesManager),
    INestedAdapter<Parent, Model, Data, InnerAdapter>
        where Model : NestedAdapterParentViewModel<out Parent, Parent, Data>,
              InnerAdapter : BaseAdapter<Data, out VM<Data>> {

    override var adapterMap: MutableMap<Any, InnerAdapter> = HashMap()
    override var onInitAdapterListener: OnInitNestedAdapterListener<Data>? = null

    override fun onBindViewHolder(holder: ViewHolder<Parent, Model>, position: Int) {
        super.onBindViewHolder(holder, position)

        getNestedView(holder)?.apply {
            val adapter = getNestedAdapterByModel(holder.model)
                ?: throw RuntimeException("Something went wrong!")

            if (this.adapter != adapter) {
                this.adapter = adapter
            }
        }
    }
}