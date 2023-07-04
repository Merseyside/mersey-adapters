package com.merseyside.adapters.core.feature.sorting.comparator

inline fun <reified T> compareByType(
    model1: Any,
    model2: Any,
    onSameTypes: (model1: T, model2: T) -> Int,
    onError: (model1: Any, model2: Any) -> Int
): Int {
    return if (model1 is T && model2 is T) onSameTypes(model1, model2)
    else onError(model1, model2)
}