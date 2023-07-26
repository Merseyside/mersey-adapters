package com.merseyside.adapters.delegates.feature.placeholder.provider

import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.DelegateAdapter

abstract class PlaceholderProvider<Item : Parent, Parent> {

    abstract val placeholder: Item
    abstract val placeholderDelegate: DelegateAdapter<Item, Parent, out AdapterParentViewModel<Item, Parent>>?
}