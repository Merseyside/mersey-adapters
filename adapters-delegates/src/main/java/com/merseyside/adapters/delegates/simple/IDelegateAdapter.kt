package com.merseyside.adapters.delegates.simple

import com.merseyside.adapters.core.base.callback.click.HasOnItemClickListener
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.composites.CompositeAdapter

interface IDelegateAdapter<Item : Parent, Parent, Model : AdapterParentViewModel<Item, Parent>> :
    HasOnItemClickListener<Item> {

    @OptIn(InternalAdaptersApi::class)
    fun onModelCreated(
        model: Model,
        parentAdapter: CompositeAdapter<Parent, Model>
    ) {
        model.addOnClickListener(::onClick)
    }

    @InternalAdaptersApi
    fun onClick(item: Item) {
        notifyOnClick(item)
    }
}