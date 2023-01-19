package com.merseyside.adapters.compose.view.button

import com.merseyside.adapters.compose.view.text.ComposingTextViewModel

open class ComposingButtonViewModel<Item : ComposingButton<*>>(
    item: Item
) : ComposingTextViewModel<Item>(item)