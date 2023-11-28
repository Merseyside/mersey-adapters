package com.merseyside.adapters.core.base.ext

import com.merseyside.adapters.core.async.removeAsync
import com.merseyside.adapters.core.base.IBaseAdapter

inline fun <reified R: Parent, Parent> IBaseAdapter<Parent, *>.filterItemsIsInstance(): List<R> {
    return getAll().filterIsInstance<R>()
}

inline fun <reified R: Parent, Parent> IBaseAdapter<Parent, *>.findItemsIsInstance(): R {
    return filterItemsIsInstance<R, Parent>().first()
}

inline fun <Item> IBaseAdapter<Item, *>.findPosition(predicate: (item: Item) -> Boolean): Int {
    return getAll().find { predicate(it) }?.run {
        getPositionOfItem(this)
    } ?: -1
}

inline fun <Item> IBaseAdapter<Item, *>.findFirst(predicate: (item: Item) -> Boolean): Item? {
    return getAll().find { predicate(it) }
}

inline fun <Item> IBaseAdapter<Item, *>.findLast(predicate: (item: Item) -> Boolean): Item? {
    return getAll().findLast { predicate(it) }
}

inline fun <Item> IBaseAdapter<Item, *>.findAll(predicate: (Item) -> Boolean): List<Item> {
    val list = mutableListOf<Item>()
    getAll().forEach { if (predicate(it)) list.add(it) }

    return list
}

inline fun <Item> IBaseAdapter<Item, *>.removeIfAsync(predicate: (Item) -> Boolean): List<Item> {
    val itemsToRemove = findAll(predicate)
    removeAsync(itemsToRemove)
    return itemsToRemove
}