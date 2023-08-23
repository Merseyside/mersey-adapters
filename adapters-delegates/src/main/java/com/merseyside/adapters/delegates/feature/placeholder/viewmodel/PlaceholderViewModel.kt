package com.merseyside.adapters.delegates.feature.placeholder.viewmodel

import com.merseyside.adapters.core.model.AdapterParentViewModel

open class PlaceholderViewModel<Item : Any>(item: Item) : AdapterParentViewModel<Item, Any>(
    item = item,
    deletable = false,
    filterable = false
)