package com.merseyside.adapters.core.config.ext

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.contract.FilterProvider
import com.merseyside.adapters.core.config.feature.Feature
import com.merseyside.adapters.core.feature.filtering.AdapterFilter
import com.merseyside.adapters.core.feature.filtering.AdapterQueryFilter
import com.merseyside.adapters.core.feature.selecting.AdapterSelect
import com.merseyside.adapters.core.feature.selecting.SelectProvider
import com.merseyside.adapters.core.feature.sorting.ComparatorProvider
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.feature.sorting.Comparator

fun AdapterConfig<*, *>.hasFeature(featureKey: String): Boolean {
    return featureList.find { it.featureKey == featureKey } != null
}

fun <Parent> AdapterConfig<Parent, *>.getFeatureByKey(featureKey: String): Feature<Parent, *>? {
    return featureList.find { it.featureKey == featureKey }
}

fun <Parent, F : Feature<Parent, *>>
        AdapterConfig<Parent, *>.getFeatureByInstance(clazz: Class<F>): F? {
    return featureList.filterIsInstance(clazz).firstOrNull()
}