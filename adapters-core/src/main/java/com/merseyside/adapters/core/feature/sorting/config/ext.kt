package com.merseyside.adapters.core.feature.sorting.config

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.feature.sorting.comparator.Comparator
import com.merseyside.adapters.core.feature.sorting.comparator.ComparatorProvider
import com.merseyside.adapters.core.feature.sorting.comparator.HasItemComparator
import com.merseyside.adapters.core.feature.sorting.comparator.ItemComparator
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> AdapterConfig<Parent, Model>.getAdapterComparator()
        : Comparator<Parent, Model>? {
    return featureList.filterIsInstance<ComparatorProvider<Parent, Model>>()
        .firstOrNull()?.comparator
}

fun <Parent, Model : VM<Parent>> AdapterConfig<Parent, Model>.getItemComparators()
        : List<ItemComparator<out Parent, Parent, out Model>> {
    return featureList.filterIsInstance<HasItemComparator<Parent, Model>>()
        .map { it.getItemComparator() }
}