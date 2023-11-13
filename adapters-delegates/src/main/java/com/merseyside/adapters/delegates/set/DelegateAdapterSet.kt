package com.merseyside.adapters.delegates.set

import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.delegates.simple.DelegateAdapter

abstract class DelegateAdapterSet<DA : DelegateAdapter<Item, Parent, Model>, Item : Parent, Parent, Model>
        where Model : AdapterParentViewModel<Item, Parent> {

    abstract fun getDelegates(): List<DA>
}

typealias SimpleDelegateAdapterSet<DA, Item, Model> = DelegateAdapterSet<DA, Item, Any, Model>