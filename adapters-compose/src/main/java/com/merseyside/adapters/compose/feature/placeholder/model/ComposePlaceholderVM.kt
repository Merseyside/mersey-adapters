package com.merseyside.adapters.compose.feature.placeholder.model

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.AdapterParentViewModel

abstract class ComposePlaceholderVM<Item : SCV>(item: Item) : AdapterParentViewModel<Item, SCV>(
    item = item,
    deletable = false,
    filterable = false
)