package com.merseyside.adapters.core.base.callback.click

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.utils.InternalAdaptersApi

fun interface OnItemClickListener<Item> {

    fun onItemClicked(
        item: Item,
        adapter: BaseAdapter<Item, *>?
    )
}

interface HasOnItemClickListener<Item> {
    val adapter: BaseAdapter<Item, *>?
        get() = null

    val clickListeners: MutableList<OnItemClickListener<Item>>

    fun setOnItemClickListener(listener: OnItemClickListener<Item>) {
        clickListeners.add(listener)
    }

    fun removeOnItemClickListener(listener: OnItemClickListener<Item>) {
        clickListeners.remove(listener)
    }

    @InternalAdaptersApi
    fun notifyOnClick(item: Item) {
        clickListeners.forEach { listener -> listener.onItemClicked(item, adapter) }
    }

    fun removeAllClickListeners() {
        clickListeners.clear()
    }
}