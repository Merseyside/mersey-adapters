package com.merseyside.adapters

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.config.nestedConfig
import com.merseyside.adapters.core.holder.TypedBindingHolder
import com.merseyside.adapters.core.model.NestedAdapterViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.nested.INestedAdapter
import com.merseyside.adapters.core.nested.OnInitNestedAdapterListener

abstract class NestedAdapter<Item, Model, Data, InnerAdapter>(
    final override val adapterConfig: NestedAdapterConfig<Item, Model, Data, InnerAdapter>
) : SimpleAdapter<Item, Model>(adapterConfig),
    INestedAdapter<Item, Model, Data, InnerAdapter>
        where Model : NestedAdapterViewModel<Item, Data>,
              InnerAdapter : BaseAdapter<Data, out VM<Data>> {

    override var adapterList: MutableList<Pair<Model, InnerAdapter>> = ArrayList()
    override var onInitAdapterListener: OnInitNestedAdapterListener<Data>? = null

    override fun onBindViewHolder(holder: TypedBindingHolder<Model>, position: Int) {
        super.onBindViewHolder(holder, position)
        val model = getModelByPosition(position)

        getNestedView(holder.binding)?.apply {
            val adapter = getNestedAdapterByModel(model)
            if (this.adapter != adapter) {
                this.adapter = adapter
            }
        }
    }
}