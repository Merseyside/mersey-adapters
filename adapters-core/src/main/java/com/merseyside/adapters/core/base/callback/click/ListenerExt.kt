package com.merseyside.adapters.core.base.callback.click

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.model.AdapterParentViewModel


fun <Item> HasOnItemClickListener<Item>.onClick(
    onClick: (item: Item) -> Unit
): OnItemClickListener<Item> {
    val listener =
        OnItemClickListener { item, _ -> onClick(item) }
    setOnItemClickListener(listener)

    return listener
}

fun <Item> HasOnItemClickListener<Item>.adapterOnClick(
    onClickAdapter: (
        adapter: BaseAdapter<Item, out AdapterParentViewModel<out Item, Item>>, item: Item,
    ) -> Unit,
): OnItemClickListener<Item> {
    val listener =
        OnItemClickListener<Item> { item, adapter ->
            if (adapter != null) {
                onClickAdapter(adapter, item)
            } else throw UnsupportedOperationException()
        }
    setOnItemClickListener(listener)

    return listener
}