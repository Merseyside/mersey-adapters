package com.merseyside.adapters.core.feature.selecting.ext

import com.merseyside.adapters.core.feature.selecting.AdapterSelect
import com.merseyside.adapters.core.model.VM

fun <Item, Model : VM<Item>> AdapterSelect<Item, Model>.selectFirstIf(predicate: (Model) -> Boolean): Item? {
    modelList.forEach {
        if (predicate(it) && selectItem(it.item)) return it.item
    }

    return null
}