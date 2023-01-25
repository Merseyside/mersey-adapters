package com.merseyside.adapters.core.feature.selecting.ext

import com.merseyside.adapters.core.feature.selecting.AdapterSelect
import com.merseyside.adapters.core.model.VM


fun AdapterSelect<*, *>.clearAsync(onComplete: (Unit) -> Unit = {}) {
    workManager.doAsync(onComplete) { clear() }
}

fun <Item, Model : VM<Item>> AdapterSelect<Item, Model>.selectFirstIfAsync(
    predicate: (Model) -> Boolean,
    onComplete: (Item?) -> Unit = {}
) {
    workManager.doAsync(onComplete) {
       selectFirstIf(predicate)
    }
}