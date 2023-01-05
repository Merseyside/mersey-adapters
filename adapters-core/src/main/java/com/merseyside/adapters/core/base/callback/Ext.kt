package com.merseyside.adapters.core.base.callback

fun <Item> HasOnItemClickListener<Item>.onClick(
    onClick: (item: Item) -> Unit
): OnItemClickListener<Item> {
    val listener = object : OnItemClickListener<Item> {
        override fun onItemClicked(item: Item) {
            onClick.invoke(item)
        }
    }
    setOnItemClickListener(listener)

    return listener
}