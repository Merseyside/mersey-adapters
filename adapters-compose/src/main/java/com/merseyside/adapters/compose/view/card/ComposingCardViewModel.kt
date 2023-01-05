package com.merseyside.adapters.compose.view.card

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel

open class ComposingCardViewModel<Item : ComposingCard>(item: Item) :
    NestedAdapterParentViewModel<Item, SCV, SCV>(item) {

    override fun getNestedData() = item.viewList
}