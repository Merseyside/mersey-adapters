package com.merseyside.adapters.delegates.nestedDelegate

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.holder.TypedBindingHolder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.DelegateAdapter

abstract class NestedDelegateAdapter<Item : Parent, Parent, Model, Data, InnerAdapter>
    : DelegateAdapter<Item, Parent, Model>(),
    INestedDelegateAdapter<Item, Parent, Model, Data, InnerAdapter>
        where Model : NestedAdapterParentViewModel<Item, Parent, Data>,
              InnerAdapter : BaseAdapter<Data, out AdapterParentViewModel<out Data, Data>> {

    override val adapterList: MutableList<Pair<Model, InnerAdapter>> = ArrayList()

    @InternalAdaptersApi
    override fun onBindViewHolder(holder: TypedBindingHolder<Model>, model: Model, position: Int) {
        super.onBindViewHolder(holder, model, position)
        bindNestedAdapter(holder, model, position)
    }
}