package com.merseyside.adapters

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.NestedAdapterViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.nested.INestedAdapter
import com.merseyside.adapters.core.nested.OnInitNestedAdapterListener
import java.util.Collections

abstract class NestedAdapter<Item, Model, Data, InnerAdapter>(
    final override val adapterConfig: NestedAdapterConfig<Item, Model, Data, InnerAdapter>
) : SimpleAdapter<Item, Model>(adapterConfig), INestedAdapter<Item, Model, Data, InnerAdapter>
        where Model : NestedAdapterViewModel<Item, Data>,
              InnerAdapter : BaseAdapter<Data, out VM<Data>> {

    override var adapterMap: MutableMap<Any, InnerAdapter> = HashMap()
    override var onInitAdapterListener: OnInitNestedAdapterListener<Data>? = null

    override fun onBindViewHolder(holder: ViewHolder<Item, Model>, position: Int) {
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