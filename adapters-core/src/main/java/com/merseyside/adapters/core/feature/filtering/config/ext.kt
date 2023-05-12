package com.merseyside.adapters.core.feature.filtering.config

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.contract.FilterProvider
import com.merseyside.adapters.core.feature.filtering.AdapterFilter
import com.merseyside.adapters.core.feature.filtering.AdapterQueryFilter
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> AdapterConfig<Parent, Model>.getAdapterFilter(): AdapterFilter<Parent, Model>? {
    return featureList.filterIsInstance<FilterProvider<Parent, Model>>()
        .firstOrNull()?.adapterFilter
}

fun <Parent, Model : VM<Parent>> AdapterConfig<Parent, Model>.getAdapterQueryFilter(): AdapterQueryFilter<Parent, Model>? {
    return featureList.filterIsInstance<FilterProvider<Parent, Model>>()
        .firstOrNull()?.adapterFilter as? AdapterQueryFilter<Parent, Model>
}