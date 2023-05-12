package com.merseyside.adapters.compose.view.card

import com.merseyside.adapters.compose.view.viewGroup.ComposingViewGroupViewModel

open class ComposingCardViewModel<Item : ComposingCard>(item: Item) :
    ComposingViewGroupViewModel<Item>(item)