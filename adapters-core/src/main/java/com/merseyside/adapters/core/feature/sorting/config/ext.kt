package com.merseyside.adapters.core.feature.sorting.config

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.feature.sorting.Comparator
import com.merseyside.adapters.core.feature.sorting.ComparatorProvider
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> AdapterConfig<Parent, Model>.getAdapterComparator(): Comparator<Parent, Model>? {
    return featureList.filterIsInstance<ComparatorProvider<Parent, Model>>()
        .firstOrNull()?.comparator
}