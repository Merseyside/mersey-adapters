package com.merseyside.adapters.core.feature.expanding.callback

fun <Item> HasOnItemExpandedListener<Item>.onItemExpanded(
    onExpanded: (
        item: Item,
        isExpanded: Boolean,
        isExpandedByUser: Boolean
    ) -> Unit
): OnItemExpandedListener<Item> {
    val listener = object : OnItemExpandedListener<Item> {
        override fun onExpanded(
            item: Item,
            isExpanded: Boolean,
            isExpandedByUser: Boolean
        ) {
            onExpanded(item, isExpanded, isExpandedByUser)
        }
    }

    addOnItemExpandedListener(listener)
    return listener
}