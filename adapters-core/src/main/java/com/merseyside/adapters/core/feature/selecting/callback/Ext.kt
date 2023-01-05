package com.merseyside.adapters.core.feature.selecting.callback

import com.merseyside.adapters.core.feature.selecting.AdapterSelect

fun <Item> HasOnItemSelectedListener<Item>.onItemSelected(
    onSelected: (
        item: Item,
        isSelected: Boolean,
        isSelectedByUser: Boolean
    ) -> Unit
): OnItemSelectedListener<Item> {
    val listener = object : OnItemSelectedListener<Item> {
        override fun onSelected(item: Item, isSelected: Boolean, isSelectedByUser: Boolean) {
            onSelected.invoke(item, isSelected, isSelectedByUser)
        }

        override fun onSelectedRemoved(
            adapterList: AdapterSelect<Item, *>,
            items: List<Item>
        ) {}
    }
    addOnItemSelectedListener(listener)

    return listener
}