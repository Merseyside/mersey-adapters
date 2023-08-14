package com.merseyside.adapters.core.feature.sorting.comparator

import com.merseyside.adapters.core.feature.sorting.SortFeature
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.utils.InternalAdaptersApi

inline fun <Parent, reified Model : VM<Parent>> SortFeature.Config<Parent, Model>.Comparator(
    noinline compare: (model1: Model, model2: Model) -> Int
): Comparator<Parent, Model> {

    @OptIn(InternalAdaptersApi::class)
    modelClass = Model::class.java
    comparator = object : Comparator<Parent, Model>() {
        override fun compare(model1: Model, model2: Model): Int {
            return compare(model1, model2)
        }
    }

    return comparator
}