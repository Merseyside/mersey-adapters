package com.merseyside.adapters.core.feature.dataProvider

import com.merseyside.adapters.core.base.IBaseAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@JvmName("singleDataProvider")
fun <Parent> IBaseAdapter<Parent, *>.dataProvider(
    providerFlow: Flow<*>,
    observeWhenAttached: Boolean = false
): DataProvider<Parent> {
    return DataProvider(
        this,
        providerFlow,
        observeWhenAttached
    )
}

fun <Data, Parent> IBaseAdapter<Parent, *>.dataProvider(
    providerListFlow: Flow<Data>,
    observeWhenAttached: Boolean = false,
    mapper: (Data) -> Parent
): DataProvider<Parent> {
    return DataProvider(
        this,
        providerListFlow.map(mapper),
        observeWhenAttached
    )
}