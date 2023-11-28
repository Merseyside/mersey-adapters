package com.merseyside.adapters.compose.feature.placeholder

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.delegates.simple.DelegateAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider

class ComposePlaceholderProvider<Item : SCV>(
    private val provideView: () -> Item
) : PlaceholderProvider<Item, SCV>() {

    override val placeholder: Item = provideView()

    override val placeholderDelegate = placeholder.delegate
            as DelegateAdapter<Item, SCV, out AdapterParentViewModel<Item, SCV>>
}