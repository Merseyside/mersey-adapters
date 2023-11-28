package com.merseyside.adapters.core.feature.sorting.comparator

import com.merseyside.adapters.core.model.VM

context(Comparator<*, *>)
inline fun <reified T> compareByType(
    model1: VM<*>,
    model2: VM<*>,
    onSameTypes: (model1: T, model2: T) -> Int
): Int? {
    return if (model1 is T && model2 is T) onSameTypes(model1, model2)
    else null
}