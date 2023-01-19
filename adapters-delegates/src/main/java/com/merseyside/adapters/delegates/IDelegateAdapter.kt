package com.merseyside.adapters.delegates

import com.merseyside.adapters.core.base.callback.HasOnItemClickListener
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi

interface IDelegateAdapter<Item : Parent, Parent, Model> : HasOnItemClickListener<Item>
        where Model : AdapterParentViewModel<Item, Parent> {

    @InternalAdaptersApi
    val onClick: (Item) -> Unit

    fun getBindingVariable(): Int
}